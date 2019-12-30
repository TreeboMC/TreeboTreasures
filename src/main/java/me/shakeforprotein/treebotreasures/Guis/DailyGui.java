package me.shakeforprotein.treebotreasures.Guis;

import me.shakeforprotein.treebotreasures.TreeboTreasures;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DailyGui {

    private TreeboTreasures pl;

    public DailyGui(TreeboTreasures main) {
        this.pl = main;
    }

    public void dailyGui(Player p) {

        File dailyGuiFile = new File(pl.getDataFolder(), "dailyRewards.yml");
        YamlConfiguration dailyYml = YamlConfiguration.loadConfiguration(dailyGuiFile);
        File playerFile = new File(pl.getDataFolder() + File.separator + "playerFiles", p.getUniqueId() + ".yml");
        YamlConfiguration playerYml = YamlConfiguration.loadConfiguration(playerFile);
        boolean allClaimed = true;

        int rows = dailyYml.getInt("gui.rows");
        String title = ChatColor.translateAlternateColorCodes('&', dailyYml.getString("gui.title") + "");

        Inventory thisInv = Bukkit.createInventory(null, rows * 9, title);
        for (int slot = 0; slot < thisInv.getSize(); slot++) {
            ItemStack fillerItem = new ItemStack(Material.valueOf(dailyYml.getString("gui.filler")));
            ItemMeta fillMeta = fillerItem.getItemMeta();
            fillMeta.setDisplayName(" ");
            fillerItem.setItemMeta(fillMeta);
            thisInv.setItem(slot, fillerItem);
        }
        for (String menuItem : dailyYml.getConfigurationSection("gui.items").getKeys(false)) {
            int position = dailyYml.getInt("gui.items." + menuItem + ".Slot");
            ItemStack newItem;

            if (playerYml.get("claimed." + menuItem) == null) {
                playerYml.set("claimed." + menuItem, false);
                try {
                    playerYml.save(playerFile);
                } catch (IOException err) {
                    err.printStackTrace();
                }
            }
            if (playerYml.getInt("streak") < dailyYml.getInt("gui.items." + menuItem + ".DaysUntilPlayerCanClaim")) {
                playerYml.set("claimed." + menuItem, false);
            }
            if (!playerYml.getBoolean("claimed." + menuItem)) {
                allClaimed = false;
            }


            if (!playerYml.getBoolean("claimed." + menuItem) && playerYml.getInt("streak") >= dailyYml.getInt("gui.items." + menuItem + ".DaysUntilPlayerCanClaim") && (dailyYml.getString("gui.items." + menuItem + ".PermissionNeeded") == null || p.hasPermission(dailyYml.getString("gui.items." + menuItem + ".PermissionNeeded")))) {
                    newItem = new ItemStack(Material.valueOf(dailyYml.getString("gui.items." + menuItem + ".ActiveItem")), dailyYml.getInt("gui.items." + menuItem + ".ActiveItemAmount"));
                    ItemMeta newMeta = newItem.getItemMeta();
                    List<String> newLore = new ArrayList<>();
                    newMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', dailyYml.getString("gui.items." + menuItem + ".Heading")));
                    String configLore = dailyYml.getString("gui.items." + menuItem + ".ActiveLore").replace("some days", playerYml.getInt("streak") + " days");
                    for (String txt : configLore.split(";")) {
                        newLore.add(ChatColor.translateAlternateColorCodes('&', txt.replace(";", "\n")));
                    }
                    newLore.add("");
                    newLore.add(ChatColor.translateAlternateColorCodes('&', dailyYml.getString("gui.items." + menuItem + ".CanBeClaimedFormat")));
                    newMeta.setLore(newLore);
                    newItem.setItemMeta(newMeta);
                    addGlow(newItem);
            } else {
                newItem = new ItemStack(Material.valueOf(dailyYml.getString("gui.items." + menuItem + ".InactiveItem")), dailyYml.getInt("gui.items." + menuItem + ".InactiveItemAmount"));
                ItemMeta newMeta = newItem.getItemMeta();
                List<String> newLore = new ArrayList<>();

                newMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', dailyYml.getString("gui.items." + menuItem + ".Heading")));
                String configLore = dailyYml.getString("gui.items." + menuItem + ".InactiveLore").replace("some days", playerYml.getInt("streak") + " days").replace("%days", (1111) + "");
                for (String txt : configLore.split(";")) {
                    newLore.add(ChatColor.translateAlternateColorCodes('&', txt));
                }
                newLore.add("");
                int totalDays = dailyYml.getInt("gui.items." + menuItem + ".DaysUntilPlayerCanClaim") - playerYml.getInt("streak");
                if (totalDays < 1) {
                    newLore.add(ChatColor.DARK_RED + "You have already claimed this reward");

                } else {
                    String dayString = "";
                    if (totalDays == 1) {
                        long now = System.currentTimeMillis();
                        long configTime = playerYml.getLong("lastToken");
                        dayString = getDurationBreakdown(86400000 - (now - configTime));
                    } else {
                        dayString = totalDays + "";
                    }
                    newLore.add(ChatColor.translateAlternateColorCodes('&', dailyYml.getString("gui.items." + menuItem + ".TimeToClaimFormat").replace("%days", dayString + "")));
                }
                newMeta.setLore(newLore);
                newItem.setItemMeta(newMeta);

            }
            thisInv.setItem(position, newItem);


        }
        if (allClaimed) {
            playerYml.set("claimed", null);
            playerYml.set("streak", 0);
            //playerYml.set("lastToken", System.currentTimeMillis());
            try {
                playerYml.save(playerFile);
            } catch (IOException err) {
                err.printStackTrace();
            }
        }
        for (int slot = 0; slot < thisInv.getSize(); slot++) {
            if (thisInv.getItem(slot).getType() == Material.AIR) {
                ItemStack airItem = new ItemStack(Material.AIR);
                thisInv.setItem(slot, airItem);
            }
        }

        p.openInventory(thisInv);
    }

    public void addGlow(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        meta.addEnchant(Enchantment.LURE, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        stack.setItemMeta(meta);
    }

    public static String getDurationBreakdown(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);
        sb.append(days);
        sb.append(" Days ");
        sb.append(hours);
        sb.append(" Hours ");
        sb.append(minutes);
        sb.append(" Minutes ");
        sb.append(seconds);
        sb.append(" Seconds");

        return (sb.toString());
    }
}

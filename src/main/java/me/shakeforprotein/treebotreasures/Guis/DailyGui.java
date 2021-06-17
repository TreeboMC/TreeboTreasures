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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class DailyGui {

    private TreeboTreasures pl;
    private File dailyGuiFile;
    private YamlConfiguration dailyYml;

    public DailyGui(TreeboTreasures main) {
        this.pl = main;

        dailyGuiFile = new File(pl.getDataFolder(), "dailyRewards.yml");
        dailyYml = YamlConfiguration.loadConfiguration(dailyGuiFile);
    }

    public void dailyGui(Player p) {

        File playerFile = new File(pl.playerDataFolder + File.separator + p.getUniqueId().toString(), "TTreasures_DailyReward" + ".yml");
        YamlConfiguration playerYml = YamlConfiguration.loadConfiguration(playerFile);
        Inventory dailyInventory = prepareInventory(p, playerYml);

        p.openInventory(dailyInventory);
    }


    private Inventory prepareInventory(Player p, YamlConfiguration playerYml){
        int invSize = dailyYml.getInt("gui.rows") * 9;
        String title = ChatColor.translateAlternateColorCodes('&', dailyYml.getString("gui.title") + "");

        Inventory newInv = Bukkit.createInventory(null, invSize, title);

        for(String key: dailyYml.getConfigurationSection("gui.items").getKeys(false)){
            int slot = dailyYml.getInt("gui.items." + key + ".Slot");
            newInv.setItem(slot, getItem(key, p, playerYml));
        }

        return newInv;
    }

    private ItemStack getItem(String key, Player p, YamlConfiguration playerYml){
        Material theMaterial = Material.valueOf(dailyYml.getString("gui.items." + key + ".InactiveItem"));
        ItemStack theItem = new ItemStack(theMaterial);
        String shortKey = "gui.items." + key;
        if(isAllowed(p, key, playerYml)){
            theMaterial = Material.valueOf(dailyYml.getString(shortKey + ".ActiveItem"));
            theItem.setType(theMaterial);
            theItem = setLore(key, theItem, true, playerYml);
        } else{
            theItem = setLore(key, theItem, false, playerYml);
        }


        return theItem;
    }

    private boolean isAllowed (Player p, String key, YamlConfiguration playerYml){
        String shortKey = "gui.items." + key;
        int requiredStreak = dailyYml.getInt(shortKey + ".RequiredStreak");
        int playerStreak = playerYml.getInt("streak");
        String requiredPermission = dailyYml.getString(shortKey + ".RequiredPermission");
        if(requiredPermission == null || requiredPermission.equals("")){
            requiredPermission = "nte.member";
        }
        if(p.hasPermission(requiredPermission)
                && playerStreak >= requiredStreak
                && TimeUnit.MILLISECONDS.toDays((System.currentTimeMillis() - playerYml.getLong("claimed." + key))) >= dailyYml.getInt(shortKey + ".DaysUntilPlayerCanClaim")
                && TimeUnit.MILLISECONDS.toDays((System.currentTimeMillis())) - TimeUnit.MILLISECONDS.toDays(playerYml.getLong("claimed." + key)) >= 1){
            return true;
        }
        return false;
    }

    private ItemStack setLore(String key, ItemStack item, boolean isActive, YamlConfiguration playerYml){
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', dailyYml.getString("gui.items." + key + ".Heading")));
        List<String> newLore = new ArrayList<>();
        if(isActive){
            String configLore = dailyYml.getString("gui.items." + key + ".ActiveLore").replace("some days", playerYml.getInt("streak") + " days");
            for (String txt : configLore.split(";")) {
                newLore.add(ChatColor.translateAlternateColorCodes('&', txt.replace(";", "\n")));
            }
            newLore.add("");
            newLore.add(ChatColor.translateAlternateColorCodes('&', dailyYml.getString("gui.items." + key + ".CanBeClaimedFormat")));
        }
        else{
            String configLore = dailyYml.getString("gui.items." + key + ".InactiveLore").replace("some days", playerYml.getInt("streak") + " days").replace("%days", (1111) + "");
            for (String txt : configLore.split(";")) {
                newLore.add(ChatColor.translateAlternateColorCodes('&', txt));
            }
            newLore.add("");
            int totalDays = dailyYml.getInt("gui.items." + key + ".StreakRequired") - playerYml.getInt("streak");
            if (totalDays < 1) {
                newLore.add(ChatColor.DARK_RED + "You Cannot claim this reward at this time.");
            }
        }
        meta.setLore(newLore);
        meta.addEnchant(Enchantment.LURE, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);

        return item;
    }
}

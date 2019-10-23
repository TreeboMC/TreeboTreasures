package me.shakeforprotein.treebotreasures.Listeners;

import me.shakeforprotein.treebotreasures.Commands.DailyReward;
import me.shakeforprotein.treebotreasures.Guis.DailyGui;
import me.shakeforprotein.treebotreasures.Guis.RewardsGui;
import me.shakeforprotein.treebotreasures.TreeboTreasures;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;

public class TicketSelectGuiListener implements Listener {

    private TreeboTreasures pl;
    private RewardsGui rewardsGui;
    private DailyGui dailyGui;

    public TicketSelectGuiListener(TreeboTreasures main) {
        this.pl = main;
        this.rewardsGui = new RewardsGui(pl);
        this.dailyGui = new DailyGui(pl);
    }

    @EventHandler
    private void useRewardsGui(InventoryClickEvent e) {
        File dailyFile = new File(pl.getDataFolder(), "dailyRewards.yml");
        YamlConfiguration dailyYml = YamlConfiguration.loadConfiguration(dailyFile);

        String invName = e.getView().getTitle();
        Player p = (Player) e.getWhoClicked();
        invName = ChatColor.stripColor(invName);
        int slot = e.getSlot();
        if (invName.equalsIgnoreCase(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', pl.getConfig().getString("gui.title").replace("{badge}", pl.badge))))) {
            e.setCancelled(true);

            for (String menuItem : pl.getConfig().getConfigurationSection("gui.items").getKeys(false)) {
                if (slot == pl.getConfig().getInt("gui.items." + menuItem + ".position")) {
                    if (e.getClick() == ClickType.LEFT) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "issuereward " + p.getName() + " " + menuItem);
                        rewardsGui.rewardsGui(p);
                    } else if (e.getClick() == ClickType.RIGHT) {
                        Bukkit.dispatchCommand(e.getWhoClicked(), "showrewards " + menuItem);
                    }
                }
            }
        } else if (invName.equalsIgnoreCase(ChatColor.RED + pl.getConfig().getString("gui.title"))) {
            e.setCancelled(true);
        } else if (invName.equalsIgnoreCase(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', dailyYml.getString("gui.title").replace("{badge}", pl.badge))))) {
            e.setCancelled(true);
            File playerFile = new File(pl.getDataFolder() + File.separator + "playerFiles", p.getUniqueId() + ".yml");
            YamlConfiguration playerYml = YamlConfiguration.loadConfiguration(playerFile);

            for (String menuItem : dailyYml.getConfigurationSection("gui.items").getKeys(false)) {
                if (slot == dailyYml.getInt("gui.items." + menuItem + ".Slot")) {
                    if ((playerYml.get("claimed." + menuItem) == null || playerYml.getInt("claimed." + menuItem) == 0) && !(playerYml.get("streak") == null) && playerYml.getInt("streak") >= dailyYml.getInt("gui.items." + menuItem + ".DaysUntilPlayerCanClaim")) {
                        if (e.getClick() == ClickType.LEFT) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), dailyYml.getString("gui.items." + menuItem + ".Command").replace("[Player]", p.getName()));
                            playerYml.set("claimed." + menuItem, 1);
                            try {
                                playerYml.save(playerFile);
                            } catch (IOException err) {
                                err.printStackTrace();
                            }
                            dailyGui.dailyGui(p);
                        }
                    }
                }
            }
        }
    }
}
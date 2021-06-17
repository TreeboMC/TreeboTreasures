package me.shakeforprotein.treebotreasures.Listeners;

import me.shakeforprotein.treebotreasures.Guis.DailyGui;
import me.shakeforprotein.treebotreasures.Guis.RewardsGui;
import me.shakeforprotein.treebotreasures.TreeboTreasures;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;


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
    public void rewardTicketsListener(InventoryClickEvent e){
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
        }
    }

    @EventHandler
    public void InventoryClick(InventoryClickEvent e){
        File dailyFile = new File(pl.getDataFolder(), "dailyRewards.yml");
        YamlConfiguration dailyYml = YamlConfiguration.loadConfiguration(dailyFile);

        String invName = e.getView().getTitle();
        Player p = (Player) e.getWhoClicked();
        invName = ChatColor.stripColor(invName);
        int slot = e.getSlot();
        if (invName.equalsIgnoreCase(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', dailyYml.getString("gui.title").replace("{badge}", pl.badge))))) {
            e.setCancelled(true);
            for(String key : dailyYml.getConfigurationSection("gui.items").getKeys(false)){
                if(dailyYml.getInt("gui.items." + key + ".Slot") == slot && e.getClickedInventory().getItem(slot).getType() == Material.valueOf(dailyYml.getString("gui.items." + key + ".ActiveItem"))){
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), dailyYml.getString("gui.items." + key + ".Command").replace("[Player]", p.getName()));
                    setUsed(p, key);
                    dailyGui.dailyGui(p);
                    break;
                }
                else{
                }
            }
        }
    }

    private void setUsed(Player p, String key){
        File playerFile = new File(pl.playerDataFolder + File.separator + p.getUniqueId().toString(), "TTreasures_DailyReward" + ".yml");
        YamlConfiguration playerYml = YamlConfiguration.loadConfiguration(playerFile);
        playerYml.set("claimed." + key, System.currentTimeMillis());
        try {
            playerYml.save(playerFile);
        } catch (IOException err) {
            err.printStackTrace();
        }
    }
}

//TODO: Rewrite Daily gui/listener to use updated config values. Replace "DaysUntilPlayerCanClaim" with "StreakRequired" then use Repeats as the DaysUntil. Do full rewrite of the logic.
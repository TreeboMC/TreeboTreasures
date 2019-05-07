package me.shakeforprotein.treebotreasures.Listeners;

import me.shakeforprotein.treebotreasures.TreeboTreasures;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.ResultSet;

public class JoinListener implements Listener {

    private TreeboTreasures pl = TreeboTreasures.instance;

    public JoinListener(TreeboTreasures main){
        this.pl = main;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(pl, new Runnable() {
            public void run() {
                try {
                    Player p = e.getPlayer();
                    pl.openConnection();
                    ResultSet results = pl.connection.createStatement().executeQuery("SELECT * FROM `" + pl.table + "` WHERE `UUID` = '" + p.getUniqueId() + "'");
                    while (results.next()) {
                        for (String menuItem : pl.getConfig().getConfigurationSection("gui.items").getKeys(false)) {
                            pl.getConfig().set("keys." + p.getUniqueId() + "." + menuItem.toUpperCase(), results.getInt(menuItem));
                        }
                    }
                } catch (Exception err) {
                    pl.makeLog(err);
                }
            }
        });
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(pl, new Runnable() {
            public void run() {
                try {
                    Player p = e.getPlayer();
                    pl.openConnection();
                    String zeroString = "";
                    for (String menuItem : pl.getConfig().getConfigurationSection("gui.items").getKeys(false)) {
                        if (pl.getConfig().get("keys." + p.getUniqueId()) != null) {
                            if(pl.getConfig().get("keys." + p.getUniqueId() + "." + menuItem.toUpperCase()) != null){
                                zeroString += "" + menuItem + " = " + pl.getConfig().getInt("keys." + p.getUniqueId() + "." + menuItem.toUpperCase()) + ", ";
                            }
                        } else {
                            p.sendMessage("ERROR");
                            break;
                        }
                    }
                    zeroString = zeroString.replaceAll(", $", "");
                    int results = pl.connection.createStatement().executeUpdate("UPDATE `" + pl.table + "` SET " + zeroString + " WHERE `UUID` = '" + p.getUniqueId() + "'");
                    for (String menuItem : pl.getConfig().getConfigurationSection("gui.items").getKeys(false)) {
                        pl.getConfig().set("keys." + p.getUniqueId() + "." + menuItem.toUpperCase(), 0);
                    }
                    pl.saveConfig();
                } catch (Exception err) {
                    pl.makeLog(err);
                }
            }
        });
    }
}

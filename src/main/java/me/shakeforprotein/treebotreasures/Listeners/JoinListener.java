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
import java.util.HashMap;
import java.util.UUID;

public class JoinListener implements Listener {

    private TreeboTreasures pl;

    public JoinListener(TreeboTreasures main) {
        this.pl = main;
    }

    private HashMap joinHash = new HashMap<UUID, Player>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if(joinHash.containsKey(e.getPlayer().getUniqueId())){
            joinHash.remove(e.getPlayer().getUniqueId());
        }
        for (String menuItem : pl.getConfig().getConfigurationSection("gui.items").getKeys(false)) {
            pl.getConfig().set("keys." + e.getPlayer().getUniqueId() + "." + menuItem.toUpperCase(), 0);
        }
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
            @Override
            public void run() {
                Bukkit.getServer().getScheduler().runTaskAsynchronously(pl, new Runnable() {
                    public void run() {
                        try {
                            Player p = e.getPlayer();
                            pl.openConnection();

                            ResultSet exists = pl.connection.createStatement().executeQuery("SELECT Count(*) AS COUNT FROM `TreeboTreasures` WHERE UUID = '" + p.getUniqueId() + "'");
                            while (exists.next()){
                                if(exists.getInt("COUNT") < 1){
                                    System.out.println(pl.badge + "Player not found in database. Adding new entry");
                                    int insert = pl.connection.createStatement().executeUpdate("INSERT INTO `" + pl.table + "` (UUID, IGNAME) VALUES ('" + p.getUniqueId() + "','" + p.getName() + "');");
                                }
                            }

                            ResultSet results = pl.connection.createStatement().executeQuery("SELECT * FROM `" + pl.table + "` WHERE `UUID` = '" + p.getUniqueId() + "'");
                            while (results.next()) {
                                for (String menuItem : pl.getConfig().getConfigurationSection("gui.items").getKeys(false)) {
                                    pl.getConfig().set("keys." + p.getUniqueId() + "." + menuItem.toUpperCase(), results.getInt(menuItem));
                                }
                            }
                            if(p.isOnline()){
                                joinHash.put(p.getUniqueId(), p);
                            }
                        } catch (Exception err) {
                            pl.makeLog(err);
                        }
                    }
                });
            }
        }, 100L);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(pl, new Runnable() {
            public void run() {
                try {
                    Player p = e.getPlayer();
                    if (joinHash.containsKey(p.getUniqueId())) {
                        pl.openConnection();
                        String zeroString = "";
                        for (String menuItem : pl.getConfig().getConfigurationSection("gui.items").getKeys(false)) {
                            if (pl.getConfig().get("keys." + p.getUniqueId()) != null) {
                                if (pl.getConfig().get("keys." + p.getUniqueId() + "." + menuItem.toUpperCase()) != null) {
                                    zeroString += "" + menuItem + " = " + pl.getConfig().getInt("keys." + p.getUniqueId() + "." + menuItem.toUpperCase()) + ", ";
                                }
                            } else {
                                p.sendMessage("ERROR");
                                break;
                            }
                        }
                        zeroString = zeroString.replaceAll(", $", "");
                        System.out.println("UPDATE `" + pl.table + "` SET " + zeroString + " WHERE `UUID` = '" + p.getUniqueId() + "'");
                        int results = pl.connection.createStatement().executeUpdate("UPDATE `" + pl.table + "` SET " + zeroString + " WHERE `UUID` = '" + p.getUniqueId() + "'");
                        for (String menuItem : pl.getConfig().getConfigurationSection("gui.items").getKeys(false)) {
                            pl.getConfig().set("keys." + p.getUniqueId() + "." + menuItem.toUpperCase(), 0);
                        }
                        pl.saveConfig();
                        joinHash.remove(p.getUniqueId());
                    }
                } catch (Exception err) {
                    pl.makeLog(err);
                }
            }
        });
    }
}

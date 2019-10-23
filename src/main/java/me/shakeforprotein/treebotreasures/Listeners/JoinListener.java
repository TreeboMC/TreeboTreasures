package me.shakeforprotein.treebotreasures.Listeners;

import me.shakeforprotein.treebotreasures.TreeboTreasures;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class JoinListener implements Listener {

    private TreeboTreasures pl;

    public JoinListener(TreeboTreasures main) {
        this.pl = main;
    }

    public HashMap joinHash = new HashMap<UUID, Player>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (joinHash.containsKey(e.getPlayer().getUniqueId())) {
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
                            while (exists.next()) {
                                if (exists.getInt("COUNT") < 1) {
                                    System.out.println(pl.badge + "Player not found in database. Adding new entry");
                                    int insert = pl.connection.createStatement().executeUpdate("INSERT INTO `" + pl.table + "` (UUID, IGNAME) VALUES ('" + p.getUniqueId() + "','" + p.getName() + "');");
                                }
                            }

                            ResultSet results = pl.connection.createStatement().executeQuery("SELECT * FROM `" + pl.table + "` WHERE `UUID` = '" + p.getUniqueId() + "'");
                            while (results.next()) {
                                for (String menuItem : pl.getConfig().getConfigurationSection("gui.items").getKeys(false)) {
                                    pl.getConfig().set("keys." + p.getUniqueId() + "." + menuItem.toUpperCase(), results.getInt(menuItem));
                                    if (pl.getConfig().get("cachedKeys." + p.getUniqueId().toString() + "." + menuItem.toUpperCase()) != null) {
                                        int cachedKey = pl.getConfig().getInt("cachedKeys." + p.getUniqueId().toString() + "." + menuItem.toUpperCase());
                                        int currentKeys = pl.getConfig().getInt("keys." + p.getUniqueId().toString() + "." + menuItem.toUpperCase());
                                        int newKeys = cachedKey + currentKeys;
                                        pl.getConfig().set("keys." + p.getUniqueId().toString() + "." + menuItem.toUpperCase(), newKeys);
                                        pl.getConfig().set("cachedKeys." + p.getUniqueId().toString() + "." + menuItem.toUpperCase(), null);
                                    }
                                }
                                pl.getConfig().set("cachedKeys." + p.getUniqueId().toString(), null);

                            }
                            if (p.isOnline()) {
                                joinHash.put(p.getUniqueId(), p);
                            }
                        } catch (Exception err) {
                            pl.makeLog(err);
                        }
                    }
                });
            }
        }, 100L);

        if (pl.getConfig().getBoolean("doDailyRewards")) {
            File playerFileFolder = new File(pl.getDataFolder() + File.separator + "playerFiles");
            File playerFile = new File(playerFileFolder, File.separator + e.getPlayer().getUniqueId() + ".yml");
            FileConfiguration playerYml = YamlConfiguration.loadConfiguration(playerFile);


            Player p = e.getPlayer();
            //Set default values in case they don't exist
            long timeNow = (System.currentTimeMillis() / 1000) / 60;
            long lastToken = timeNow;
            int streak = 0;
            if (playerYml.get("lastToken") != null) {
                lastToken = playerYml.getLong("lastToken");
            }
            if (playerYml.get("streak") != null) {
                streak = playerYml.getInt("streak");
            }
            if ((timeNow - 3600) > lastToken && ((timeNow - 4800) < lastToken)) {
                lastToken = timeNow;
                streak++;
                p.sendMessage(pl.badge + " You have earned a daily reward. Your current streak is " + streak);
            } else {
                boolean doNothing = true;
            }
            playerYml.set("lastToken", lastToken);
            playerYml.set("streak", streak);
            try {
                playerYml.save(playerFile);
            } catch (IOException err) {
                boolean doNothing = true;
            }
        }
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
                            //pl.getConfig().set("keys." + p.getUniqueId() + "." + menuItem.toUpperCase(), 0);
                            pl.getConfig().set("keys." + p.getUniqueId(), null);
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

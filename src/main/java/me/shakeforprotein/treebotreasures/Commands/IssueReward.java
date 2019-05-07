package me.shakeforprotein.treebotreasures.Commands;

import me.shakeforprotein.treebotreasures.TreeboTreasures;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

public class IssueReward implements CommandExecutor {

    private TreeboTreasures pl;

    public IssueReward(TreeboTreasures main) {
        this.pl = main;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        int keys = 0;
        boolean success = false;
        File tableFolder = new File(pl.getDataFolder() + File.separator + "lootTables");
        String keyType = "";
        if (args.length == 2) {
            if (Bukkit.getPlayer(args[0]) != null) {
                Player p = Bukkit.getPlayer(args[0]);
                for (String item : pl.getConfig().getConfigurationSection("categories").getKeys(false)) {
                    if (args[1].equalsIgnoreCase(item)) {
                        keyType = args[1];
                        break;
                    }
                }
                if (keyType.equalsIgnoreCase("")) {
                    sender.sendMessage(pl.err + "Invalid key type");
                } else {
                             if(pl.getConfig().get("keys." + p.getUniqueId() + "." + keyType.toUpperCase()) != null){
                                 keys = pl.getConfig().getInt("keys." + p.getUniqueId() + "." + keyType.toUpperCase());
                             }


                            if (keys > 0) {
                                sender.sendMessage(pl.badge + "Player has " + keys + " " + args[1] + " keys");

                                File rewardsFile = new File(pl.getDataFolder() + File.separator + "lootTables", keyType + ".yml");
                                FileConfiguration rewardsYml = YamlConfiguration.loadConfiguration(rewardsFile);
                                ArrayList<String> validItems = new ArrayList<>();
                                for (String ymlItem : rewardsYml.getConfigurationSection("items").getKeys(false)) {
                                    if (rewardsYml.get("items." + ymlItem + ".stack") != null) {
                                        validItems.add(ymlItem);
                                    }
                                }
                                int randNum = randomWithRange(0, validItems.toArray().length-1);

                                if (rewardsYml.get("items." + validItems.toArray()[randNum] + ".stack") != null) {
                                    success = true;
                                    p.getInventory().addItem(rewardsYml.getItemStack("items." + validItems.toArray()[randNum] + ".stack"));
                                    p.getWorld().playEffect(p.getLocation(), Effect.END_GATEWAY_SPAWN, 10);
                                } else {
                                    sender.sendMessage("Fail");
                                }

                            } else {
                                sender.sendMessage(pl.err + "Player has no " + args[1] + " keys");
                            }
                        }
            } else {
                sender.sendMessage(pl.err + "Player not found");
            }
        } else {
            sender.sendMessage(pl.err + "Invalid Arguments");
        }
        if (success) {
            Player p = Bukkit.getPlayer(args[0]);
            p.sendMessage("You have " + (keys -1) + " " + args[1] + " Keys remaining.");
            pl.getConfig().set("keys." + p.getUniqueId() + "." + keyType.toUpperCase(), pl.getConfig().getInt("keys." + p.getUniqueId() + "." + keyType.toUpperCase()) -1);
        }
        pl.saveConfig();

        return true;
    }

    /*@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(pl, new Runnable() {
            public void run() {
                pl.createConnection();
                boolean success = false;
                File tableFolder = new File(pl.getDataFolder() + File.separator + "lootTables");
                String keyType = "";
                if (args.length == 2) {
                    if (Bukkit.getPlayer(args[0]) != null) {
                        Player p = Bukkit.getPlayer(args[0]);
                        for (String item : pl.getConfig().getConfigurationSection("categories").getKeys(false)) {
                            if (args[1].equalsIgnoreCase(item)) {
                                keyType = args[1];
                                break;
                            }
                        }
                        if (keyType.equalsIgnoreCase("")) {
                            sender.sendMessage(pl.err + "Invalid key type");
                        } else {
                            try {
                                ResultSet results = pl.connection.createStatement().executeQuery("SELECT * FROM `" + pl.table + "` WHERE `UUID` = '" + p.getUniqueId() + "'");
                                while (results.next()) {
                                    int keys = results.getInt(args[1]);
                                    if (keys > 0) {
                                        sender.sendMessage("Player has " + keys + " " + args[1] + " keys");

                                        File rewardsFile = new File(pl.getDataFolder() + File.separator + "lootTables", keyType + ".yml");
                                        FileConfiguration rewardsYml = YamlConfiguration.loadConfiguration(rewardsFile);
                                        ArrayList<String> validItems = new ArrayList<>();
                                        for (String ymlItem : rewardsYml.getConfigurationSection("items").getKeys(false)) {
                                            if (rewardsYml.get("items." + ymlItem + ".stack") != null) {
                                                validItems.add(ymlItem);
                                            }
                                        }
                                        int randNum = randomWithRange(0, validItems.toArray().length);

                                        if (rewardsYml.get("items." + validItems.toArray()[randNum] + ".stack") != null) {
                                            success = true;
                                            p.getInventory().addItem(rewardsYml.getItemStack("items." + validItems.toArray()[randNum] + ".stack"));
                                            p.getWorld().playEffect(p.getLocation(), Effect.END_GATEWAY_SPAWN, 10);
                                        } else {
                                            sender.sendMessage("Fail");
                                        }

                                    } else {
                                        sender.sendMessage("Player has no " + args[1] + "keys");
                                    }
                                }
                            } catch (SQLException err) {
                                sender.sendMessage("Sql Exception");
                                pl.makeLog(err);
                            }
                        }
                    } else {
                        sender.sendMessage("Player not found");
                    }
                } else {
                    sender.sendMessage(pl.err + "Invalid Arugments");
                }
                pl.closeConnection();
                if (success) {
                    Player p = Bukkit.getPlayer(args[0]);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "removekey " + p.getName() + " " + args[1] + " " + 1);
                }
            }
        });
        return true;
    }

     */


    private int randomWithRange(int min, int max) {
        int range = Math.abs(max - min) + 1;
        return (int) (Math.random() * range) + (min <= max ? min : max);
    }
}

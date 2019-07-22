package me.shakeforprotein.treebotreasures.Commands;

import me.shakeforprotein.treebotreasures.TreeboTreasures;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;

public class IssueReward implements CommandExecutor {

    private TreeboTreasures pl;

    public IssueReward(TreeboTreasures main) {
        this.pl = main;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        int keys = 0;
        ItemStack receivedItem = null;
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
                    if (pl.getConfig().get("keys." + p.getUniqueId() + "." + keyType.toUpperCase()) != null) {
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
                        int randNum = randomWithRange(0, validItems.toArray().length - 1);

                        if (rewardsYml.get("items." + validItems.toArray()[randNum] + ".stack") != null) {
                            success = true;
                            ItemStack rewardItem = rewardsYml.getItemStack("items." + validItems.toArray()[randNum] + ".stack");

                            if (rewardItem.getType().equals(Material.PAPER) && rewardItem.hasItemMeta() && rewardItem.getItemMeta().hasDisplayName() && rewardItem.getItemMeta().getDisplayName().equalsIgnoreCase("RUN COMMAND")) {
                                int randCommandNumber = randomWithRange(0, rewardsYml.getConfigurationSection("commands").getKeys(false).size() -1);
                                int f = 0;
                                for(String key : rewardsYml.getConfigurationSection("commands").getKeys(false)) {
                                    if(randCommandNumber == f){
                                        String command = rewardsYml.getString("commands." + key + ".command").replace("{player}", p.getName());
                                        String playerMessage = ChatColor.translateAlternateColorCodes('&', rewardsYml.getString("commands." + key + ".message").replace("{player}", p.getName()));
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                                        p.sendMessage(playerMessage);
                                        break;
                                    }
                                    else{
                                        f++;
                                    }
                                }
                            }

                            else {
                                p.getInventory().addItem(rewardItem);
                                receivedItem = rewardItem;
                            }
                            p.getWorld().playEffect(p.getLocation(), Effect.END_GATEWAY_SPAWN, 10);
                        }

                        else {
                            sender.sendMessage(pl.err + "Fail");
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
            if(receivedItem != null) {
                if(receivedItem.hasItemMeta() && receivedItem.getItemMeta().hasDisplayName()){
                p.sendMessage(pl.badge + "You have " + (keys - 1) + " " + args[1] + " Keys remaining, and received " + ChatColor.GOLD + receivedItem.getAmount() + ChatColor.RESET + " X " + ChatColor.GOLD + receivedItem.getItemMeta().getDisplayName());
                }
                else{
                    p.sendMessage(pl.badge + "You have " + (keys - 1) + " " + args[1] + " Keys remaining, and received " + ChatColor.GOLD + receivedItem.getAmount() + ChatColor.RESET + " X " + ChatColor.GOLD + receivedItem.getType().name());
                }
            }
            else{
                p.sendMessage(pl.badge + "You have " + (keys - 1) + " " + args[1] + " Keys");
            }
                pl.getConfig().set("keys." + p.getUniqueId() + "." + keyType.toUpperCase(), pl.getConfig().getInt("keys." + p.getUniqueId() + "." + keyType.toUpperCase()) - 1);
        }
        pl.saveConfig();

        return true;
    }


    private int randomWithRange(int min, int max) {
        int range = Math.abs(max - min) + 1;
        return (int) (Math.random() * range) + (min <= max ? min : max);
    }
}

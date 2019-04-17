package me.shakeforprotein.treebotreasures.Commands;

import me.shakeforprotein.treebotreasures.TreeboTreasures;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ConfigureRewards implements CommandExecutor {

    private TreeboTreasures pl;

    public ConfigureRewards(TreeboTreasures main) {
        this.pl = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            args[0] = args[0].toLowerCase();
            File tableFolder = new File(pl.getDataFolder() + File.separator + "lootTables");
            File lootFile = new File(tableFolder, File.separator + pl.getConfig().getString("categories." + args[0]));
            FileConfiguration lootMenu = YamlConfiguration.loadConfiguration(lootFile);

            if(!tableFolder.exists()){
                boolean rslt = tableFolder.mkdir();
            }

            if (!lootFile.exists()) {
                try {
                    boolean rslt = lootFile.createNewFile();
                    try {
                        lootMenu.options().copyDefaults();
                        lootMenu.save(lootFile);
                    } catch (FileNotFoundException e) {
                        pl.makeLog(e);
                    }
                } catch (IOException e) {
                    pl.makeLog(e);
                }
            }

            Inventory lootInventory = Bukkit.createInventory(null, 54, args[0]);

            if (lootMenu.getConfigurationSection("items") != null) {
                for (String item : lootMenu.getConfigurationSection("items").getKeys(false)) {
                    if(lootMenu.get("items." + item + ".stack") != null){
                        ItemStack newItem = lootMenu.getItemStack("items." + item + ".stack");
                        lootInventory.setItem(lootMenu.getInt("items." + item + ".position"),newItem);
                    }
                }
            }
            if (sender instanceof Player) {
                ((Player) sender).openInventory(lootInventory);
            }
        } else {
            sender.sendMessage("Invalid arguments");
        }
        return true;
    }
}

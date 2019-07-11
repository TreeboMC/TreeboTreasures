package me.shakeforprotein.treebotreasures.Listeners;

import me.shakeforprotein.treebotreasures.TreeboTreasures;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;

public class ConfigureListener implements Listener {

    private TreeboTreasures pl;

    public ConfigureListener(TreeboTreasures main){
        this.pl = main;
    }

    @EventHandler
    private void onInventoryInteract(InventoryInteractEvent e){
        for(String item : pl.getConfig().getConfigurationSection("categories").getKeys(false)) {
            if (e.getView().getTitle().equalsIgnoreCase(item)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void onInventoryClose(InventoryCloseEvent e){
        String invFilename = "";
        for(String item : pl.getConfig().getConfigurationSection("categories").getKeys(false)){
            if(e.getView().getTitle().equalsIgnoreCase(item)){
                invFilename = e.getView().getTitle();
                File tableFolder = new File(pl.getDataFolder() + File.separator + "lootTables");
                File lootFile = new File(tableFolder, File.separator + invFilename + ".yml");
                FileConfiguration lootMenu = YamlConfiguration.loadConfiguration(lootFile);
                int counter = 0;
                for(ItemStack stack : e.getInventory().getContents()){
                    lootMenu.set("items.item" + counter + ".stack", stack);
                    lootMenu.set("items.item" + counter + ".position", counter);
                    counter++;
                }
                try{
                    lootMenu.save(lootFile);
                    e.getPlayer().sendMessage("File saved.");
                }
                catch(IOException err){
                    pl.makeLog(err);
                    e.getPlayer().sendMessage("Failed to save inventory");
                }
                break;
            }
        }
    }
}

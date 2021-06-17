package me.shakeforprotein.treebotreasures.Listeners;

import me.shakeforprotein.treebotreasures.TreeboTreasures;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
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
            if (e.getView().getTitle().equalsIgnoreCase(item) || e.getView().getTitle().equalsIgnoreCase(ChatColor.RED + ChatColor.stripColor(item))) {
                e.setCancelled(true);
            }
            if(e.getView().getTitle().startsWith("Possible Rewards:")){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void onInventoryPickup(InventoryPickupItemEvent e){
        for(HumanEntity p : e.getInventory().getViewers()){
            if(p.getOpenInventory().getTitle().startsWith("Possible Rewards:")){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void onInventoryMove(InventoryMoveItemEvent e){
        for(HumanEntity p : e.getDestination().getViewers()){
            if(p.getOpenInventory().getTitle().startsWith("Possible Rewards:")){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void onInventoryRoll(InventoryClickEvent e){
        for(HumanEntity p : e.getInventory().getViewers()){
            if(p.getOpenInventory().getTitle().startsWith("Possible Rewards:")){
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
                catch(IOException ex){
                    pl.roots.errorLogger.logError(pl, ex);
                    e.getPlayer().sendMessage("Failed to save inventory");
                }
                break;
            }
        }
    }
}

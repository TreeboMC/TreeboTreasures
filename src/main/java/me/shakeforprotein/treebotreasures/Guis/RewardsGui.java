package me.shakeforprotein.treebotreasures.Guis;

import me.shakeforprotein.treebotreasures.TreeboTreasures;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RewardsGui {

    private TreeboTreasures pl;

    public RewardsGui(TreeboTreasures main) {
        this.pl = main;
    }

    public void rewardsGui(Player p) {

        int rows = pl.getConfig().getInt("gui.rows");
        String title = ChatColor.translateAlternateColorCodes('&', pl.getConfig().getString("gui.title") + "");

        Inventory thisInv = Bukkit.createInventory(null, rows * 9, title);
        for (int slot = 0; slot < thisInv.getSize(); slot++) {
            ItemStack fillerItem = new ItemStack(Material.valueOf(pl.getConfig().getString("gui.filler")));
            ItemMeta fillMeta = fillerItem.getItemMeta();
            fillMeta.setDisplayName(" ");
            fillerItem.setItemMeta(fillMeta);
            thisInv.setItem(slot, fillerItem);
        }
        for (String menuItem : pl.getConfig().getConfigurationSection("gui.items").getKeys(false)) {
            int position = pl.getConfig().getInt("gui.items." + menuItem + ".position");
            ItemStack newItem = new ItemStack(Material.valueOf(pl.getConfig().getString("gui.items." + menuItem + ".material")), 1);
            ItemMeta newMeta = newItem.getItemMeta();
            List<String> newLore = new ArrayList<>();
            int keys = 0;
            if (pl.getConfig().get("keys." + p.getUniqueId() + "." + menuItem.toUpperCase()) != null) {
                keys = pl.getConfig().getInt("keys." + p.getUniqueId() + "." + menuItem.toUpperCase());
            }
            newLore.add(ChatColor.translateAlternateColorCodes('&', pl.getConfig().getString("gui.countLore").replace("{keyCount}", keys + "").replace("{keyType}", menuItem)));
            newMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', pl.getConfig().getString("gui.items." + menuItem + ".title")));
            newMeta.setLore(newLore);
            newItem.setItemMeta(newMeta);
            thisInv.setItem(position, newItem);
        }
        for (int slot = 0; slot < thisInv.getSize(); slot++) {
            if (thisInv.getItem(slot).getType() == Material.AIR) {
                ItemStack airItem = new ItemStack(Material.AIR);
                thisInv.setItem(slot, airItem);
            }
        }

        p.openInventory(thisInv);
    }

    /*
    public void rewardsGui(Player p){

        Bukkit.getServer().getScheduler().runTaskAsynchronously(pl, new Runnable() {
            public void run() {
                try {
                    pl.openConnection();
                    ResultSet results = pl.connection.createStatement().executeQuery("SELECT * FROM `" + pl.table + "` WHERE `UUID` = '" + p.getUniqueId() + "'");
                    while (results.next()) {


                        int rows = pl.getConfig().getInt("gui.rows");
                        String title = ChatColor.translateAlternateColorCodes('&', pl.getConfig().getString("gui.title").replace("{badge}", pl.badge));

                        Inventory thisInv = Bukkit.createInventory(null, rows * 9, title);

                        for (String menuItem : pl.getConfig().getConfigurationSection("gui.items").getKeys(false)) {
                            ItemStack newItem = new ItemStack(Material.valueOf(pl.getConfig().getString("gui.items." + menuItem + ".material")), 1);
                            ItemMeta newMeta = newItem.getItemMeta();
                            List<String> newLore = new ArrayList<String>();
                            newLore.add("You have -* " + results.getInt(menuItem) + " *- " + menuItem + " keys");
                            newMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', pl.getConfig().getString("gui.items." + menuItem + ".title")));
                            int position = pl.getConfig().getInt("gui.items." + menuItem + ".position");
                            newItem.setItemMeta(newMeta);
                            thisInv.setItem(position, newItem);
                        }
                        p.openInventory(thisInv);
                        pl.closeConnection();
                    }}
                catch(Exception err){
                    pl.makeLog(err);
                }
            }});
    }
    */
}

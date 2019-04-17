package me.shakeforprotein.treebotreasures.Guis;

import me.shakeforprotein.treebotreasures.TreeboTreasures;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RewardsGui {

    private TreeboTreasures pl;

    public RewardsGui(TreeboTreasures main){
        this.pl = main;
    }

    public void rewardsGui(Player p){


        int rows = pl.getConfig().getInt("gui.rows");
        String title = ChatColor.translateAlternateColorCodes('&',pl.getConfig().getString("gui.title").replace("{badge}", pl.badge));

        Inventory thisInv = Bukkit.createInventory(null, rows*9, title);

        for(String menuItem : pl.getConfig().getConfigurationSection("gui.items").getKeys(false)){
            ItemStack newItem = new ItemStack(Material.valueOf(pl.getConfig().getString("gui.items." + menuItem + ".material")), 1);
            ItemMeta newMeta = newItem.getItemMeta();
            newMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', pl.getConfig().getString("gui.items." + menuItem + ".title")));
            int position = pl.getConfig().getInt("gui.items." + menuItem + ".position");
            newItem.setItemMeta(newMeta);
            thisInv.setItem(position, newItem);
        }
        p.openInventory(thisInv);
    }
}

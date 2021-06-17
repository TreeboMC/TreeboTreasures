package me.shakeforprotein.treebotreasures.Guis;

import me.shakeforprotein.treebotreasures.TreeboTreasures;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
            newLore.add("");
            newLore.add("Left Click to claim");
            newLore.add("Right Click to show possible rewards");
            newMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', pl.getConfig().getString("gui.items." + menuItem + ".title")));
            newMeta.setLore(newLore);
            newItem.setItemMeta(newMeta);
            if(keys > 0){
                addGlow(newItem);
            }
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

    public void addGlow(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        meta.addEnchant(Enchantment.LURE, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        stack.setItemMeta(meta);
    }
}

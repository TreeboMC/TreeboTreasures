package me.shakeforprotein.treebotreasures.Listeners;

import me.shakeforprotein.treebotreasures.Guis.RewardsGui;
import me.shakeforprotein.treebotreasures.TreeboTreasures;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class TicketSelectGuiListener implements Listener {

    private TreeboTreasures pl;
    private RewardsGui rewardsGui;

    public TicketSelectGuiListener(TreeboTreasures main){
        this.pl = main;
        this.rewardsGui = new RewardsGui(pl);
    }

    @EventHandler
    private void useRewardsGui(InventoryClickEvent e) {
        String invName = e.getView().getTitle();
        Player p = (Player) e.getWhoClicked();
        invName = ChatColor.stripColor(invName);
        int slot = e.getSlot();
        if(invName.equalsIgnoreCase(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', pl.getConfig().getString("gui.title").replace("{badge}", pl.badge))))){
            e.setCancelled(true);

            for (String menuItem : pl.getConfig().getConfigurationSection("gui.items").getKeys(false)) {
                if (slot == pl.getConfig().getInt("gui.items." + menuItem + ".position")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "issuereward " + p.getName() + " " + menuItem);
                    rewardsGui.rewardsGui(p);
                }
            }
        }
        else if(invName.equalsIgnoreCase(ChatColor.RED + pl.getConfig().getString("gui.title"))){
            e.setCancelled(true);
        }
    }
}

package me.shakeforprotein.treebotreasures.Listeners;

import me.shakeforprotein.treebotreasures.Guis.RewardsGui;
import me.shakeforprotein.treebotreasures.TreeboTreasures;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;


public class OpenRewardsGuiListener implements Listener {

    private TreeboTreasures pl;

    public OpenRewardsGuiListener(TreeboTreasures main){
        this.pl = main;
    }



    @EventHandler
    public void openGuiListener(PlayerInteractEvent e) {
        RewardsGui rewardsGui = new RewardsGui(pl);
        Player p = e.getPlayer();

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getHand() == EquipmentSlot.HAND) {
            Block clickedBlock = e.getClickedBlock();
            if(e.getPlayer().getInventory().getItemInMainHand().getType() == Material.NETHER_STAR && e.getPlayer().hasPermission("ttreasures.configure")){
                    pl.getConfig().set("BlockOpener", e.getClickedBlock());

                    pl.getConfig().set("BlockOpenerState", clickedBlock.getState());
            }

            if (clickedBlock.getState() instanceof Skull) {
                Skull theSkull = (Skull) clickedBlock.getState();
                String bUUID = theSkull.getOwningPlayer().getUniqueId().toString();
                /*if (bUUID.equalsIgnoreCase("1d060f05-7253-4959-b583-4e023332c965") || bUUID.equalsIgnoreCase("2d060f05-7253-4959-b583-4e023332c965") || bUUID.equalsIgnoreCase("3d060f05-7253-4959-b583-4e023332c965") || bUUID.equalsIgnoreCase("4d060f05-7253-4959-b583-4e023332c965")) {
                    Location openerLoc = clickedBlock.getLocation();
                    clickedBlock.getWorld().playSound(openerLoc, Sound.BLOCK_CHEST_LOCKED,4,0);
                    rewardsGui.rewardsGui(p);
                }
                 */
                Skull configSkull = (Skull) ((BlockState) pl.getConfig().get("BlockOpenerState"));
                String cUUID = configSkull.getOwningPlayer().getUniqueId().toString();
                if (bUUID.equalsIgnoreCase(cUUID)) {
                    Location openerLoc = clickedBlock.getLocation();
                    clickedBlock.getWorld().playSound(openerLoc, Sound.BLOCK_CHEST_LOCKED,4,0);
                    rewardsGui.rewardsGui(p);
                }
            }
        }
    }
}

        /*
        eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vZWR1Y2F0aW9uLm1pbmVjcmFmdC5uZXQvd3AtY29udGVudC91cGxvYWRzL1RyZWVib0NoZXN0X3doaXRlX3NoYWRvd2VkcGRuLnBuZyJ9fX0=
        3d060f05-7253-4959-b583-4e023332c965

        1d060f05-7253-4959-b583-4e023332c965
        2d060f05-7253-4959-b583-4e023332c965
        4d060f05-7253-4959-b583-4e023332c965

        */
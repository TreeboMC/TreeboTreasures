package me.shakeforprotein.treebotreasures.Commands;

import me.shakeforprotein.treebotreasures.Guis.RewardsGui;
import me.shakeforprotein.treebotreasures.TreeboTreasures;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TreasuresGui implements CommandExecutor {

    private TreeboTreasures pl;
    public TreasuresGui(TreeboTreasures main) {
        this.pl = main;
    }




    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        RewardsGui rewardsGui = new RewardsGui(pl);
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if(args.length == 0 || !p.hasPermission("tbtreasures.addkey")){
                rewardsGui.rewardsGui(p);
            }
            else {
                if(Bukkit.getPlayer(args[0]) != null) {
                    Player target = Bukkit.getPlayer(args[0]);
                    rewardsGui.rewardsGui(target);
                }
                else{
                    p.sendMessage("Unknown player at argument " + args[0]);
                }
            }
        } else {
            if (args.length == 0) {
                sender.sendMessage(pl.badge + "This command requires a Playername to run from here.");
            }
        }
        return true;
    }
}

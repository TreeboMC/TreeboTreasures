package me.shakeforprotein.treebotreasures.Commands;

import me.shakeforprotein.treebotreasures.Guis.DailyGui;
import me.shakeforprotein.treebotreasures.TreeboTreasures;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DailyReward implements CommandExecutor {

    private TreeboTreasures pl;
    private DailyGui dailyGui;

    public DailyReward(TreeboTreasures main) {
        this.pl = main;
        this.dailyGui = new DailyGui(pl);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(pl.roots.getConfig().getString("General.ServerDetails.ServerName").equalsIgnoreCase("Hub")) {
            if(sender instanceof Player) {
                dailyGui.dailyGui((Player) sender);
            }
            else {
                sender.sendMessage("As this command opens a GUI, it is not possible to run from console.");
            }
        }
        else {
            sender.sendMessage(pl.badge + "This command is only enabled on the Hub Server.");
        }
        return true;
    }
}

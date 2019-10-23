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
        dailyGui.dailyGui((Player) sender);
        return true;
    }
}

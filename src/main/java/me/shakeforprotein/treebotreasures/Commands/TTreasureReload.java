package me.shakeforprotein.treebotreasures.Commands;

import me.shakeforprotein.treebotreasures.TreeboTreasures;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TTreasureReload implements CommandExecutor {

    private TreeboTreasures pl;

    public TTreasureReload(TreeboTreasures main){
        this.pl = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        pl.reloadConfig();
        return true;
    }
}

package me.shakeforprotein.treebotreasures.Commands;

import me.shakeforprotein.treebotreasures.TreeboTreasures;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SaveConfig implements CommandExecutor {

    private TreeboTreasures pl;

    public SaveConfig(TreeboTreasures main) {
        this.pl = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        pl.saveConfig();
        return true;
    }
}
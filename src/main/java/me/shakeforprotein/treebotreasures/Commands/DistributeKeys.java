package me.shakeforprotein.treebotreasures.Commands;

import me.shakeforprotein.treebotreasures.TreeboTreasures;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DistributeKeys implements CommandExecutor {
    private TreeboTreasures pl;

    public DistributeKeys(TreeboTreasures main) {
        this.pl = main;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (args.length == 2) {
            if (pl.isNumeric(args[1])) {
                for (Player target : Bukkit.getOnlinePlayers()) {
                    String uuid = target.getUniqueId().toString();
                    String type = args[0].toUpperCase();
                    String amount = args[1];
                    int currentKeys = 0;
                    if (pl.getConfig().get("keys." + uuid + "." + type) != null) {
                        currentKeys = pl.getConfig().getInt("keys." + uuid + "." + type);
                    }
                    int newKeys = currentKeys + Integer.parseInt(args[1]);
                    pl.getConfig().set("keys." + uuid + "." + type, newKeys);
                    target.sendMessage("You have received " + amount + " " + type + " key(s)");
                }
            } else {
                sender.sendMessage(pl.err + " Second argument must be a number.");
            }
            sender.sendMessage(pl.badge + "Successfully Assigned key(s)");
            pl.saveConfig();
        } else {
            sender.sendMessage(pl.badCommand + "/distribuekeys <KeyType> <Quantity>");
        }

        return true;
    }
}

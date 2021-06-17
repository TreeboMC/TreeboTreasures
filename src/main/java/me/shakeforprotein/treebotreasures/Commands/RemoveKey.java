package me.shakeforprotein.treebotreasures.Commands;

import me.shakeforprotein.treebotreasures.TreeboTreasures;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class RemoveKey implements CommandExecutor {

    private TreeboTreasures pl;

    public RemoveKey(TreeboTreasures main){
        this.pl = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(args.length == 3)        {
            if (pl.isNumeric(args[2])) {
                Player target = pl.getServer().getPlayer(args[0]);
                String uuid = target.getUniqueId().toString();
                String name = target.getName();
                String type = args[1].toUpperCase();
                String amount = args[2];
                int currentKeys = 0;
                if(pl.getConfig().get("keys." + uuid + "." + type) != null){
                    currentKeys = pl.getConfig().getInt("keys." + uuid + "." + type);
                }
                int newKeys = currentKeys - Integer.parseInt(amount);
                pl.getConfig().set("keys." + uuid + "." + type, newKeys);
            } else {
                sender.sendMessage(pl.err + " Third argument must be a number.");
            }
            sender.sendMessage(pl.badge + "Successfully Removed key(s)");
            pl.saveConfig();
        }
        else

        {
            sender.sendMessage(pl.badCommand + "/RemoveKey <Playername> <KeyType> <Quantity>");
        }
        return true;
    }
}

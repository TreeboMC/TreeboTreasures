package me.shakeforprotein.treebotreasures.Commands.TabCompleters;

import me.shakeforprotein.treebotreasures.TreeboTreasures;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class TabCompleteShowRewards implements TabCompleter {

    private TreeboTreasures pl;

    public TabCompleteShowRewards(TreeboTreasures main) {
        this.pl = main;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("showrewards")) {
            if (args.length == 1) {
                ArrayList<String> outputStrings = new ArrayList<>();


                    for (String item : pl.getConfig().getConfigurationSection("gui.items").getKeys(false)) {
                        if (item.toLowerCase().startsWith(args[0].toLowerCase())) {
                            outputStrings.add(item);
                        }
                    }


                return outputStrings;
            }
        }
        return null;
    }
}

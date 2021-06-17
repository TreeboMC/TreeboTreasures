package me.shakeforprotein.treebotreasures.Commands;

import me.shakeforprotein.treebotreasures.TreeboTreasures;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

public class KeyParty implements CommandExecutor {

    private TreeboTreasures pl;

    public KeyParty(TreeboTreasures main) {
        this.pl = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (args.length == 2) {

            for (Player p : Bukkit.getOnlinePlayers()) {

                TextComponent announceMessage = new net.md_5.bungee.api.chat.TextComponent(pl.badge + ChatColor.YELLOW + args[0] + ChatColor.RESET + " Has Activated " + ChatColor.GREEN + "Key Party Mode");
                p.spigot().sendMessage(announceMessage);
            }
            int i;
            for(i=0; i < Integer.parseInt(args[1]); i++){
                makeRunnable(args[0], (i*6000) + 100);
            }

        }
        else {
            sender.sendMessage("This requires a username as the first argument");
        }
        return true;
    }

    private void makeRunnable(String user, long delay){
                String keyType = "";
                int counter = 0;
                int keyTypeSelector = ThreadLocalRandom.current().nextInt(0, pl.getConfig().getConfigurationSection("gui.items").getKeys(false).size());
                for(String key : pl.getConfig().getConfigurationSection("gui.items").getKeys(false)){
                    if(keyTypeSelector == counter){
                        keyType = key;
                        break;
                    }
                    else{
                        counter++;
                    }
                }

                int playerSelector = ThreadLocalRandom.current().nextInt(0, Bukkit.getOnlinePlayers().size());
                Player p = (Player) Bukkit.getOnlinePlayers().toArray()[playerSelector];

                Bukkit.getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
                    @Override
                    public void run() {
                        String keyType = "";
                        int counter = 0;
                        int keyTypeSelector = ThreadLocalRandom.current().nextInt(0, pl.getConfig().getConfigurationSection("gui.items").getKeys(false).size());
                        for(String key : pl.getConfig().getConfigurationSection("gui.items").getKeys(false)){
                            if(keyTypeSelector == counter){
                                keyType = key;
                                break;
                            }
                            else{
                                counter++;
                            }
                        }

                        int playerSelector = ThreadLocalRandom.current().nextInt(0, Bukkit.getOnlinePlayers().size());
                        Player p = (Player) Bukkit.getOnlinePlayers().toArray()[playerSelector];

                        for(Player player : Bukkit.getOnlinePlayers()){
                            if (!player.equals(p)){
                                player.sendMessage(pl.badge + ChatColor.YELLOW + p.getName() + ChatColor.RESET + " just recieved 1 " + keyType + " key");
                            }
                        }


                        TextComponent updateMessage = new net.md_5.bungee.api.chat.TextComponent(pl.badge + "Congratulations " + ChatColor.YELLOW + p.getName() + ChatColor.RESET + "! Click here to thank " + ChatColor.GOLD + user);
                        ClickEvent updateClickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/whisper " + user + " Thanks " + user + "!");
                        updateMessage.setClickEvent(updateClickEvent);
                        p.spigot().sendMessage(updateMessage);

                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "addkey " + p.getName() + " " + keyType + " 1");
                    }
                },delay);

    }
}

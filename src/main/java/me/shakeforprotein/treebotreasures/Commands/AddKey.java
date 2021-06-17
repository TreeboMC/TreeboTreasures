package me.shakeforprotein.treebotreasures.Commands;

import me.shakeforprotein.treebotreasures.Listeners.JoinListener;
import me.shakeforprotein.treebotreasures.TreeboTreasures;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;


public class AddKey implements CommandExecutor {

    private TreeboTreasures pl;
    private JoinListener joinListener;

    public AddKey(TreeboTreasures main) {
        this.pl = main;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        joinListener = pl.joinListener;
        if (args.length == 3) {
            if (pl.isNumeric(args[2])) { // received a number as the third input
                OfflinePlayer target = pl.getServer().getOfflinePlayer(args[0]);
                String uuid = target.getUniqueId().toString();
                String name = target.getName();
                String type = args[1].toUpperCase();
                String amount = args[2];
                int currentKeys = 0;
                if (joinListener.joinHash.containsKey(target.getUniqueId())) {
                    if (pl.getConfig().get("keys." + uuid + "." + type) != null) {
                        currentKeys = pl.getConfig().getInt("keys." + uuid + "." + type);
                    }
                    int newKeys = currentKeys + Integer.parseInt(args[2]);
                    pl.getConfig().set("keys." + uuid + "." + type, newKeys);
                    if(target instanceof Player) {
                        if(pl.getConfig().getString("msg.addkey") != null) {
                            ((Player) target).sendMessage(ChatColor.translateAlternateColorCodes('&', pl.getConfig().getString("msg.addkey").replace("%amount%", amount).replace("%type%", type).replace("%badge%", pl.badge)));
                        } else {
                            pl.getConfig().set("msg.addkey", pl.badge + "You have received " + amount + " " + type + " key(s).");
                            ((Player) target).sendMessage(ChatColor.translateAlternateColorCodes('&', pl.badge + "You have received " + amount + " " + type + " key(s)."));
                        }
                    }
                    sender.sendMessage(pl.badge + "Successfully Assigned key(s)");
                    pl.saveConfig();
                }
                else{
                    sender.sendMessage(pl.err + "Player keys not yet loaded. Caching keys for later.");
                    if(pl.getConfig().get("cachedKeys." + uuid + "." + type) != null){
                        pl.getConfig().set("cachedKeys." + uuid + "." + type, Integer.parseInt(args[2]) + pl.getConfig().getInt("cachedKKeys." + uuid + "." + type));
                    }
                    else{
                        pl.getConfig().set("cachedKeys." + uuid + "." + type, Integer.parseInt(args[2]));
                    }
                }
            } else {
                sender.sendMessage(pl.err + " Third argument must be a number.");
            }
        } else {
            sender.sendMessage(pl.badCommand + "/AddKey <Playername> <KeyType> <Quantity>");
        }

        return true;
    }


    private Runnable theRunnable = new Runnable() {
        @Override
        public void run() {

        }
    };

    double c1, c2, c3 = 0;
    public void spiral(Player p, String particle, double a1, double a2, double a3, int o1, String s1){

        c1 = 0;
        c2 = 0;
        c3 = 0;

        theRunnable = new Runnable() {
            @Override
            public void run() {
                if(c2 < 3) {
                    c1 = c1 + a1;
                    c2 = c2 + a2;
                    c3 = c3 + a3;
                    p.getWorld().spawnParticle(Particle.valueOf(particle), p.getLocation().add(Math.cos(c1), c2, Math.sin(c3)), 3);
                    p.getWorld().spawnParticle(Particle.valueOf(particle), p.getLocation().add(Math.sin(c1), c2, Math.cos(c3)), 3);
                    p.getWorld().spawnParticle(Particle.valueOf(particle), p.getLocation().subtract(Math.cos(c1), (c2 - 2), Math.sin(c3)), 3);
                    p.getWorld().spawnParticle(Particle.valueOf(particle), p.getLocation().subtract(Math.sin(c1), (c2 - 2), Math.cos(c3)), 3);

                    Note.Tone tone = Note.Tone.values()[ThreadLocalRandom.current().nextInt(o1, Note.Tone.values().length)];
                    p.getWorld().playSound(p.getLocation(), Sound.valueOf(s1), 3, tone.ordinal());
                    Bukkit.getScheduler().runTaskLater(pl, theRunnable, 2);

                }
                else{
                    c1 = 0;
                    c2 = 0;
                    c3 = 0;
                }
            }
        };

        Bukkit.getScheduler().runTaskLater(pl, theRunnable, 2);
    }
}
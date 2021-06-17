package me.shakeforprotein.treebotreasures;

import me.shakeforprotein.treeboroots.TreeboRoots;
import me.shakeforprotein.treebotreasures.HelpBook.HelpBook;
import me.shakeforprotein.treebotreasures.Commands.*;
import me.shakeforprotein.treebotreasures.Commands.TabCompleters.TabCompleteShowRewards;
import me.shakeforprotein.treebotreasures.Listeners.JoinListener;
import me.shakeforprotein.treebotreasures.Listeners.OpenRewardsGuiListener;
import me.shakeforprotein.treebotreasures.Listeners.TicketSelectGuiListener;
import me.shakeforprotein.treebotreasures.Listeners.ConfigureListener;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public final class TreeboTreasures extends JavaPlugin {


    public static TreeboTreasures instance;
    public TreeboRoots roots;
    public String table = "TreeboTreasures";
    public File playerDataFolder;

    public String badge = ChatColor.translateAlternateColorCodes('&', getConfig().getString("msg.badge") + "");
    public String err = badge + "" + ChatColor.translateAlternateColorCodes('&', getConfig().getString("msg.error") + "");
    public String badCommand = err + ChatColor.translateAlternateColorCodes('&', getConfig().getString("msg.badCommand") + "");


    public JoinListener joinListener = new JoinListener(this);
    public TreeboTreasures main = this;

    @Override
    public void onEnable() {

        if(Bukkit.getPluginManager().getPlugin("TreeboRoots") != null && Bukkit.getPluginManager().getPlugin("TreeboRoots").isEnabled()) {
            instance = this;
            roots = (TreeboRoots) Bukkit.getPluginManager().getPlugin("TreeboRoots");
            this.playerDataFolder = new File(roots.getDataFolder() + File.separator + "PlayerData");

            scheduleRootsIntegrations();

            this.getCommand("addkey").setExecutor(new AddKey(this));
            this.getCommand("removekey").setExecutor(new RemoveKey(this));
            this.getCommand("configurerewards").setExecutor(new ConfigureRewards(this));
            this.getCommand("issuereward").setExecutor(new IssueReward(this));
            this.getCommand("ttreasurereload").setExecutor(new TTreasureReload(this));
            this.getCommand("ttreasuresave").setExecutor(new SaveConfig(this));
            this.getCommand("showrewards").setExecutor(new ShowRewards(this));
            this.getCommand("showrewards").setTabCompleter(new TabCompleteShowRewards(this));
            this.getCommand("distributekeys").setExecutor(new DistributeKeys(this));
            this.getCommand("treasuresgui").setExecutor(new TreasuresGui(this));
            this.getCommand("keyparty").setExecutor(new KeyParty(this));
            this.getCommand("dailyreward").setExecutor(new DailyReward(this));



            getServer().getPluginManager().registerEvents(new ConfigureListener(this), this);
            getServer().getPluginManager().registerEvents(new OpenRewardsGuiListener(this), this);
            getServer().getPluginManager().registerEvents(new TicketSelectGuiListener(this), this);
            getServer().getPluginManager().registerEvents(joinListener, this);


            getConfig().options().copyDefaults(true);
            getConfig().set("version", this.getDescription().getVersion());
            saveConfig();
            createDefaultFiles("dailyRewards.yml");

            if (getConfig().get("bstatsIntegration") != null) {
                if (getConfig().getBoolean("bstatsIntegration")) {
                    Metrics metrics = new Metrics(this);
                }
            }
        } else {
            this.getLogger().warning("Unable to detect dependency TreeboRoots. Disabling " + this.getDescription().getName());
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }


    @Override
    public void onDisable() {
        for (String key : getConfig().getConfigurationSection("keys").getKeys(false)) {
            for (String keyType : getConfig().getConfigurationSection("keys." + key).getKeys(false)) {
                getConfig().set("keys." + key + "." + keyType, null);
            }
            getConfig().set("keys." + key, null);
            saveConfig();
            System.out.println(badge + "Removed " + key + " keys from config.");
        }
    }


    public static boolean isNumeric(String str) {
        return str.matches("\\d+");
    }



    public void createDefaultFiles(String filePath) {
        File userFile = new File(this.getDataFolder(), filePath);
        if (!userFile.exists()) {
            this.saveResource(filePath, false);
        }
    }


    private void scheduleRootsIntegrations(){
        Bukkit.getScheduler().runTaskLater(this, () -> {
            roots.updateHandler.registerPlugin(instance, "TreeboMC", "TreeboTreasures", Material.ENDER_CHEST);
            new HelpBook(instance);
        }, 100L);
    }
}


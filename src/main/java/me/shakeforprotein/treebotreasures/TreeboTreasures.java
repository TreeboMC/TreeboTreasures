package me.shakeforprotein.treebotreasures;

import me.shakeforprotein.treebotreasures.Commands.*;
import me.shakeforprotein.treebotreasures.Commands.TabCompleters.TabCompleteShowRewards;
import me.shakeforprotein.treebotreasures.Guis.RewardsGui;
import me.shakeforprotein.treebotreasures.Listeners.JoinListener;
import me.shakeforprotein.treebotreasures.Listeners.OpenRewardsGuiListener;
import me.shakeforprotein.treebotreasures.Listeners.TicketSelectGuiListener;
import me.shakeforprotein.treebotreasures.Methods.CreateTables;
import me.shakeforprotein.treebotreasures.Methods.DbKeepAlive;
import me.shakeforprotein.treebotreasures.UpdateChecker.UpdateChecker;
import me.shakeforprotein.treebotreasures.Listeners.ConfigureListener;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.sql.*;

public final class TreeboTreasures extends JavaPlugin {


    private UpdateChecker uc;
    private CreateTables createTables = new CreateTables(this);
    private DbKeepAlive dbKeepAlive = new DbKeepAlive(this);
    private RewardsGui rewardsGui = new RewardsGui(this);
    public JoinListener joinListener = new JoinListener(this);
    public TreeboTreasures main = this;

    @Override
    public void onEnable() {
        instance = this;
        // Plugin startup logic

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



        getServer().getPluginManager().registerEvents(new ConfigureListener(this), this);
        getServer().getPluginManager().registerEvents(new OpenRewardsGuiListener(this), this);
        getServer().getPluginManager().registerEvents(new TicketSelectGuiListener(this), this);
        getServer().getPluginManager().registerEvents(joinListener, this);


        getConfig().options().copyDefaults(true);
        getConfig().set("version", this.getDescription().getVersion());
        saveConfig();

        this.uc = new UpdateChecker(this);
        //uc.getCheckDownloadURL();

        host = getConfig().getString("connection.host");
        port = getConfig().getInt("connection.port");
        database = getConfig().getString("connection.database");
        username = getConfig().getString("connection.username");
        password = getConfig().getString("connection.password");
        table = getConfig().getString("connection.table");
        /*
        System.out.println(host);
        System.out.println(port);
        System.out.println(table);
        System.out.println(database);
        System.out.println("password");
        */


        try {
            openConnection();
            Statement statement = connection.createStatement();
            connection.close();
        } catch (ClassNotFoundException e) {
            makeLog(e);
        } catch (SQLException e) {
            makeLog(e);
        }

        /*if (getServer().getName().equalsIgnoreCase("hub")) {
            createTables.createTable();
        }*/
        //dbKeepAlive.dbKeepAlive();
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try {
            connection.close();
        } catch (SQLException e) {
            makeLog(e);
        }
        for(String key : getConfig().getConfigurationSection("keys").getKeys(false)){
            for(String keyType: getConfig().getConfigurationSection("keys." + key).getKeys(false)){
                getConfig().set("keys." + key + "." + keyType, null);
            }
            getConfig().set("keys." + key, null);
            saveConfig();
            System.out.println(badge + "Removed " + key + " keys from config.");
        }
    }

    public static TreeboTreasures instance;

    public Connection connection;
    private String host, database, username, password;
    private int port;
    public String table = getConfig().getString("connection.table");

    public String badge = ChatColor.translateAlternateColorCodes('&', getConfig().getString("msg.badge") + "");
    public String err = badge + "" + ChatColor.translateAlternateColorCodes('&', getConfig().getString("msg.error") + "");
    public String badCommand = err + ChatColor.translateAlternateColorCodes('&', getConfig().getString("msg.badCommand") + "");


    public void openConnection() throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            return;
        }

        synchronized (this) {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
        }
    }

    public void createConnection(){
        try {
            openConnection();
            Statement statement = connection.createStatement();
         } catch (ClassNotFoundException e) {
            makeLog(e);
        } catch (SQLException e) {
            makeLog(e);
        }
    }

    public void closeConnection(){
        try {
            connection.close();
        }  catch (SQLException e) {
            makeLog(e);
        }
    }


    public void makeLog(Exception tr) {
        System.out.println("Creating new log folder - " + new File(this.getDataFolder() + File.separator + "logs").mkdir());
        String dateTimeString = LocalDateTime.now().toString().replace(":", "_").replace("T", "__");
        File file = new File(this.getDataFolder() + File.separator + "logs" + File.separator + dateTimeString + "-" + tr.getCause() + ".log");
        try {
            PrintStream ps = new PrintStream(file);
            tr.printStackTrace(ps);
            System.out.println(this.getDescription().getName() + " - " + this.getDescription().getVersion() + "Encountered Error of type: " + tr.getCause());
            System.out.println("A log file has been generated at " + this.getDataFolder() + File.separator + "logs" + File.separator + dateTimeString + "-" + tr.getCause() + ".log");
            ps.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error creating new log file for " + getDescription().getName() + " - " + getDescription().getVersion());
            System.out.println("Error was as follows");
            System.out.println(e.getMessage());
        }
    }

    public static boolean isNumeric(String str) {
        return str.matches("\\d+");
    }

}

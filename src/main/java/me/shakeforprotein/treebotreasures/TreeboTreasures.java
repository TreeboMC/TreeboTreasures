package me.shakeforprotein.treebotreasures;

import UpdateChecker.UpdateChecker;
import me.shakeforprotein.treebotreasures.Commands.AddKey;
import me.shakeforprotein.treebotreasures.Commands.ConfigureRewards;
import me.shakeforprotein.treebotreasures.Commands.RemoveKey;
import me.shakeforprotein.treebotreasures.Commands.TestRewards;
import me.shakeforprotein.treebotreasures.Listeners.InventoryListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;

public final class TreeboTreasures extends JavaPlugin {


    private UpdateChecker uc;
    private DbKeepAlive dbKeepAlive;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getCommand("addkey").setExecutor(new AddKey(this));
        this.getCommand("removekey").setExecutor(new RemoveKey(this));
        this.getCommand("configurerewards").setExecutor(new ConfigureRewards(this));
        this.getCommand("testrewards").setExecutor(new TestRewards(this));


        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        getConfig().options().copyDefaults(true);
        getConfig().set("version", this.getDescription().getVersion());
        saveConfig();


        this.dbKeepAlive = new DbKeepAlive(this);
        this.uc = new UpdateChecker(this);
        uc.getCheckDownloadURL();

        host = getConfig().getString("host");
        port = getConfig().getInt("port");
        database = getConfig().getString("database");
        username = getConfig().getString("username");
        password = getConfig().getString("password");
        table = getConfig().getString("table");


        try {
            openConnection();
            Statement statement = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
            int doesNothing = 1;
        }

        dbKeepAlive.dbKeepAlive();

    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    public Connection connection;
    private String host, database, username, password;
    private int port;
    public String table = getConfig().getString("table");


    public void openConnection() throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            return;
        }

        synchronized (this) {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password);
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

    public String getServerName(Entity e) {
        String server = getConfig().getString("serverName");
        if (server.toLowerCase().contains("sky")) {
            server = e.getWorld().getName().split("_")[0].split("-")[0];
        }
        return server;
    }



}

package me.shakeforprotein.treebotreasures.Methods;

import me.shakeforprotein.treebotreasures.TreeboTreasures;
import org.bukkit.Bukkit;

import java.sql.SQLException;

public class DbKeepAlive {

    private TreeboTreasures pl;
    public DbKeepAlive(TreeboTreasures main){this.pl = main;}


    public void dbKeepAlive() {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
            public void run() {
                try {
                    pl.connection.createStatement().executeQuery("SELECT * FROM `" + pl.table + "` WHERE ID!='0'");
                    dbKeepAlive();
                } catch (NullPointerException e) {
                    pl.makeLog(e);
                }
                catch (SQLException e){
                    pl.makeLog(e);
                }
            }
        }, 6000L);
    }
}

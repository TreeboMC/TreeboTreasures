package me.shakeforprotein.treebotreasures.Methods;

import me.shakeforprotein.treebotreasures.TreeboTreasures;

import java.sql.SQLException;
import java.util.UUID;

public class CreateTables {

    private TreeboTreasures pl;

    public CreateTables(TreeboTreasures main){
        this.pl = main;
    }

    public boolean createTable(){
        System.out.println("Checking for Server PlayerStatistics Table");
        String serverStatTableCreationQuery =
                "CREATE TABLE IF NOT EXISTS TreeboTreasures (" +
                        "  ID int(11) NOT NULL AUTO_INCREMENT," +
                        "  UUID text NOT NULL," +
                        "  IGNAME text NOT NULL," +
                        "  COMMON int(11) NOT NULL DEFAULT '0'," +
                        "  RARE int(11) NOT NULL DEFAULT '0'," +
                        "  LEGENDARY int(11) NOT NULL DEFAULT '0'," +
                        "  MYTHIC int(11) NOT NULL DEFAULT '0'," +
                        "  PRIMARY KEY (ID)," +
                        "  UNIQUE KEY ID (ID)" +
                        ") ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;";

        int serverStatTableCreationResponse;
        try {
            serverStatTableCreationResponse = pl.connection.createStatement().executeUpdate(serverStatTableCreationQuery);
        } catch (SQLException e) {
            serverStatTableCreationResponse = -1;
            System.out.println("Encountered " + e.toString() + " during createServerStatsTable()");
            pl.makeLog(e);
        }
        System.out.println(serverStatTableCreationResponse + "");
        return true;
    }
}

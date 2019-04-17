package me.shakeforprotein.treebotreasures.Commands;

import me.shakeforprotein.treebotreasures.TreeboTreasures;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class RemoveKey implements CommandExecutor {

    private TreeboTreasures pl;

    public RemoveKey(TreeboTreasures main){
        this.pl = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        pl.createConnection();
        if(args.length == 3){
            if(pl.isNumeric(args[2])){
                String uuid = Bukkit.getOfflinePlayer(args[0]).getUniqueId().toString();
                String name = Bukkit.getOfflinePlayer(args[0]).getName();
                String type = args[1].toUpperCase();
                String amount = args[2];
                String query;

                ResultSet response;

                try {
                    response = pl.connection.createStatement().executeQuery("SELECT Count(*) AS TOTAL FROM `" + pl.table + "` WHERE UUID = '"+ uuid +"'");


                    while (response.next()) {
                        if(response.getInt("TOTAL") == 0){
                            sender.sendMessage(pl.err + "Player " + args[0] + "is not registered in the database and thus has no keys");
                        }

                        else{
                            ResultSet response2 = pl.connection.createStatement().executeQuery("SELECT COUNT(*) AS TOTALROWS FROM `" + pl.table + "` WHERE `UUID` = '" + uuid + "' AND `" + type + "` > " + amount);
                            while (response2.next()) {
                                if (response2.getInt("TOTALROWS") > 0) {
                                    query =  "UPDATE  `"+ pl.table +"` SET  `IGNAME` = '" + name + "',`"+ type + "` = " + type + " - " + amount +"  WHERE `UUID` = '" + uuid +"'";
                                    int response3 = pl.connection.createStatement().executeUpdate(query);
                                    sender.sendMessage("Deducted " + args[2] + " " + args[1] + " keys from " + args[0]);
                                }
                                else{
                                    sender.sendMessage(pl.err + name + " does not have any " + type +" keys");
                                }
                            }
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("Encountered " + e.toString() + " during RemoveKey()");
                    pl.makeLog(e);
                }


            }
            else {
                sender.sendMessage(pl.err + " Third argument must be a number.");
            }
        }
        else{
            sender.sendMessage("Incorrect usage. /RemoveKey <Playername> <KeyType> <Quantity>");
        }
        pl.closeConnection();
        return true;
    }
}

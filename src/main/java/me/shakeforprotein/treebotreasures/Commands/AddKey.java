package me.shakeforprotein.treebotreasures.Commands;

import me.shakeforprotein.treebotreasures.TreeboTreasures;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AddKey implements CommandExecutor {

    private TreeboTreasures pl;

    public AddKey(TreeboTreasures main) {
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
                int newKeys = currentKeys + Integer.parseInt(args[2]);
                pl.getConfig().set("keys." + uuid + "." + type, newKeys);
                target.sendMessage("You have received " + amount + " " + type + " key(s)");
            } else {
                sender.sendMessage(pl.err + " Third argument must be a number.");
            }
            sender.sendMessage(pl.badge + "Successfully Assigned key(s)");
            pl.saveConfig();
        }
        else

        {
            sender.sendMessage(pl.badCommand + "/AddKey <Playername> <KeyType> <Quantity>");
        }

        return true;
    }
/*    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(pl, new Runnable() {
            public void run() {

                pl.createConnection();

                    if(args.length == 3)

            {
                if (pl.isNumeric(args[2])) {
                    String uuid = Bukkit.getOfflinePlayer(args[0]).getUniqueId().toString();
                    String name = Bukkit.getOfflinePlayer(args[0]).getName();
                    String type = args[1].toUpperCase();
                    String amount = args[2];
                    String query;

                    ResultSet response;

                    try {
                        query = "SELECT Count(*) AS TOTAL FROM `" + pl.table + "` WHERE UUID = '" + uuid + "'";
                        response = pl.connection.createStatement().executeQuery(query);


                        while (response.next()) {
                            if (response.getInt("TOTAL") == 0) {
                                query = "INSERT INTO `" + pl.table + "`(`UUID`, `IGNAME`, `" + type + "`) VALUES  ('" + uuid + "','" + name + "','" + amount + "')";
                                sender.sendMessage("Adding " + amount + " key(s) to player " + name);
                                int response2 = 456456456;
                                response2 = pl.connection.createStatement().executeUpdate(query);

                                if (response2 != 456456456) {
                                    sender.sendMessage("Assigning key Successful");
                                } else {
                                    sender.sendMessage("Assigning key Failed with response code " + response2);
                                }
                            } else {
                                query = "UPDATE  `" + pl.table + "` SET  `IGNAME` = '" + name + "',`" + type + "` = " + type + " + " + amount + "  WHERE  `UUID` = '" + uuid + "'";
                                int response2 = 456456456;
                                response2 = pl.connection.createStatement().executeUpdate(query);

                                if (response2 != 456456456) {
                                    sender.sendMessage("Assigning key Successful");
                                } else {
                                    sender.sendMessage("Assigning key Failed with response code " + response2);
                                }
                            }
                        }
                    } catch (SQLException e) {
                        System.out.println("Encountered " + e.toString() + " during AddKey()");
                        pl.makeLog(e);
                    }


                } else {
                    sender.sendMessage(pl.err + " Third argument must be a number.");
                }
            }
        else

            {
                sender.sendMessage(pl.badCommand + "/AddKey <Playername> <KeyType> <Quantity>");
            }
        pl.closeConnection();
            }
        });
        return true;
    }

 */
}

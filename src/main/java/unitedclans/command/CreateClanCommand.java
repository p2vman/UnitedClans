package unitedclans.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import unitedclans.UnitedClans;

import java.sql.*;
import java.util.*;

public class CreateClanCommand implements CommandExecutor {
    private Connection con;
    public CreateClanCommand(Connection con) {
        this.con = con;
    }

    @Override
    public boolean onCommand( CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 0 || args.length >= 2) {
            sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.invalidcommand"));
            return false;
        }
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        String clanNameInput = args[0];
        if (clanNameInput == null) {
            sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.wrongclanname"));
            return true;
        }
        try {
            Statement stmt = con.createStatement();
            ResultSet rsNumberClans = stmt.executeQuery("SELECT ClanID FROM CLANS WHERE ClanID IS (SELECT MAX(ClanID) FROM CLANS);");
            Integer NumberClans = rsNumberClans.getInt("ClanID") + 1;

            ResultSet rsChekerPlayer = stmt.executeQuery("SELECT * FROM PLAYERS WHERE UUID IS '" + uuid + "';");
            if (rsChekerPlayer.getInt("ClanID") != 0) {
                sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.youmemberclan"));
                return true;
            }

            ResultSet rsChekerClanName = stmt.executeQuery("SELECT * FROM CLANS WHERE ClanName IS '" + clanNameInput + "';");
            if (rsChekerClanName.next()) {
                sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.clannametaken"));
                return true;
            }

            String tableCLANS = "INSERT INTO CLANS (ClanID, ClanName) " +
                    "VALUES ('" + NumberClans + "', '" + clanNameInput + "');";
            stmt.executeUpdate(tableCLANS);
            String tablePLAYERS1 = "UPDATE PLAYERS SET ClanID = '" + NumberClans + "' WHERE UUID IS '" + uuid + "';";
            stmt.executeUpdate(tablePLAYERS1);
            String tablePLAYERS2 = "UPDATE PLAYERS SET ClanRole = '" + UnitedClans.getInstance().getConfig().getString("roles.leader") + "' WHERE UUID IS '" + uuid + "';";
            stmt.executeUpdate(tablePLAYERS2);
            stmt.close();
            sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.successcreateclan"));
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return true;
    }
}
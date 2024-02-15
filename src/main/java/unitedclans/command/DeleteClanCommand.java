package unitedclans.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import unitedclans.UnitedClans;

import java.sql.*;
import java.util.*;

public class DeleteClanCommand implements CommandExecutor {
    private Connection con;
    public DeleteClanCommand(Connection con) {
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
        try {
            Statement stmt = con.createStatement();
            ResultSet rsgetClanName = stmt.executeQuery("SELECT ClanName FROM CLANS WHERE ClanName IS '" + clanNameInput + "';");
            String getClanName = rsgetClanName.getString("ClanName");
            if (getClanName == null) {
                sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.wrongclanname"));
                return true;
            }
            ResultSet rsgetClanID = stmt.executeQuery("SELECT ClanID FROM PLAYERS WHERE UUID IS '" + uuid + "';");
            Integer getClanID = rsgetClanID.getInt("ClanID");
            if (getClanID == 0) {
                sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.younotmemberclan"));
                return true;
            }
            ResultSet rsClanID = stmt.executeQuery("SELECT ClanID FROM CLANS WHERE ClanName IS '" + getClanName + "';");
            Integer ClanID = rsClanID.getInt("ClanID");
            if (ClanID != getClanID) {
                sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.younotmemberthisclan"));
                return true;
            }
            ResultSet rsgetLeaderUUID = stmt.executeQuery("SELECT ClanRole FROM PLAYERS WHERE UUID IS '" + uuid + "';");
            String getLeaderUUID = rsgetLeaderUUID.getString("ClanRole");
            if (!Objects.equals(getLeaderUUID, UnitedClans.getInstance().getConfig().getString("roles.leader"))) {
                sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.notleader"));
                return true;
            }
            ResultSet rsgetUUID = stmt.executeQuery("SELECT UUID FROM PLAYERS WHERE ClanID IS " + ClanID + ";");
            while (rsgetUUID.next()) {
                String uuidget = rsgetUUID.getString("UUID");
                String tablePLAYERS1 = "UPDATE PLAYERS SET ClanID = " + 0 + " WHERE UUID IS '" + uuidget + "';";
                stmt.executeUpdate(tablePLAYERS1);
                String tablePLAYERS2 = "UPDATE PLAYERS SET ClanRole = '" + UnitedClans.getInstance().getConfig().getString("roles.noclan") + "' WHERE UUID IS '" + uuidget + "';";
                stmt.executeUpdate(tablePLAYERS2);
            }
            String tableCLANS = "DELETE FROM CLANS WHERE ClanID IS '" + ClanID + "';";
            stmt.executeUpdate(tableCLANS);
            stmt.close();
            sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.successdeleteclan"));
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return true;
    }
}
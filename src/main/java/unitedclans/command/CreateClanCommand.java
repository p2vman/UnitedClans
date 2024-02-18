package unitedclans.command;

import org.bukkit.Sound;
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
        Player playerSender = (Player) sender;
        UUID uuid = playerSender.getUniqueId();
        String clanNameInput = args[0];
        if (args.length <= 0 || args.length >= 2) {
            sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.invalidcommand"));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
            return false;
        }
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
                playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                return true;
            }

            ResultSet rsChekerClanName = stmt.executeQuery("SELECT * FROM CLANS WHERE ClanName IS '" + clanNameInput + "';");
            if (rsChekerClanName.next()) {
                sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.clannametaken"));
                playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                return true;
            }

            String tableCLANS = "INSERT INTO CLANS (ClanID, ClanName) " +
                    "VALUES ('" + NumberClans + "', '" + clanNameInput + "');";
            stmt.executeUpdate(tableCLANS);
            String tablePLAYERS = "UPDATE PLAYERS SET ClanID = " + NumberClans + ", ClanRole = '" + UnitedClans.getInstance().getConfig().getString("roles.leader") + "' WHERE UUID IS '" + uuid + "';";
            stmt.executeUpdate(tablePLAYERS);
            stmt.close();
            String createclanmsg = UnitedClans.getInstance().getConfig().getString("messages.successcreateclan");
            sender.sendMessage(createclanmsg.replace("%clan%", clanNameInput));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return true;
    }
}
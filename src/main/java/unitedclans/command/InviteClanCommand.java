package unitedclans.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import unitedclans.UnitedClans;

import java.sql.*;
import java.util.*;

public class InviteClanCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private Connection con;
    public InviteClanCommand(JavaPlugin plugin, Connection con) {
        this.plugin = plugin;
        this.con = con;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 0 || args.length >= 2) {
            sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.invalidcommand"));
            return false;
        }
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        String playerNameInput = args[0];
        Player playerName = plugin.getServer().getPlayer(playerNameInput);
        if (playerName == null) {
            sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.wrongplayername"));
            return true;
        }
        if (uuid == playerName.getUniqueId()) {
            sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.sendinvitationyourself"));
            return true;
        }
        try {
            Statement stmt = con.createStatement();
            ResultSet rsInvitedPlayer = stmt.executeQuery("SELECT * FROM PLAYERS WHERE UUID IS '" + playerName.getUniqueId() + "';");
            if (rsInvitedPlayer.getInt("ClanID") != 0) {
                sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.playermemberclan"));
                return true;
            }
            ResultSet rsSender = stmt.executeQuery("SELECT * FROM PLAYERS WHERE UUID IS '" + uuid + "';");
            String getRoleUUID = rsSender.getString("ClanRole");
            Integer getClanID = rsSender.getInt("ClanID");
            if (!Objects.equals(getRoleUUID, UnitedClans.getInstance().getConfig().getString("roles.leader"))) {
                sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.notleader"));
                return true;
            }
            ResultSet rsInviteCheker = stmt.executeQuery("SELECT * FROM INVITATIONS WHERE UUID IS '" + playerName.getUniqueId() + "';");
            if (rsInviteCheker.next()) {
                sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.alreadysentinvitation"));
                return true;
            }

            Timestamp timeFirst = new Timestamp(System.currentTimeMillis());
            String tableINVITATIONS1 = "INSERT INTO INVITATIONS (UUID, PlayerName, ClanID) " +
                    "VALUES ('" + playerName.getUniqueId() + "', '" + playerName.getName() + "', " + getClanID + ");";
            stmt.executeUpdate(tableINVITATIONS1);

            ResultSet rsClanName = stmt.executeQuery("SELECT ClanName FROM CLANS WHERE ClanID IS " + getClanID + ";");
            String invitationmsg = UnitedClans.getInstance().getConfig().getString("messages.invitation");
            playerName.sendMessage(invitationmsg.replace("%clan%",rsClanName.getString("ClanName")).replace("%player%",sender.getName()));
            sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.invitationsent"));
            stmt.close();
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    try {
                        Statement stmt = con.createStatement();
                        String tableINVITATIONS2 = "DELETE FROM INVITATIONS WHERE UUID IS '" + playerName.getUniqueId() + "'";
                        stmt.executeUpdate(tableINVITATIONS2);
                        stmt.close();
                    } catch (Exception e) {
                        System.err.println(e.getClass().getName() + ": " + e.getMessage());
                    }
                }
            }, 1200);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return true;
    }
}


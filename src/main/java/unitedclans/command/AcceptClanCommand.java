package unitedclans.command;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import unitedclans.UnitedClans;
import unitedclans.utils.ShowClanUtils;

import java.sql.*;
import java.util.*;

public class AcceptClanCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private Connection con;
    public AcceptClanCommand(JavaPlugin plugin, Connection con) {
        this.plugin = plugin;
        this.con = con;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player playerSender = (Player) sender;
        UUID uuid = playerSender.getUniqueId();
        if (args.length >= 1) {
            sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.invalidcommand"));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
            return false;
        }
        try {
            Statement stmt = con.createStatement();
            ResultSet rsInvitation = stmt.executeQuery("SELECT * FROM INVITATIONS WHERE UUID IS '" + uuid + "';");
            Integer ClanID = rsInvitation.getInt("ClanID");
            if (!rsInvitation.next()) {
                sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.younotinvited"));
                playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                return true;
            }

            ResultSet rsClanPlayers = stmt.executeQuery("SELECT * FROM PLAYERS WHERE ClanID IS " + ClanID + ";");
            String playerjoinedmsg = UnitedClans.getInstance().getConfig().getString("messages.playerjoined");
            while (rsClanPlayers.next()) {
                String playerName = rsClanPlayers.getString("PlayerName");
                Player playerClan = plugin.getServer().getPlayer(playerName);
                playerClan.sendMessage(playerjoinedmsg.replace("%player%",playerSender.getName()));
            }
            ResultSet rsClanName = stmt.executeQuery("SELECT * FROM CLANS WHERE ClanID IS " + ClanID + ";");
            String clanName = rsClanName.getString("ClanName");
            String joinedclanmsg = UnitedClans.getInstance().getConfig().getString("messages.successfullyjoinedclan");
            sender.sendMessage(joinedclanmsg.replace("%clan%",clanName));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

            stmt.executeUpdate("UPDATE PLAYERS SET ClanID = " + ClanID + ", ClanRole = '" + UnitedClans.getInstance().getConfig().getString("roles.member") + "' WHERE UUID IS '" + uuid + "';");
            stmt.executeUpdate("DELETE FROM INVITATIONS WHERE UUID IS '" + uuid + "'");
            stmt.close();

            ShowClanUtils.showClan(plugin, con);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return true;
    }
}
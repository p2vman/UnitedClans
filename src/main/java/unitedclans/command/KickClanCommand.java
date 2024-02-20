package unitedclans.command;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import unitedclans.UnitedClans;

import java.sql.*;
import java.util.*;

public class KickClanCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private Connection con;
    public KickClanCommand(JavaPlugin plugin, Connection con) {
        this.plugin = plugin;
        this.con = con;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player playerSender = (Player) sender;
        UUID uuid = playerSender.getUniqueId();
        if (args.length <= 0 || args.length >= 2) {
            sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.invalidcommand"));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
            return false;
        }
        String playerName = args[0];
        if (playerName == null) {
            sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.wrongplayername"));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
            return true;
        }
        try {
            Statement stmt = con.createStatement();
            ResultSet rsKickedPlayer = stmt.executeQuery("SELECT * FROM PLAYERS WHERE PlayerName IS '" + playerName + "';");
            String KickedPlayerRole = rsKickedPlayer.getString("ClanRole");
            Integer KickedPlayerClanID = rsKickedPlayer.getInt("ClanID");
            if (!rsKickedPlayer.next()) {
                sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.wrongplayername"));
                playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                return true;
            }
            if (Objects.equals(playerSender.getName(), playerName)) {
                sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.kickyourself"));
                playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                return true;
            }
            ResultSet rsSender = stmt.executeQuery("SELECT * FROM PLAYERS WHERE UUID IS '" + uuid + "';");
            String getRoleUUID = rsSender.getString("ClanRole");
            Integer getClanID = rsSender.getInt("ClanID");
            if (KickedPlayerClanID != getClanID) {
                sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.playernotyourclan"));
                playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                return true;
            }
            if (!Objects.equals(getRoleUUID, UnitedClans.getInstance().getConfig().getString("roles.leader")) && !Objects.equals(getRoleUUID, UnitedClans.getInstance().getConfig().getString("roles.elder"))) {
                sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.norightskick"));
                playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                return true;
            }
            if (Objects.equals(KickedPlayerRole, getRoleUUID) || Objects.equals(KickedPlayerRole, UnitedClans.getInstance().getConfig().getString("roles.leader"))) {
                sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.roleishigher"));
                playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                return true;
            }

            String tablePLAYERS = "UPDATE PLAYERS SET ClanID = " + 0 + ", ClanRole = '" + UnitedClans.getInstance().getConfig().getString("roles.noclan") + "' WHERE PlayerName IS '" + playerName + "';";
            stmt.executeUpdate(tablePLAYERS);

            ResultSet rsClanPlayers = stmt.executeQuery("SELECT * FROM PLAYERS WHERE ClanID IS " + getClanID + ";");
            String playerkickedmsg = UnitedClans.getInstance().getConfig().getString("messages.playerwaskicked");
            while (rsClanPlayers.next()) {
                String playerNameClan = rsClanPlayers.getString("PlayerName");
                Player playerClan = plugin.getServer().getPlayer(playerNameClan);
                playerClan.sendMessage(playerkickedmsg.replace("%player%",playerName));
            }
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            Player argPlayerName = plugin.getServer().getPlayer(playerName);
            if (argPlayerName != null) {
                ResultSet rsClanName = stmt.executeQuery("SELECT * FROM CLANS WHERE ClanID IS " + KickedPlayerClanID + ";");
                String KickedPlayerClanName = rsClanName.getString("ClanName");
                String youwaskickedmsg = UnitedClans.getInstance().getConfig().getString("messages.youwaskicked");
                argPlayerName.sendMessage(youwaskickedmsg.replace("%clan%",KickedPlayerClanName));
                argPlayerName.playSound(argPlayerName.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            }
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return true;
    }
}


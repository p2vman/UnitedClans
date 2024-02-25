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

public class LeaveClanCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private Connection con;
    public LeaveClanCommand(JavaPlugin plugin, Connection con) {
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
            ResultSet rsLeavingPlayer = stmt.executeQuery("SELECT * FROM PLAYERS WHERE UUID IS '" + uuid + "';");
            String LeavingPlayerRole = rsLeavingPlayer.getString("ClanRole");
            Integer LeavingPlayerClanID = rsLeavingPlayer.getInt("ClanID");
            if (LeavingPlayerClanID == 0) {
                sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.younotmemberclan"));
                playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                return true;
            }
            if (Objects.equals(LeavingPlayerRole, UnitedClans.getInstance().getConfig().getString("roles.leader"))) {
                sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.youleader"));
                playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                return true;
            }

            stmt.executeUpdate("UPDATE PLAYERS SET ClanID = " + 0 + ", ClanRole = '" + UnitedClans.getInstance().getConfig().getString("roles.noclan") + "' WHERE UUID IS '" + uuid + "';");

            ResultSet rsClanPlayers = stmt.executeQuery("SELECT * FROM PLAYERS WHERE ClanID IS " + LeavingPlayerClanID + ";");
            String playerleavemsg = UnitedClans.getInstance().getConfig().getString("messages.playerleave");
            while (rsClanPlayers.next()) {
                String playerNameClan = rsClanPlayers.getString("PlayerName");
                Player playerClan = plugin.getServer().getPlayer(playerNameClan);
                playerClan.sendMessage(playerleavemsg.replace("%player%",playerSender.getName()));
            }
            ResultSet rsClanName = stmt.executeQuery("SELECT * FROM CLANS WHERE ClanID IS " + LeavingPlayerClanID + ";");
            String LeavingPlayerClanName = rsClanName.getString("ClanName");
            String successfullyleftmsg = UnitedClans.getInstance().getConfig().getString("messages.successfullyleft");
            playerSender.sendMessage(successfullyleftmsg.replace("%clan%",LeavingPlayerClanName));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            stmt.close();

            ShowClanUtils.showClan(plugin, con);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return true;
    }
}
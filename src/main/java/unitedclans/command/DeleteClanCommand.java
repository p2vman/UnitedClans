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

public class DeleteClanCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private Connection con;
    public DeleteClanCommand(JavaPlugin plugin, Connection con) {
        this.plugin = plugin;
        this.con = con;
    }

    @Override
    public boolean onCommand( CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return true;
        Player playerSender = (Player) sender;
        UUID uuid = playerSender.getUniqueId();
        String clanNameInput = args[0];
        if (args.length <= 0 || args.length >= 2) {
            sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.invalidcommand"));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
            return false;
        }
        try {
            Statement stmt = con.createStatement();
            ResultSet rsgetClan = stmt.executeQuery("SELECT * FROM CLANS WHERE ClanName IS '" + clanNameInput + "'");
            String getClanName = rsgetClan.getString("ClanName");
            Integer getClanID = rsgetClan.getInt("ClanID");

            ResultSet rsgetClanPlayer = stmt.executeQuery("SELECT * FROM PLAYERS WHERE UUID IS '" + uuid + "'");
            Integer getClanIDPlayer = rsgetClanPlayer.getInt("ClanID");
            String getLeaderUUID = rsgetClanPlayer.getString("ClanRole");

            if (getClanName == null) {
                sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.wrongclanname"));
                playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                return true;
            }
            if (getClanIDPlayer == 0) {
                sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.younotmemberclan"));
                playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                return true;
            }
            if (getClanID != getClanIDPlayer) {
                sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.younotmemberthisclan"));
                playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                return true;
            }
            if (!Objects.equals(getLeaderUUID, UnitedClans.getInstance().getConfig().getString("roles.leader"))) {
                sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.notleader"));
                playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                return true;
            }

            ResultSet rsUUID = stmt.executeQuery("SELECT * FROM PLAYERS WHERE ClanID IS " + getClanIDPlayer);
            while (rsUUID.next()) {
                String playerName = rsUUID.getString("PlayerName");
                if (Objects.equals(playerName, playerSender.getName())) {
                    continue;
                }
                Player playerClan = plugin.getServer().getPlayer(playerName);
                playerClan.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.leaderdeleteclan"));
                playerClan.playSound(playerClan.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            }

            stmt.executeUpdate("UPDATE PLAYERS SET ClanID = " + 0 + ", ClanRole = '" + UnitedClans.getInstance().getConfig().getString("roles.noclan") + "' WHERE ClanID IS " + getClanID);
            stmt.executeUpdate("DELETE FROM CLANS WHERE ClanID IS " + getClanID);
            stmt.close();

            ShowClanUtils.showClan(plugin, con);

            String deleteclanmsg = UnitedClans.getInstance().getConfig().getString("messages.successdeleteclan");
            sender.sendMessage(deleteclanmsg.replace("%clan%", getClanName));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return true;
    }
}
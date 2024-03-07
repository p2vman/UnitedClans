package unitedclans.command;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import unitedclans.UnitedClans;
import unitedclans.utils.LocalizationUtils;
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
        if(!(sender instanceof Player)) return true;
        String language = UnitedClans.getInstance().getConfig().getString("lang");
        Player playerSender = (Player) sender;
        UUID uuid = playerSender.getUniqueId();
        if (args.length >= 1) {
            sender.sendMessage(LocalizationUtils.langCheck(language, "INVALID_COMMAND"));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
            return false;
        }
        try {
            Statement stmt = con.createStatement();
            ResultSet rsInvitation = stmt.executeQuery("SELECT * FROM INVITATIONS WHERE UUID IS '" + uuid + "'");
            Integer ClanID = rsInvitation.getInt("ClanID");
            if (!rsInvitation.next()) {
                sender.sendMessage(LocalizationUtils.langCheck(language, "YOU_NOT_INVITED"));
                playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                return true;
            }
            ResultSet rsClan = stmt.executeQuery("SELECT * FROM CLANS WHERE ClanID IS " + ClanID);
            Integer countMembers = rsClan.getInt("CountMembers");
            String clanName = rsClan.getString("ClanName");
            if (countMembers >= 25) {
                sender.sendMessage(LocalizationUtils.langCheck(language, "THIS_CLAN_MAX"));
                playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                return true;
            }

            ResultSet rsClanPlayers = stmt.executeQuery("SELECT * FROM PLAYERS WHERE ClanID IS " + ClanID);
            String playerjoinedmsg = LocalizationUtils.langCheck(language, "PLAYER_JOINED");
            while (rsClanPlayers.next()) {
                String playerName = rsClanPlayers.getString("PlayerName");
                Player playerClan = plugin.getServer().getPlayer(playerName);
                playerClan.sendMessage(playerjoinedmsg.replace("%player%", playerSender.getName()));
            }
            String joinedclanmsg = LocalizationUtils.langCheck(language, "SUCCESSFULLY_JOINED_CLAN");
            sender.sendMessage(joinedclanmsg.replace("%clan%", clanName));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

            stmt.executeUpdate("UPDATE PLAYERS SET ClanID = " + ClanID + ", ClanRole = '" + UnitedClans.getInstance().getConfig().getString("roles.member") + "' WHERE UUID IS '" + uuid + "'");
            stmt.executeUpdate("UPDATE CLANS SET CountMembers = CountMembers + 1 WHERE ClanID IS " + ClanID);
            stmt.executeUpdate("DELETE FROM INVITATIONS WHERE UUID IS '" + uuid + "'");
            stmt.close();

            ShowClanUtils.showClan(plugin, con);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return true;
    }
}
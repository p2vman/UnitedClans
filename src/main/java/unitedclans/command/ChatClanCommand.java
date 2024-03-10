package unitedclans.command;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import unitedclans.UnitedClans;
import unitedclans.utils.LocalizationUtils;

import java.sql.*;
import java.util.*;

public class ChatClanCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private Connection con;
    public ChatClanCommand(JavaPlugin plugin, Connection con) {
        this.plugin = plugin;
        this.con = con;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return true;
        String language = UnitedClans.getInstance().getConfig().getString("lang");
        Player playerSender = (Player) sender;
        UUID uuid = playerSender.getUniqueId();
        if (args.length < 1) {
            sender.sendMessage(LocalizationUtils.langCheck(language, "INVALID_COMMAND"));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
            return false;
        }

        try {
            Statement stmt = con.createStatement();
            ResultSet rsPlayerSender = stmt.executeQuery( "SELECT * FROM PLAYERS WHERE UUID IS '" + uuid + "'");
            Integer senderClanID = rsPlayerSender.getInt("ClanID");
            if (senderClanID == 0) {
                sender.sendMessage(LocalizationUtils.langCheck(language, "YOU_NOT_MEMBER_CLAN"));
                playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 1.0f);
                return true;
            }

            StringBuilder message = new StringBuilder();
            for (int i=0; i<args.length; i++) {
                message.append(" " + args[i]);
            }
            String messagepattern = UnitedClans.getInstance().getConfig().getString("clan-msg-pattern");
            ResultSet rsClan = stmt.executeQuery( "SELECT * FROM CLANS WHERE ClanID IS " + senderClanID);
            String clanName = rsClan.getString("ClanName");
            String clanColor = rsClan.getString("ClanColor");
            ResultSet rsClanPlayers = stmt.executeQuery( "SELECT * FROM PLAYERS WHERE ClanID IS " + senderClanID);
            while (rsClanPlayers.next()) {
                String playerNameClan = rsClanPlayers.getString("PlayerName");
                Player playerClan = plugin.getServer().getPlayer(playerNameClan);
                playerClan.sendMessage(messagepattern.replace("%clan%", ChatColor.valueOf(clanColor) + (ChatColor.BOLD + clanName + ChatColor.RESET)).replace("%sender%", playerSender.getName()).replace("%message%", message));
            }
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return true;
    }
}
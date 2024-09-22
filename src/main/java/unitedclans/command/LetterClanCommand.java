package unitedclans.command;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import unitedclans.UnitedClans;
import unitedclans.utils.GeneralUtils;
import unitedclans.utils.LocalizationUtils;
import unitedclans.utils.DatabaseDriver;

import java.util.*;

public class LetterClanCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final DatabaseDriver dbDriver;

    public LetterClanCommand(JavaPlugin plugin, DatabaseDriver dbDriver) {
        this.plugin = plugin;
        this.dbDriver = dbDriver;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return true;
        String language = UnitedClans.getInstance().getConfig().getString("lang");
        Player playerSender = (Player) sender;
        UUID uuid = playerSender.getUniqueId();

        List<Map<String, Object>> rsPlayerSender = dbDriver.selectData("clan_role, clan_id", "players", "WHERE uuid = ?", uuid);
        String senderClanRole = (String) rsPlayerSender.get(0).get("clan_role");
        int senderClanID = (int) rsPlayerSender.get(0).get("clan_id");
        if (senderClanID == 0) {
            return GeneralUtils.checkUtil(playerSender, language, "YOU_NOT_MEMBER_CLAN", true);
        }

        if (args.length < 1) {
            List<Map<String, Object>> rsLetter = dbDriver.selectData("letter", "letters", "WHERE clan_id = ?", senderClanID);
            String msgLetter = (String) rsLetter.get(0).get("letter");

            if (Objects.equals(msgLetter, "null")) {
                return GeneralUtils.checkUtil(playerSender, language, "NO_LETTER_WRITTEN", true);
            }

            String msgLetterPattern = LocalizationUtils.langCheck(language, "LETTER_MESSAGE");
            sender.sendMessage(msgLetterPattern.replace("%letter%", msgLetter));
            playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

            Map<String, Object> updateMapPlayers = new HashMap<>();
            updateMapPlayers.put("letter_read", 0);
            dbDriver.updateData("players", updateMapPlayers, "uuid = ?", uuid);

            return true;
        }

        if (!Objects.equals(senderClanRole, UnitedClans.getInstance().getConfig().getString("roles.leader"))) {
            return GeneralUtils.checkUtil(playerSender, language, "NO_RIGHTS_LETTER", true);
        }

        StringBuilder letter = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            letter.append(" " + args[i]);
        }

        List<Map<String, Object>> rsPlayerClan = dbDriver.selectData("player_name", "players", "WHERE clan_id = ?", senderClanID);
        for (Map<String, Object> i : rsPlayerClan) {
            String playerName = (String) i.get("player_name");
            Player playerClan = plugin.getServer().getPlayer(playerName);
            if (playerClan == null || playerClan == playerSender) {
                continue;
            }

            playerClan.sendMessage(LocalizationUtils.langCheck(language, "UNREAD_LETTER_MESSAGE"));
            playerClan.playSound(playerClan.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }

        Map<String, Object> updateMapFirstPlayers = new HashMap<>();
        updateMapFirstPlayers.put("letter_read", 1);
        dbDriver.updateData("players", updateMapFirstPlayers, "clan_id = ?", senderClanID);
        Map<String, Object> updateMapSecondPlayers = new HashMap<>();
        updateMapSecondPlayers.put("letter_read", 0);
        dbDriver.updateData("players", updateMapSecondPlayers, "uuid = ?", uuid);
        Map<String, Object> updateMapLetters = new HashMap<>();
        updateMapLetters.put("letter", letter);
        dbDriver.updateData("letters", updateMapLetters, "clan_id = ?", senderClanID);

        sender.sendMessage(LocalizationUtils.langCheck(language, "SUCCESSFULLY_LETTER_MESSAGE"));
        playerSender.playSound(playerSender.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

        List<Map<String, Object>> rsClan = dbDriver.selectData("clan_name", "clans", "WHERE clan_id = ?", senderClanID);
        String ClanName = (String) rsClan.get(0).get("clan_name");

        plugin.getServer().getLogger().info("[UnitedClans] " + playerSender.getName() + " wrote a letter to the " + ClanName + " clan");

        return true;
    }
}

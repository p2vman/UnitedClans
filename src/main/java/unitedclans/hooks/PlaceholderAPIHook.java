package unitedclans.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import unitedclans.UnitedClans;
import unitedclans.utils.LocalizationUtils;
import unitedclans.utils.SqliteDriver;

import java.util.*;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    private final JavaPlugin plugin;
    private SqliteDriver sql;
    public PlaceholderAPIHook(JavaPlugin plugin, SqliteDriver sql) {
        this.plugin = plugin;
        this.sql = sql;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "unitedclans";
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().get(0);
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        String language = UnitedClans.getInstance().getConfig().getString("lang");

        if (offlinePlayer != null && offlinePlayer.isOnline()) {
            Player player = offlinePlayer.getPlayer();

            if (params.equals("clanName")) {
                try {
                    List<Map<String, Object>> rsClanID = sql.sqlSelectData("ClanID", "PLAYERS", "UUID = '" + player.getUniqueId() + "'");
                    Integer ClanID = (Integer) rsClanID.get(0).get("ClanID");

                    if (ClanID != 0) {
                        List<Map<String, Object>> rsClan = sql.sqlSelectData("ClanName, ClanColor", "CLANS", "ClanID = " + ClanID);
                        String ClanName = (String) rsClan.get(0).get("ClanName");
                        String ClanColor = (String) rsClan.get(0).get("ClanColor");

                        return ChatColor.valueOf(ClanColor) + (ChatColor.BOLD + ClanName + ChatColor.RESET) + " ";
                    }

                    return "";
                } catch (Exception e) {
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                }
            } else if (params.equals("clanRole")) {
                try {
                    List<Map<String, Object>> rsClanID = sql.sqlSelectData("ClanRole", "PLAYERS", "UUID = '" + player.getUniqueId() + "'");
                    String ClanRole = (String) rsClanID.get(0).get("ClanRole");

                    if (!ClanRole.equals(UnitedClans.getInstance().getConfig().getString("roles.no-clan"))) {
                        String color = "WHITE";
                        String role = "";
                        if (ClanRole.equals(UnitedClans.getInstance().getConfig().getString("roles.member"))) {
                            color = "GREEN";
                            role = LocalizationUtils.langCheck(language, "MEMBER");
                        } else if (ClanRole.equals(UnitedClans.getInstance().getConfig().getString("roles.elder"))) {
                            color = "GOLD";
                            role = LocalizationUtils.langCheck(language, "ELDER");
                        } else if (ClanRole.equals(UnitedClans.getInstance().getConfig().getString("roles.leader"))) {
                            color = "DARK_PURPLE";
                            role = LocalizationUtils.langCheck(language, "LEADER");
                        }
                        return ChatColor.valueOf(color) + role + ChatColor.RESET;
                    }

                    return "";
                } catch (Exception e) {
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                }
            }
        }
        return null;
    }
}
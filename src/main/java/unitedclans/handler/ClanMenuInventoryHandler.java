package unitedclans.handler;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import unitedclans.UnitedClans;
import unitedclans.utils.GeneralUtils;
import unitedclans.utils.LocalizationUtils;
import unitedclans.utils.MenuClanUtils;
import unitedclans.utils.SqliteDriver;

import java.util.*;


public class ClanMenuInventoryHandler implements Listener {
    private final JavaPlugin plugin;
    private SqliteDriver sql;
    private String selectedPlayerName;
    public ClanMenuInventoryHandler(JavaPlugin plugin, SqliteDriver sql) {
        this.plugin = plugin;
        this.sql = sql;
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        String language = UnitedClans.getInstance().getConfig().getString("lang");
        Player player = (Player) event.getWhoClicked();

        if(event.getView().getTitle().equalsIgnoreCase(ChatColor.BOLD + LocalizationUtils.langCheck(language, "CLAN_MENU"))) {
            if (event.getCurrentItem() == null) {

            } else if (event.getCurrentItem().getType() == Material.PLAYER_HEAD) {
                MenuClanUtils.openMembersMenu(player, sql);
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
            } else if (event.getCurrentItem().getType() == Material.GOLD_BLOCK) {

            } else if (event.getCurrentItem().getType() == Material.BEACON) {

            }
            event.setCancelled(true);
        } else if (event.getView().getTitle().equalsIgnoreCase(ChatColor.BOLD + LocalizationUtils.langCheck(language, "MEMBERS_MENU"))) {
            if (event.getCurrentItem() == null) {

            } else if (event.getCurrentItem().getType() == Material.PLAYER_HEAD) {
                selectedPlayerName = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
                MenuClanUtils.openMemberMenu(player, sql, selectedPlayerName);
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
            } else if (event.getCurrentItem().getType() == Material.STRUCTURE_VOID) {
                MenuClanUtils.openClanMenu(player);
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
            }
            event.setCancelled(true);
        } else if (event.getView().getTitle().equalsIgnoreCase(ChatColor.BOLD + LocalizationUtils.langCheck(language, "MEMBER_MENU"))) {
            if (event.getCurrentItem() == null) {

            } else if (event.getCurrentItem().getType() == Material.PLAYER_HEAD) {

            } else if (event.getCurrentItem().getType() == Material.NAME_TAG) {
                MenuClanUtils.openChangeRoleMenu(player);
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
            } else if (event.getCurrentItem().getType() == Material.BARRIER) {
                plugin.getServer().dispatchCommand(player, "kickclan " + selectedPlayerName);
                player.closeInventory();
            } else if (event.getCurrentItem().getType() == Material.STRUCTURE_VOID) {
                MenuClanUtils.openMembersMenu(player, sql);
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
            }
            event.setCancelled(true);
        } else if (event.getView().getTitle().equalsIgnoreCase(ChatColor.BOLD + LocalizationUtils.langCheck(language, "CHANGE_ROLE_MENU"))) {
            if (event.getCurrentItem() == null) {

            } else if (event.getCurrentItem().getType() == Material.BAMBOO) {
                plugin.getServer().dispatchCommand(player, "setroleclan " + selectedPlayerName + " " + UnitedClans.getInstance().getConfig().getString("roles.member"));
                player.closeInventory();
            } else if (event.getCurrentItem().getType() == Material.STICK) {
                plugin.getServer().dispatchCommand(player, "setroleclan " + selectedPlayerName + " " + UnitedClans.getInstance().getConfig().getString("roles.elder"));
                player.closeInventory();
            } else if (event.getCurrentItem().getType() == Material.BLAZE_ROD) {
                try {
                    List<Map<String, Object>> rsPlayerRole = sql.sqlSelectData("ClanRole", "PLAYERS", "UUID = '" + player.getUniqueId() + "'");
                    String PlayerRole = (String) rsPlayerRole.get(0).get("ClanRole");

                    if (Objects.equals(player.getName(), selectedPlayerName)) {
                        player.closeInventory();
                        GeneralUtils.checkUtil(player, language, "SET_ROLE_YOURSELF", true);
                    } else if (!Objects.equals(PlayerRole, UnitedClans.getInstance().getConfig().getString("roles.leader"))) {
                        player.closeInventory();
                        GeneralUtils.checkUtil(player, language, "NO_RIGHTS_SET_ROLE", true);
                    } else {
                        MenuClanUtils.openConfirmRoleMenu(player);
                        player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
                    }
                } catch (Exception e) {
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                }
            } else if (event.getCurrentItem().getType() == Material.STRUCTURE_VOID) {
                MenuClanUtils.openMemberMenu(player, sql, selectedPlayerName);
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
            }
            event.setCancelled(true);
        } else if (event.getView().getTitle().equalsIgnoreCase(ChatColor.BOLD + LocalizationUtils.langCheck(language, "CONFIRM_ROLE_MENU"))) {
            if (event.getCurrentItem() == null) {

            } else if (event.getCurrentItem().getType() == Material.GREEN_STAINED_GLASS_PANE) {
                try {
                    List<Map<String, Object>> rsPlayerClanID = sql.sqlSelectData("ClanID", "PLAYERS", "UUID = '" + player.getUniqueId() + "'");
                    Integer ClanID = (Integer) rsPlayerClanID.get(0).get("ClanID");

                    List<Map<String, Object>> rsClan = sql.sqlSelectData("ClanName", "CLANS", "ClanID = " + ClanID);
                    String ClanName = (String) rsClan.get(0).get("ClanName");

                    plugin.getServer().dispatchCommand(player, "changeleaderclan " + ClanName + " " + selectedPlayerName);
                    player.closeInventory();
                } catch (Exception e) {
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                }
            } else if (event.getCurrentItem().getType() == Material.RED_STAINED_GLASS_PANE) {
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
                player.closeInventory();
            } else if (event.getCurrentItem().getType() == Material.STRUCTURE_VOID) {
                MenuClanUtils.openChangeRoleMenu(player);
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
            }
            event.setCancelled(true);
        }
    }
}
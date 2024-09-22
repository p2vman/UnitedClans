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
import unitedclans.utils.DatabaseDriver;

import java.util.*;

public class ClanMenuInventoryHandler implements Listener {
    private final JavaPlugin plugin;
    private final DatabaseDriver dbDriver;
    private String selectedPlayerName;
    private int pageNumber;

    public ClanMenuInventoryHandler(JavaPlugin plugin, DatabaseDriver dbDriver) {
        this.plugin = plugin;
        this.dbDriver = dbDriver;
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        String language = UnitedClans.getInstance().getConfig().getString("lang");
        Player player = (Player) event.getWhoClicked();

        if(event.getView().getTitle().equalsIgnoreCase(ChatColor.BOLD + LocalizationUtils.langCheck(language, "CLAN_MENU"))) {
            if (event.getCurrentItem() == null) {

            } else if (Objects.equals(event.getCurrentItem().getItemMeta().getDisplayName(), "§e" + LocalizationUtils.langCheck(language, "CLAN_BANK"))) {
                MenuClanUtils.openBankMenu(player, dbDriver);
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
            } else if (Objects.equals(event.getCurrentItem().getItemMeta().getDisplayName(), "§a" + LocalizationUtils.langCheck(language, "CLAN_MEMBERS"))) {
                pageNumber = MenuClanUtils.openMembersMenu(player, dbDriver, 0);
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
            } else if (Objects.equals(event.getCurrentItem().getItemMeta().getDisplayName(), "§b" + LocalizationUtils.langCheck(language, "TOP_CLANS"))) {
                MenuClanUtils.openTopClanMenu(player);
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
            } else if (Objects.equals(event.getCurrentItem().getItemMeta().getDisplayName(), "§5" + LocalizationUtils.langCheck(language, "CLAN_SETTINGS"))) {
                MenuClanUtils.openClanSettingsMenu(player);
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
            } else if (Objects.equals(event.getCurrentItem().getItemMeta().getDisplayName(), "§6" + LocalizationUtils.langCheck(language, "CLAN_INFO"))) {
                plugin.getServer().dispatchCommand(player, "infoclan");
                player.closeInventory();
            }
            event.setCancelled(true);
        } else if (event.getView().getTitle().equalsIgnoreCase(ChatColor.BOLD + LocalizationUtils.langCheck(language, "MEMBERS_MENU"))) {
            if (event.getCurrentItem() == null) {

            } else if (event.getCurrentItem().getType() == Material.PLAYER_HEAD) {
                selectedPlayerName = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
                MenuClanUtils.openMemberMenu(player, dbDriver, selectedPlayerName);
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
            } else if (Objects.equals(event.getCurrentItem().getItemMeta().getDisplayName(), "§6" + LocalizationUtils.langCheck(language, "PREVIOUS_PAGE"))) {
                pageNumber--;
                if (pageNumber < 0) {
                    pageNumber = 0;
                } else if (pageNumber > 3) {
                    pageNumber = 3;
                }
                MenuClanUtils.openMembersMenu(player, dbDriver, pageNumber);
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
            } else if (Objects.equals(event.getCurrentItem().getItemMeta().getDisplayName(), "§6" + LocalizationUtils.langCheck(language, "NEXT_PAGE"))) {
                pageNumber++;
                if (pageNumber < 0) {
                    pageNumber = 0;
                } else if (pageNumber > 3) {
                    pageNumber = 3;
                }
                MenuClanUtils.openMembersMenu(player, dbDriver, pageNumber);
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
            } else if (Objects.equals(event.getCurrentItem().getItemMeta().getDisplayName(), "§b" + LocalizationUtils.langCheck(language, "MEMBERS_BACK_TO_MENU"))) {
                MenuClanUtils.openClanMenu(player);
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
            }
            event.setCancelled(true);
        } else if (event.getView().getTitle().equalsIgnoreCase(ChatColor.BOLD + LocalizationUtils.langCheck(language, "MEMBER_MENU"))) {
            if (event.getCurrentItem() == null) {

            } else if (event.getCurrentItem().getType() == Material.PLAYER_HEAD) {

            } else if (Objects.equals(event.getCurrentItem().getItemMeta().getDisplayName(), "§6" + LocalizationUtils.langCheck(language, "CHANGE_ROLE"))) {
                MenuClanUtils.openChangeRoleMenu(player);
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
            } else if (Objects.equals(event.getCurrentItem().getItemMeta().getDisplayName(), "§c" + LocalizationUtils.langCheck(language, "KICK_MEMBER"))) {
                plugin.getServer().dispatchCommand(player, "kickclan " + selectedPlayerName);
                player.closeInventory();
            } else if (Objects.equals(event.getCurrentItem().getItemMeta().getDisplayName(), "§b" + LocalizationUtils.langCheck(language, "MEMBER_BACK_TO_MENU"))) {
                pageNumber = MenuClanUtils.openMembersMenu(player, dbDriver, 0);
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
            }
            event.setCancelled(true);
        } else if (event.getView().getTitle().equalsIgnoreCase(ChatColor.BOLD + LocalizationUtils.langCheck(language, "CHANGE_ROLE_MENU"))) {
            if (event.getCurrentItem() == null) {

            } else if (Objects.equals(event.getCurrentItem().getItemMeta().getDisplayName(), "§a" + LocalizationUtils.langCheck(language, "SET_MEMBER"))) {
                plugin.getServer().dispatchCommand(player, "setroleclan " + selectedPlayerName + " " + UnitedClans.getInstance().getConfig().getString("roles.member"));
                player.closeInventory();
            } else if (Objects.equals(event.getCurrentItem().getItemMeta().getDisplayName(), "§6" + LocalizationUtils.langCheck(language, "SET_ELDER"))) {
                plugin.getServer().dispatchCommand(player, "setroleclan " + selectedPlayerName + " " + UnitedClans.getInstance().getConfig().getString("roles.elder"));
                player.closeInventory();
            } else if (Objects.equals(event.getCurrentItem().getItemMeta().getDisplayName(), "§5" + LocalizationUtils.langCheck(language, "SET_LEADER"))) {
                List<Map<String, Object>> rsPlayerRole = dbDriver.selectData("clan_role", "players", "WHERE uuid = ?", player.getUniqueId());
                String PlayerRole = (String) rsPlayerRole.get(0).get("clan_role");

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
            } else if (Objects.equals(event.getCurrentItem().getItemMeta().getDisplayName(), "§b" + LocalizationUtils.langCheck(language, "CHANGE_ROLE_BACK_TO_MENU"))) {
                MenuClanUtils.openMemberMenu(player, dbDriver, selectedPlayerName);
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
            }
            event.setCancelled(true);
        } else if (event.getView().getTitle().equalsIgnoreCase(ChatColor.BOLD + LocalizationUtils.langCheck(language, "CONFIRM_ROLE_MENU"))) {
            if (event.getCurrentItem() == null) {

            } else if (Objects.equals(event.getCurrentItem().getItemMeta().getDisplayName(), "§a" + LocalizationUtils.langCheck(language, "CONFIRM_ROLE"))) {
                List<Map<String, Object>> rsPlayerClanID = dbDriver.selectData("clan_id", "players", "WHERE uuid = ?", player.getUniqueId());
                int ClanID = (int) rsPlayerClanID.get(0).get("clan_id");

                List<Map<String, Object>> rsClan = dbDriver.selectData("clan_name", "clans", "WHERE clan_id = ?", ClanID);
                String ClanName = (String) rsClan.get(0).get("clan_name");

                plugin.getServer().dispatchCommand(player, "changeleaderclan " + ClanName + " " + selectedPlayerName);
                player.closeInventory();
            } else if (Objects.equals(event.getCurrentItem().getItemMeta().getDisplayName(), "§c" + LocalizationUtils.langCheck(language, "NOT_CONFIRM_ROLE"))) {
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
                player.closeInventory();
            } else if (Objects.equals(event.getCurrentItem().getItemMeta().getDisplayName(), "§b" + LocalizationUtils.langCheck(language, "CONFIRM_ROLE_BACK_TO_MENU"))) {
                MenuClanUtils.openChangeRoleMenu(player);
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
            }
            event.setCancelled(true);
        } else if (event.getView().getTitle().equalsIgnoreCase(ChatColor.BOLD + LocalizationUtils.langCheck(language, "BANK_CLAN_MENU"))) {
            if (event.getCurrentItem() == null) {

            } else if (Objects.equals(event.getCurrentItem().getItemMeta().getDisplayName(), "§a" + LocalizationUtils.langCheck(language, "DEPOSIT"))) {
                MenuClanUtils.openDepositMenu(player);
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
            } else if (Objects.equals(event.getCurrentItem().getItemMeta().getDisplayName(), "§6" + LocalizationUtils.langCheck(language, "BANK_ACCOUNT"))) {

            } else if (Objects.equals(event.getCurrentItem().getItemMeta().getDisplayName(), "§a" + LocalizationUtils.langCheck(language, "WITHDRAW"))) {
                MenuClanUtils.openWithdrawMenu(player);
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
            } else if (Objects.equals(event.getCurrentItem().getItemMeta().getDisplayName(), "§b" + LocalizationUtils.langCheck(language, "BANK_CLAN_BACK_TO_MENU"))) {
                MenuClanUtils.openClanMenu(player);
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
            }
            event.setCancelled(true);
        } else if (event.getView().getTitle().equalsIgnoreCase(ChatColor.BOLD + LocalizationUtils.langCheck(language, "DEPOSIT_MENU"))) {
            if (event.getCurrentItem() == null) {

            } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals("§68$")) {
                plugin.getServer().dispatchCommand(player, "bankdepositclan 8");
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals("§616$")) {
                plugin.getServer().dispatchCommand(player, "bankdepositclan 16");
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals("§632$")) {
                plugin.getServer().dispatchCommand(player, "bankdepositclan 32");
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals("§648$")) {
                plugin.getServer().dispatchCommand(player, "bankdepositclan 48");
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals("§664$")) {
                plugin.getServer().dispatchCommand(player, "bankdepositclan 64");
            } else if (event.getCurrentItem().getType() == Material.STRUCTURE_VOID) {
                MenuClanUtils.openBankMenu(player, dbDriver);
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
            }
            event.setCancelled(true);
        } else if (event.getView().getTitle().equalsIgnoreCase(ChatColor.BOLD + LocalizationUtils.langCheck(language, "WITHDRAW_MENU"))) {
            if (event.getCurrentItem() == null) {

            } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals("§68$")) {
                plugin.getServer().dispatchCommand(player, "bankwithdrawclan 8");
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals("§616$")) {
                plugin.getServer().dispatchCommand(player, "bankwithdrawclan 16");
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals("§632$")) {
                plugin.getServer().dispatchCommand(player, "bankwithdrawclan 32");
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals("§648$")) {
                plugin.getServer().dispatchCommand(player, "bankwithdrawclan 48");
            } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals("§664$")) {
                plugin.getServer().dispatchCommand(player, "bankwithdrawclan 64");
            } else if (event.getCurrentItem().getType() == Material.STRUCTURE_VOID) {
                MenuClanUtils.openBankMenu(player, dbDriver);
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
            }
            event.setCancelled(true);
        } else if(event.getView().getTitle().equalsIgnoreCase(ChatColor.BOLD + LocalizationUtils.langCheck(language, "TOP_CLAN_MENU"))) {
            if (event.getCurrentItem() == null) {

            } else if (Objects.equals(event.getCurrentItem().getItemMeta().getDisplayName(), "§5" + LocalizationUtils.langCheck(language, "KILLS_TOP"))) {
                plugin.getServer().dispatchCommand(player, "topclans kills");
                player.closeInventory();
            } else if (Objects.equals(event.getCurrentItem().getItemMeta().getDisplayName(), "§5" + LocalizationUtils.langCheck(language, "MONEY_TOP"))) {
                plugin.getServer().dispatchCommand(player, "topclans money");
                player.closeInventory();
            } else if (Objects.equals(event.getCurrentItem().getItemMeta().getDisplayName(), "§b" + LocalizationUtils.langCheck(language, "TOP_CLAN_BACK_TO_MENU"))) {
                MenuClanUtils.openClanMenu(player);
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
            }
            event.setCancelled(true);
        } else if(event.getView().getTitle().equalsIgnoreCase(ChatColor.BOLD + LocalizationUtils.langCheck(language, "CLAN_SETTINGS_MENU"))) {
            if (event.getCurrentItem() == null) {

            } else if (Objects.equals(event.getCurrentItem().getItemMeta().getDisplayName(), "§6" + LocalizationUtils.langCheck(language, "LETTER_CLAN"))) {
                plugin.getServer().dispatchCommand(player, "letterclan");
                player.closeInventory();
            } else if (Objects.equals(event.getCurrentItem().getItemMeta().getDisplayName(), "§c" + LocalizationUtils.langCheck(language, "LEAVE_CLAN"))) {
                List<Map<String, Object>> rsPlayerRole = dbDriver.selectData("clan_role", "players", "WHERE uuid = ?", player.getUniqueId());
                String PlayerRole = (String) rsPlayerRole.get(0).get("clan_role");

                if (Objects.equals(PlayerRole, UnitedClans.getInstance().getConfig().getString("roles.leader"))) {
                    player.closeInventory();
                    GeneralUtils.checkUtil(player, language, "YOU_LEADER", true);
                } else {
                    MenuClanUtils.openConfirmLeaveClanMenu(player);
                    player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
                }
            } else if (Objects.equals(event.getCurrentItem().getItemMeta().getDisplayName(), "§5" + LocalizationUtils.langCheck(language, "DELETE_CLAN"))) {
                List<Map<String, Object>> rsPlayerRole = dbDriver.selectData("clan_role", "players", "WHERE uuid = ?", player.getUniqueId());
                String PlayerRole = (String) rsPlayerRole.get(0).get("clan_role");

                if (!Objects.equals(PlayerRole, UnitedClans.getInstance().getConfig().getString("roles.leader"))) {
                    player.closeInventory();
                    GeneralUtils.checkUtil(player, language, "NOT_LEADER", true);
                } else {
                    MenuClanUtils.openConfirmDeleteClanMenu(player);
                    player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);;
                }
            } else if (Objects.equals(event.getCurrentItem().getItemMeta().getDisplayName(), "§b" + LocalizationUtils.langCheck(language, "CLAN_SETTINGS_CLAN_BACK_TO_MENU"))) {
                MenuClanUtils.openClanMenu(player);
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
            }
            event.setCancelled(true);
        } else if(event.getView().getTitle().equalsIgnoreCase(ChatColor.BOLD + LocalizationUtils.langCheck(language, "CONFIRM_LEAVE_MENU"))) {
            if (event.getCurrentItem() == null) {

            } else if (Objects.equals(event.getCurrentItem().getItemMeta().getDisplayName(), "§a" + LocalizationUtils.langCheck(language, "CONFIRM_LEAVE"))) {
                List<Map<String, Object>> rsPlayerClanID = dbDriver.selectData("clan_id", "players", "WHERE uuid = ?", player.getUniqueId());
                int ClanID = (int) rsPlayerClanID.get(0).get("clan_id");

                List<Map<String, Object>> rsClan = dbDriver.selectData("clan_name", "clans", "WHERE clan_id = ?", ClanID);
                String ClanName = (String) rsClan.get(0).get("clan_name");

                plugin.getServer().dispatchCommand(player, "leaveclan " + ClanName);
                player.closeInventory();
            } else if (Objects.equals(event.getCurrentItem().getItemMeta().getDisplayName(), "§c" + LocalizationUtils.langCheck(language, "NOT_CONFIRM_LEAVE"))) {
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
                player.closeInventory();
            } else if (Objects.equals(event.getCurrentItem().getItemMeta().getDisplayName(), "§b" + LocalizationUtils.langCheck(language, "CONFIRM_LEAVE_BACK_TO_MENU"))) {
                MenuClanUtils.openClanSettingsMenu(player);
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
            }
            event.setCancelled(true);
        } else if(event.getView().getTitle().equalsIgnoreCase(ChatColor.BOLD + LocalizationUtils.langCheck(language, "CONFIRM_DELETE_MENU"))) {
            if (event.getCurrentItem() == null) {

            } else if (Objects.equals(event.getCurrentItem().getItemMeta().getDisplayName(), "§a" + LocalizationUtils.langCheck(language, "CONFIRM_DELETE"))) {
                List<Map<String, Object>> rsPlayerClanID = dbDriver.selectData("clan_id", "players", "WHERE uuid = ?", player.getUniqueId());
                int ClanID = (int) rsPlayerClanID.get(0).get("clan_id");

                List<Map<String, Object>> rsClan = dbDriver.selectData("clan_name", "clans", "WHERE clan_id = ?", ClanID);
                String ClanName = (String) rsClan.get(0).get("clan_name");

                plugin.getServer().dispatchCommand(player, "deleteclan " + ClanName);
                player.closeInventory();
            } else if (Objects.equals(event.getCurrentItem().getItemMeta().getDisplayName(), "§c" + LocalizationUtils.langCheck(language, "NOT_CONFIRM_DELETE"))) {
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
                player.closeInventory();
            } else if (Objects.equals(event.getCurrentItem().getItemMeta().getDisplayName(), "§b" + LocalizationUtils.langCheck(language, "CONFIRM_DELETE_BACK_TO_MENU"))) {
                MenuClanUtils.openClanSettingsMenu(player);
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);
            }
            event.setCancelled(true);
        }
    }
}

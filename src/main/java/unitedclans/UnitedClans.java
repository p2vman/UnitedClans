package unitedclans;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import unitedclans.command.*;
import unitedclans.handler.*;
import unitedclans.hooks.PlaceholderAPIHook;
import unitedclans.langs.DefaultConfig;
import unitedclans.utils.LocalizationUtils;
import unitedclans.utils.DatabaseDriver;

public final class UnitedClans extends JavaPlugin implements Listener {
    private static UnitedClans instance;
    private DatabaseDriver dbDriver;

    @Override
    public void onEnable() {
        instance = this;
        DefaultConfig.createDefaultConfigFile(this);
        LocalizationUtils.loadLang(this);

        dbDriver = new DatabaseDriver("jdbc:sqlite:" + getDataFolder() + "/ucdatabase.db");
        dbDriver.createTable("players", "uuid TEXT NOT NULL PRIMARY KEY", "player_name TEXT", "clan_id INTEGER", "clan_role TEXT", "kills INTEGER", "donations INTEGER", "letter_read INTEGER");
        dbDriver.createTable("clans", "clan_id INTEGER NOT NULL PRIMARY KEY", "clan_name TEXT", "clan_color TEXT", "count_members INTEGER", "bank INTEGER", "kills INTEGER");
        dbDriver.createTable("invitations", "uuid TEXT NOT NULL PRIMARY KEY", "player_name TEXT", "clan_id INTEGER");
        dbDriver.createTable("letters", "clan_id TEXT NOT NULL PRIMARY KEY", "letter TEXT");

        getServer().getLogger().info("[UnitedClans] " + "\u001B[96m" + "╔═╗╔═╗╔═══╗" + "\u001B[0m");
        getServer().getLogger().info("[UnitedClans] " + "\u001B[96m" + "║ ║║ ║║ ╔═╝" + "\u001B[0m");
        getServer().getLogger().info("[UnitedClans] " + "\u001B[96m" + "║ ║║ ║║ ║" + "\u001B[0m");
        getServer().getLogger().info("[UnitedClans] " + "\u001B[96m" + "║ ║║ ║║ ║" + "\u001B[0m");
        getServer().getLogger().info("[UnitedClans] " + "\u001B[96m" + "║ ╚╝ ║║ ╚═╗" + "\u001B[0m");
        getServer().getLogger().info("[UnitedClans] " + "\u001B[96m" + "╚════╝╚═══╝" + "\u001B[0m");
        getServer().getLogger().info("[UnitedClans] UnitedClans is enabled");

        getServer().getPluginManager().registerEvents(new ClanMenuInventoryHandler(this, dbDriver), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinEventHandler(this, dbDriver), this);
        getServer().getPluginManager().registerEvents(new PlayerKillEventHandler(dbDriver), this);
        getServer().getPluginCommand("createclan").setExecutor(new CreateClanCommand(this, dbDriver));
        getServer().getPluginCommand("createclan").setTabCompleter(new CreateClanTabCompleter());
        getServer().getPluginCommand("deleteclan").setExecutor(new DeleteClanCommand(this, dbDriver));
        getServer().getPluginCommand("deleteclan").setTabCompleter(new DeleteClanTabCompleter());
        getServer().getPluginCommand("inviteclan").setExecutor(new InviteClanCommand(this, dbDriver));
        getServer().getPluginCommand("inviteclan").setTabCompleter(new InviteClanTabCompleter());
        getServer().getPluginCommand("acceptclan").setExecutor(new AcceptClanCommand(this, dbDriver));
        getServer().getPluginCommand("acceptclan").setTabCompleter(new AcceptClanTabCompleter());
        getServer().getPluginCommand("kickclan").setExecutor(new KickClanCommand(this, dbDriver));
        getServer().getPluginCommand("kickclan").setTabCompleter(new KickClanTabCompleter(dbDriver));
        getServer().getPluginCommand("leaveclan").setExecutor(new LeaveClanCommand(this, dbDriver));
        getServer().getPluginCommand("leaveclan").setTabCompleter(new LeaveClanTabCompleter());
        getServer().getPluginCommand("setroleclan").setExecutor(new SetRoleClanCommand(this, dbDriver));
        getServer().getPluginCommand("setroleclan").setTabCompleter(new SetRoleClanTabCompleter(dbDriver));
        getServer().getPluginCommand("menuclan").setExecutor(new MenuClanCommand(dbDriver));
        getServer().getPluginCommand("menuclan").setTabCompleter(new MenuClanTabCompleter());
        getServer().getPluginCommand("chatclan").setExecutor(new ChatClanCommand(this, dbDriver));
        getServer().getPluginCommand("chatclan").setTabCompleter(new ChatClanTabCompleter());
        getServer().getPluginCommand("changeleaderclan").setExecutor(new ChangeLeaderClanCommand(this, dbDriver));
        getServer().getPluginCommand("changeleaderclan").setTabCompleter(new ChangeLeaderClanTabCompleter(dbDriver));
        getServer().getPluginCommand("bankdepositclan").setExecutor(new BankDepositClanCommand(this, dbDriver));
        getServer().getPluginCommand("bankdepositclan").setTabCompleter(new BankDepositClanTabCompleter());
        getServer().getPluginCommand("bankwithdrawclan").setExecutor(new BankWithdrawClanCommand(this, dbDriver));
        getServer().getPluginCommand("bankwithdrawclan").setTabCompleter(new BankWithdrawClanTabCompleter());
        getServer().getPluginCommand("topclans").setExecutor(new TopClansCommand(dbDriver));
        getServer().getPluginCommand("topclans").setTabCompleter(new TopClansTabCompleter());
        getServer().getPluginCommand("letterclan").setExecutor(new LetterClanCommand(this, dbDriver));
        getServer().getPluginCommand("letterclan").setTabCompleter(new LetterClanTabCompleter());
        getServer().getPluginCommand("helpclan").setExecutor(new HelpClanCommand());
        getServer().getPluginCommand("helpclan").setTabCompleter(new HelpClanTabCompleter());
        getServer().getPluginCommand("infoclan").setExecutor(new InfoClanCommand(dbDriver));
        getServer().getPluginCommand("infoclan").setTabCompleter(new InfoClanTabCompleter(dbDriver));

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPIHook(this, dbDriver).register();
        }
    }

    @Override
    public void onDisable() {
        getServer().getLogger().info("[UnitedClans] UnitedClans is disabled");
        dbDriver.closeConnection();
    }

    public static UnitedClans getInstance() {
        return instance;
    }
}

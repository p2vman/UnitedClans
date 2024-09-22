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
        getServer().getPluginCommand("uchelp").setExecutor(new HelpClanCommand());
        getServer().getPluginCommand("uchelp").setTabCompleter(new HelpClanTabCompleter());
        getServer().getPluginCommand("ucinfo").setExecutor(new InfoClanCommand(dbDriver));
        getServer().getPluginCommand("ucinfo").setTabCompleter(new InfoClanTabCompleter(dbDriver));
        getServer().getPluginCommand("uccreate").setExecutor(new CreateClanCommand(this, dbDriver));
        getServer().getPluginCommand("uccreate").setTabCompleter(new CreateClanTabCompleter());
        getServer().getPluginCommand("ucdelete").setExecutor(new DeleteClanCommand(this, dbDriver));
        getServer().getPluginCommand("ucdelete").setTabCompleter(new DeleteClanTabCompleter());
        getServer().getPluginCommand("ucinvite").setExecutor(new InviteClanCommand(this, dbDriver));
        getServer().getPluginCommand("ucinvite").setTabCompleter(new InviteClanTabCompleter());
        getServer().getPluginCommand("ucaccept").setExecutor(new AcceptClanCommand(this, dbDriver));
        getServer().getPluginCommand("ucaccept").setTabCompleter(new AcceptClanTabCompleter());
        getServer().getPluginCommand("uckick").setExecutor(new KickClanCommand(this, dbDriver));
        getServer().getPluginCommand("uckick").setTabCompleter(new KickClanTabCompleter(dbDriver));
        getServer().getPluginCommand("ucleave").setExecutor(new LeaveClanCommand(this, dbDriver));
        getServer().getPluginCommand("ucleave").setTabCompleter(new LeaveClanTabCompleter());
        getServer().getPluginCommand("ucsetrole").setExecutor(new SetRoleClanCommand(this, dbDriver));
        getServer().getPluginCommand("ucsetrole").setTabCompleter(new SetRoleClanTabCompleter(dbDriver));
        getServer().getPluginCommand("ucmenu").setExecutor(new MenuClanCommand(dbDriver));
        getServer().getPluginCommand("ucmenu").setTabCompleter(new MenuClanTabCompleter());
        getServer().getPluginCommand("ucchat").setExecutor(new ChatClanCommand(this, dbDriver));
        getServer().getPluginCommand("ucchat").setTabCompleter(new ChatClanTabCompleter());
        getServer().getPluginCommand("ucchangeleader").setExecutor(new ChangeLeaderClanCommand(this, dbDriver));
        getServer().getPluginCommand("ucchangeleader").setTabCompleter(new ChangeLeaderClanTabCompleter(dbDriver));
        getServer().getPluginCommand("ucbankdeposit").setExecutor(new BankDepositClanCommand(this, dbDriver));
        getServer().getPluginCommand("ucbankdeposit").setTabCompleter(new BankDepositClanTabCompleter());
        getServer().getPluginCommand("ucbankwithdraw").setExecutor(new BankWithdrawClanCommand(this, dbDriver));
        getServer().getPluginCommand("ucbankwithdraw").setTabCompleter(new BankWithdrawClanTabCompleter());
        getServer().getPluginCommand("uctop").setExecutor(new TopClansCommand(dbDriver));
        getServer().getPluginCommand("uctop").setTabCompleter(new TopClansTabCompleter());
        getServer().getPluginCommand("ucletter").setExecutor(new LetterClanCommand(this, dbDriver));
        getServer().getPluginCommand("ucletter").setTabCompleter(new LetterClanTabCompleter());

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

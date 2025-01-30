package unitedclans;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import unitedclans.commands.*;
import unitedclans.handler.*;
import unitedclans.hooks.PlaceholderAPIHook;
import unitedclans.langs.DefaultConfig;
import unitedclans.utils.LocalizationUtils;
import unitedclans.utils.DatabaseDriver;

import java.lang.reflect.Field;
import java.util.*;

public final class UnitedClans extends JavaPlugin implements Listener {
    private static UnitedClans instance;
    private DatabaseDriver dbDriver;
    public Map<UUID, Integer> invitations = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        DefaultConfig.createDefaultConfigFile(this);
        LocalizationUtils.loadLang(this);

        dbDriver = new DatabaseDriver("jdbc:sqlite:" + getDataFolder() + "/ucdatabase.db");
        dbDriver.createTable("players", "uuid TEXT NOT NULL PRIMARY KEY", "player_name TEXT", "clan_id INTEGER", "clan_role TEXT", "kills INTEGER", "donations INTEGER", "letter_read INTEGER");
        dbDriver.createTable("clans", "clan_id INTEGER NOT NULL PRIMARY KEY", "clan_name TEXT", "clan_color TEXT", "count_members INTEGER", "bank INTEGER", "kills INTEGER");
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

        List<Command> commands = new ArrayList<>();
        commands.add(new AcceptClan(dbDriver));
        commands.add(new BankDepositClan(dbDriver));
        commands.add(new BankWithdrawClan(dbDriver));
        commands.add(new ChangeLeaderClan(dbDriver));
        commands.add(new ChatClan(dbDriver));
        commands.add(new CreateClan(dbDriver));
        commands.add(new DeleteClan(dbDriver));
        commands.add(new HelpClan(dbDriver));
        commands.add(new InfoClan(dbDriver));
        commands.add(new InviteClan(dbDriver));
        commands.add(new KickClan(dbDriver));
        commands.add(new LeaveClan(dbDriver));
        commands.add(new LetterClan(dbDriver));
        commands.add(new MenuClan(dbDriver));
        commands.add(new ReloadConfig(dbDriver));
        commands.add(new SetRoleClan(dbDriver));
        commands.add(new TopClans(dbDriver));


        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPIHook(this, dbDriver).register();
        }

        try {

            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap map = (CommandMap)commandMapField.get(Bukkit.getServer());

            map.registerAll(getName(), commands);
        } catch (Exception e) {
            e.printStackTrace();
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

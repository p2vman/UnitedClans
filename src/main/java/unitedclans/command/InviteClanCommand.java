package unitedclans.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import unitedclans.UnitedClans;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class InviteClanCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private Connection con;
    private Boolean inviteTimer;
    public InviteClanCommand(JavaPlugin plugin, Connection con) {
        this.plugin = plugin;
        this.con = con;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 0 || args.length >= 2) {
            sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.invalidcommand"));
            return false;
        }
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        String playerNameInput = args[0];
        Player playerName = plugin.getServer().getPlayer(playerNameInput);
        if (playerName == null) {
            sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.wrongplayername"));
            return true;
        }
        try {
            Statement stmt = con.createStatement();
            //ResultSet rsNumberClans = stmt.executeQuery("SELECT ClanID FROM CLANS WHERE ClanID IS (SELECT MAX(ClanID) FROM CLANS);");
            //Integer NumberClans = rsNumberClans.getInt("ClanID") + 1;

            ResultSet rsChekerPlayer = stmt.executeQuery("SELECT * FROM PLAYERS WHERE UUID IS '" + playerName.getUniqueId() + "';");
            if (rsChekerPlayer.getInt("ClanID") != 0) {
                sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.playermemberclan"));
                return true;
            }
            ResultSet rsgetRoleUUID = stmt.executeQuery("SELECT ClanRole FROM PLAYERS WHERE UUID IS '" + uuid + "';");
            String getRoleUUID = rsgetRoleUUID.getString("ClanRole");
            if (!Objects.equals(getRoleUUID, UnitedClans.getInstance().getConfig().getString("roles.leader"))) {
                sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.notleader"));
                return true;
            }
            /*ResultSet rsInviteCheker = stmt.executeQuery("SELECT * FROM PLAYERS WHERE UUID IS '" + playerName.getUniqueId() + "';");
            if (rsInviteCheker.getInt("InviteCheker") != 0) {
                sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.playermemberclan"));
                return true;
            }*/
            System.out.println("mnbbnb");
            inviteTimer = false;
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    System.out.println("aassasasa");
                    inviteTimer = true;
                }
            };
            Timer timer = new Timer();
            long time = 10000L;
            timer.schedule(task, time);
            while (true) {
                if (inviteTimer) break;
            }
            System.out.println("qwwewqw");
            ResultSet rsgetLeaderClanID = stmt.executeQuery("SELECT ClanID FROM PLAYERS WHERE UUID IS '" + uuid + "';");
            String tablePLAYERS = "UPDATE PLAYERS SET InviteChecker = " + rsgetLeaderClanID.getInt("ClanID") + " WHERE UUID IS '" + playerName.getUniqueId() + "';";
            stmt.executeUpdate(tablePLAYERS);

            /*ResultSet rsChekerClanName = stmt.executeQuery("SELECT * FROM CLANS WHERE ClanName IS '" + playerNameInput + "';");
            if (rsChekerClanName.next()) {
                sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.clannametaken"));
                return true;
            }

            String tableCLANS = "INSERT INTO CLANS (ClanID, ClanName) " +
                    "VALUES ('" + NumberClans + "', '" + playerNameInput + "');";
            stmt.executeUpdate(tableCLANS);
            String tablePLAYERS1 = "UPDATE PLAYERS SET ClanID = '" + NumberClans + "' WHERE UUID IS '" + uuid + "';";
            stmt.executeUpdate(tablePLAYERS1);
            String tablePLAYERS2 = "UPDATE PLAYERS SET ClanRole = '" + UnitedClans.getInstance().getConfig().getString("roles.leader") + "' WHERE UUID IS '" + uuid + "';";
            stmt.executeUpdate(tablePLAYERS2);*/
            stmt.close();
            sender.sendMessage(UnitedClans.getInstance().getConfig().getString("messages.successcreateclan"));
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return true;
    }
}

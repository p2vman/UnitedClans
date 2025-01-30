package unitedclans.commands;

import com.google.common.base.Preconditions;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import unitedclans.UnitedClans;
import unitedclans.utils.DatabaseDriver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractCommand extends Command implements CommandExecutor, TabCompleter {
    protected final JavaPlugin plugin;
    protected final DatabaseDriver dbDriver;
    public AbstractCommand(DatabaseDriver dbDriver) {
        super("");
        this.plugin = UnitedClans.getInstance();
        Command command = this.getClass().getAnnotation(Command.class);
        setName(command.name());
        setDescription(command.description());
        setPermission(command.permission());
        permissionMessage(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection().deserialize(command.permission_message()));
        setUsage(command.usageMessage());
        setAliases(Arrays.asList(command.aliases()));
        this.dbDriver = dbDriver;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        boolean success = false;
        if (!this.plugin.isEnabled()) {
            throw new CommandException("Cannot execute command '" + commandLabel + "' in plugin " + this.plugin.getDescription().getFullName() + " - plugin is disabled.");
        } else if (!this.testPermission(sender)) {
            return true;
        } else {
            try {
                success = this.onCommand(sender, this, commandLabel, args);
            } catch (Throwable var9) {
                throw new CommandException("Unhandled exception executing command '" + commandLabel + "' in plugin " + this.plugin.getDescription().getFullName(), var9);
            }

            if (!success && this.usageMessage.length() > 0) {
                String[] var5 = this.usageMessage.replace("<command>", commandLabel).split("\n");
                int var6 = var5.length;

                for (int var7 = 0; var7 < var6; ++var7) {
                    String line = var5[var7];
                    sender.sendMessage(line);
                }
            }

            return success;
        }
    }

    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws CommandException, IllegalArgumentException {
        Preconditions.checkArgument(sender != null, "Sender cannot be null");
        Preconditions.checkArgument(args != null, "Arguments cannot be null");
        Preconditions.checkArgument(alias != null, "Alias cannot be null");
        List<String> completions = null;

        try {
            completions = onTabComplete(sender, this, alias, args);
        } catch (Throwable var11) {
            StringBuilder message = new StringBuilder();
            message.append("Unhandled exception during tab completion for command '/").append(alias).append(' ');
            String[] var7 = args;
            int var8 = args.length;

            for(int var9 = 0; var9 < var8; ++var9) {
                String arg = var7[var9];
                message.append(arg).append(' ');
            }

            message.deleteCharAt(message.length() - 1).append("' in plugin ").append(this.plugin.getDescription().getFullName());
            throw new CommandException(message.toString(), var11);
        }

        return completions == null ? super.tabComplete(sender, alias, args) : completions;
    }
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Command {
        String name();
        String usageMessage() default "/<command>";
        String description() default "";
        String permission();
        String[] aliases();
        String permission_message() default "";
    }
}
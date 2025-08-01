package io.github.lama06.schneckenhaus.command;

import io.github.lama06.schneckenhaus.Permissions;
import io.github.lama06.schneckenhaus.command.debug.DebugCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class SchneckenCommand extends MultiplexerCommand {
    private static final String NAME = "schneckenhaus";

    public SchneckenCommand() {
        addSubCommand("help", new HelpCommand(), true);
        addSubCommand("create", new CreateCommand());
        addSubCommand("info", new InfoCommand());
        addSubCommand("giveItem", new GiveItemCommand());
        addSubCommand("count", new CountCommand());
        addSubCommand("newShellType", new NewShellTypeCommand());
        addSubCommand("tp", new TeleportCommand());
        addSubCommand("tpPlayer", new TeleportOtherPlayerCommand());
        addSubCommand("delete", new DeleteCommand());
        addSubCommand("home", new HomeCommand());
        addSubCommand("list", new ListCommand());
        addSubCommand("listLoaded", new ListLoadedShells());
        addSubCommand("debug", new DebugCommand());
        hideSubCommand("debug");

        final Executor executor = new Executor();
        final PluginCommand command = Bukkit.getPluginCommand(NAME);
        command.setTabCompleter(executor);
        command.setExecutor(executor);

        registerPermissions();
    }

    @Override
    public List<HelpCommand.Entry> getHelp() {
        final List<HelpCommand.Entry> entries = new ArrayList<>();
        for (final HelpCommand.Entry entry : super.getHelp()) {
            entries.add(new HelpCommand.Entry("/" + NAME + " " + entry.command(), entry.description()));
        }
        return entries;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String subCommand;
        if (args.length >= 1) {
            subCommand = args[0];
        } else {
            subCommand = "help";
        }
        if (!Permissions.require(sender, "schneckenhaus.command." + subCommand)) {
            return;
        }
        super.execute(sender, args);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return super.tabComplete(sender, args).stream()
            .filter(completion -> {
                String[] completionArgs = completion.split(" ");
                if (completionArgs.length == 0) {
                    return false;
                }
                String subCommand = completionArgs[0];
                return sender.hasPermission("schneckenhaus.command." + subCommand);
            })
            .toList();
    }

    public void registerPermissions() {
        for (String commandName : subCommands.keySet()) {
            Bukkit.getPluginManager().addPermission(new Permission(
                "schneckenhaus.command." + commandName,
                "Use the command /sh %s".formatted(commandName),
                commandName.equals("home") ? PermissionDefault.TRUE : PermissionDefault.OP,
                Map.of()
            ));
        }
        Bukkit.getPluginManager().addPermission(new Permission(
            "schneckenhaus.command.*",
            "Use the /sh command and all subcommands",
            PermissionDefault.OP,
            subCommands.keySet().stream()
                .map(cmdName -> "schneckenhaus.command." + cmdName)
                .collect(Collectors.toMap(perm -> perm, perm -> true))
        ));
    }

    private final class Executor implements TabExecutor {
        @Override
        public boolean onCommand(
                final CommandSender sender,
                final Command command,
                final String label,
                final String[] args
        ) {
            execute(sender, args);
            return true;
        }

        @Override
        public List<String> onTabComplete(
                final CommandSender sender,
                final Command command,
                final String label,
                final String[] args
        ) {
            return tabComplete(sender, args);
        }
    }
}

package io.github.lama06.schneckenhaus.command;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.command.argument.ShellSelector;
import io.github.lama06.schneckenhaus.command.argument.ShellsArgumentType;
import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.player.SchneckenhausPlayer;
import io.github.lama06.schneckenhaus.player.ShellTeleportOptions;
import io.github.lama06.schneckenhaus.shell.Shell;
import io.github.lama06.schneckenhaus.util.ConstantsHolder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.PlayerProfileListResolver;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public final class HomeCommand extends ConstantsHolder {
    public CommandNode<CommandSourceStack> create() {
        return Commands.literal("home")
            .requires(source -> Permission.COMMAND_HOME_TP_OWN.check(source) ||
                Permission.COMMAND_HOME_TP_OTHERS.check(source) ||
                Permission.COMMAND_HOME_MANAGE.check(source)
            )
            .then(Commands.literal("tp")
                .requires(source -> Permission.COMMAND_HOME_TP_OWN.check(source) || Permission.COMMAND_HOME_TP_OWN.check(source))
                .executes(this::tpHome)
                .then(Commands.argument("player", ArgumentTypes.playerProfiles())
                    .requires(Permission.COMMAND_HOME_TP_OTHERS::check)
                    .executes(this::tpOtherHome)
                )
            )
            .then(Commands.literal("set")
                .requires(Permission.COMMAND_HOME_MANAGE::check)
                .then(Commands.argument("player", ArgumentTypes.playerProfiles())
                    .executes(this::setHome)
                    .then(Commands.argument("home", ShellsArgumentType.INSTANCE)
                        .executes(this::setHome)
                    )
                )
            )
            .then(Commands.literal("unset")
                .requires(Permission.COMMAND_HOME_MANAGE::check)
                .then(Commands.argument("player", ArgumentTypes.playerProfiles())
                    .executes(this::unsetHome)
                )
            )
            .requires(Permission.COMMAND_HOME_TP_OWN::check)
            .build();
    }

    private int tpHome(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player player = CommandUtils.requirePlayer(context.getSource());
        SchneckenhausPlayer schneckenhausPlayer = new SchneckenhausPlayer(player);
        Shell home = schneckenhausPlayer.getHomeShell();
        return tpHome(player, home);
    }
    
    private int tpOtherHome(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player player = CommandUtils.requirePlayer(context.getSource());
        PlayerProfile homeOwner = context.getArgument("player", PlayerProfileListResolver.class).resolve(context.getSource()).iterator().next();
        Shell home = plugin.getShellManager().getHomeShell(homeOwner.getId());
        return tpHome(player, home);
    }

    private int tpHome(Player player, Shell home) {
        SchneckenhausPlayer schneckenhausPlayer = new SchneckenhausPlayer(player);
        if (home == null) {
            player.sendMessage(Message.HOME_NOT_FOUND.asComponent(NamedTextColor.RED));
            return 0;
        }
        ShellTeleportOptions teleportOptions = new ShellTeleportOptions();
        teleportOptions.setStorePreviousPositionWhenNesting(false);
        schneckenhausPlayer.enter(home, teleportOptions);
        return Command.SINGLE_SUCCESS;
    }

    private int setHome(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        PlayerProfile player = context.getArgument("player", PlayerProfileListResolver.class).resolve(context.getSource()).iterator().next();
        Shell home = ShellSelector.getSelectorOrHere(context, "home").resolve(context.getSource()).getFirst();
        plugin.getShellManager().setHomeShell(player.getId(), home.getId());
        return Command.SINGLE_SUCCESS;
    }

    private int unsetHome(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        PlayerProfile player = context.getArgument("player", PlayerProfileListResolver.class).resolve(context.getSource()).iterator().next();
        plugin.getShellManager().unsetHomeShell(player.getId());
        return Command.SINGLE_SUCCESS;
    }
}

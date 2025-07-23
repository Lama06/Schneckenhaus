package io.github.lama06.schneckenhaus.command;

import io.github.lama06.schneckenhaus.SchneckenConfig;
import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.player.SchneckenPlayer;
import io.github.lama06.schneckenhaus.position.IdGridPosition;
import io.github.lama06.schneckenhaus.shell.Shell;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomeCommand extends Command {
    @Override
    public void execute(CommandSender sender, String[] args) {
        SchneckenPlugin plugin = SchneckenPlugin.INSTANCE;
        Player player = Require.player(sender);
        if (player == null) {
            return;
        }
        SchneckenPlayer schneckenPlayer = new SchneckenPlayer(player);

        SchneckenConfig config = plugin.getSchneckenConfig();
        if (!config.home.command) {
            player.sendMessage(Component.text("This feature is disabled in the configuration file", NamedTextColor.RED));
            return;
        }

        Integer homeId = SchneckenPlayer.HOME.get(player);
        if (homeId == null) {
            player.sendMessage(Component.text("You don't have a home snail shell", NamedTextColor.RED));
            return;
        }

        IdGridPosition position = new IdGridPosition(homeId);
        Shell<?> shell = plugin.getWorld().getShell(position);
        if (shell == null) {
            player.sendMessage(Component.text("Your home was not found", NamedTextColor.RED));
            SchneckenPlayer.HOME.remove(player);
            return;
        }

        if (schneckenPlayer.isInside(position)) {
            return;
        }
        if (player.getWorld().equals(plugin.getWorld().getBukkit()) && !plugin.getSchneckenConfig().nesting) {
            player.sendMessage(Component.text("You are already in another snail shell. " +
                    "The nesting feature was disabled by the server owner.", NamedTextColor.RED));
            return;
        }

        schneckenPlayer.pushPreviousLocation(player.getLocation());
        player.teleport(shell.getSpawnLocation());
    }
}

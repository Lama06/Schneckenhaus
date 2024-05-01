package io.github.lama06.schneckenhaus.command;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.shell.Shell;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public final class TeleportCommand extends Command {
    @Override
    public List<HelpCommand.Entry> getHelp() {
        return List.of(
                new HelpCommand.Entry("", "Teleport you to the snail shell world."),
                new HelpCommand.Entry("<id>", "Teleports you to the specified snail shell.")
        );
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        final Player player = Require.player(sender);
        if (player == null) {
            return;
        }
        if (args.length == 0) {
            final Location location = new Location(SchneckenPlugin.INSTANCE.getWorld().getBukkit(), -10, 10, -10);
            final Block block = location.getBlock().getRelative(BlockFace.DOWN);
            if (block.isEmpty()) {
                block.setType(Material.STONE);
            }
            player.teleport(location);
            return;
        }
        final Shell<?> shell = Require.shell(sender, args[0]);
        if (shell == null) {
            return;
        }
        player.teleport(shell.getPosition().getSpawnLocation());
    }
}

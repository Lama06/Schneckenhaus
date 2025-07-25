package io.github.lama06.schneckenhaus.command;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.player.SchneckenPlayer;
import io.github.lama06.schneckenhaus.shell.Shell;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static io.github.lama06.schneckenhaus.language.Translator.t;

public final class TeleportCommand extends Command {
    @Override
    public List<HelpCommand.Entry> getHelp() {
        return List.of(
                new HelpCommand.Entry("", t("cmd_tp_help")),
                new HelpCommand.Entry("<id>", t("cmd_tp_help_id"))
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
        final Location previousLocation = player.getLocation();
        player.teleport(shell.getSpawnLocation());
        final SchneckenPlayer schneckenPlayer = new SchneckenPlayer(player);
        schneckenPlayer.pushPreviousLocation(previousLocation, false);
    }
}

package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.Permissions;
import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.player.SchneckenPlayer;
import io.github.lama06.schneckenhaus.position.IdGridPosition;
import io.github.lama06.schneckenhaus.shell.Shell;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public final class ClickShellSystem implements Listener {
    @EventHandler
    private void teleportToShell(final PlayerInteractEvent event) {
        final SchneckenPlugin plugin = SchneckenPlugin.INSTANCE;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        final Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }
        final BlockState state = clickedBlock.getState();
        if (!(state instanceof final TileState tileState)) {
            return;
        }
        final Integer id = Shell.BLOCK_ID.get(tileState);
        if (id == null) {
            return;
        }

        event.setCancelled(true);
        final Player player = event.getPlayer();
        final SchneckenPlayer schneckenPlayer = new SchneckenPlayer(player);
        if (!Permissions.require(player, Permissions.ENTER)) {
            return;
        }
        if (player.getWorld().equals(plugin.getWorld().getBukkit()) && !plugin.getSchneckenConfig().nesting) {
            return;
        }
        final IdGridPosition position = new IdGridPosition(id);
        final Shell<?> shell = plugin.getWorld().getShell(position);
        if (shell == null) {
            // This snail shell was deleted, also remove it from the world
            event.getClickedBlock().setType(Material.AIR);
            return;
        }
        if (!shell.isAllowedToEnter(player)) {
            player.sendMessage(Component.text("You are not allowed to enter this snail shell", NamedTextColor.RED)
                .appendNewline()
                .append(Component.text("Tell the shell's owner to add you to the whitelist")));
            return;
        }
        if (schneckenPlayer.isInside(position)) {
            return;
        }
        schneckenPlayer.pushPreviousLocation(player.getLocation());
        player.teleport(shell.getSpawnLocation());
        player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_DOOR_OPEN, 1, 1);
    }
}

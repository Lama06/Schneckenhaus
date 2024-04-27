package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.player.SchneckenPlayer;
import io.github.lama06.schneckenhaus.position.CoordinatesGridPosition;
import io.github.lama06.schneckenhaus.position.GridPosition;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Set;

public final class ClickShellDoorListener implements Listener {
    @EventHandler
    private void teleportBack(final PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        final Player player = event.getPlayer();
        final SchneckenPlayer schneckenPlayer = new SchneckenPlayer(player);
        final Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }
        if (!clickedBlock.getWorld().equals(SchneckenPlugin.INSTANCE.getWorld().getBukkit())) {
            return;
        }
        final GridPosition position = CoordinatesGridPosition.fromWorldPosition(clickedBlock.getLocation());
        if (position == null) {
            return;
        }
        if (!Set.of(position.getLowerDoorBlock(), position.getUpperDoorBlock()).contains(clickedBlock)) {
            return;
        }
        event.setCancelled(true);
        Location newLocation = schneckenPlayer.popPreviousLocation();
        if (newLocation == null) {
            World world = Bukkit.getWorld("world");
            if (world == null) { // Some servers don't have this world
                world = Bukkit.getWorlds().get(0);
            }
            newLocation = world.getSpawnLocation();
        }
        player.teleport(newLocation);
        player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_DOOR_CLOSE, 1, 1);
    }
}

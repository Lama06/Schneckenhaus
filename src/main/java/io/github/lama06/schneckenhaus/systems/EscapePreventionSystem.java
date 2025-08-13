package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.shell.Shell;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public final class EscapePreventionSystem extends System {
    private final Map<UUID, Integer> locations = new HashMap<>();

    @Override
    public boolean isEnabled() {
        return config.getEscapePrevention().isEnabled();
    }

    @Override
    public void start() {
        Bukkit.getScheduler().runTaskTimer(plugin, this::preventEscapes, 0, config.getEscapePrevention().getDelay());
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        rememberPlayerLocation(event.getPlayer(), event.getPlayer().getLocation());
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        locations.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() != TeleportCause.COMMAND &&
            event.getCause() != TeleportCause.PLUGIN &&
            event.getCause() != TeleportCause.SPECTATE
        ) {
            return;
        }
        rememberPlayerLocation(event.getPlayer(), event.getTo());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerRespawn(PlayerRespawnEvent event) {
        rememberPlayerLocation(event.getPlayer(), event.getRespawnLocation());
    }

    private void rememberPlayerLocation(Player player, Location location) {
        Shell shell = plugin.getShellManager().getShell(location);
        if (shell == null) {
            locations.remove(player.getUniqueId());
        } else {
            locations.put(player.getUniqueId(), shell.getId());
        }
    }

    private void preventEscapes() {
        Iterator<UUID> iterator = locations.keySet().iterator();
        while (iterator.hasNext()) {
            UUID uuid = iterator.next();
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                iterator.remove();
                continue;
            }
            if (Permission.BYPASS_ESCAPE_PREVENTION.check(player)) {
                continue;
            }

            Integer shellId = locations.get(uuid);
            Shell shell = plugin.getShellManager().getShell(shellId);
            if (shell == null) {
                iterator.remove();
                continue;
            }
            if (!config.getEscapePrevention().check(shell)) {
                continue;
            }
            if (shell.getArea().contains(player.getLocation())) {
                continue;
            }

            player.teleport(shell.getSpawnLocation());
            logger.info("prevented {} from escaping from shell {}", player.getName(), shellId);
        }
    }
}

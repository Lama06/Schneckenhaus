package io.github.lama06.schneckenhaus.systems.loading;

import io.github.lama06.schneckenhaus.SchneckenhausPlugin;
import io.github.lama06.schneckenhaus.event.ShellPlaceEvent;
import io.github.lama06.schneckenhaus.shell.Shell;
import io.github.lama06.schneckenhaus.systems.System;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Predicate;

public final class LoadShellSystem extends System {
    private static final int ADD_ITEM_TICKETS_DELAY = 5 * 20;
    private static final int CHECK_TICKETS_DELAY = 5 * 20;

    private final Map<Integer, Set<ShellLoadTicket>> tickets = new HashMap<>();

    @Override
    public void start() {
        Bukkit.getScheduler().runTaskTimer(SchneckenhausPlugin.INSTANCE, this::loadShellsInInventory, ADD_ITEM_TICKETS_DELAY, ADD_ITEM_TICKETS_DELAY);
        Bukkit.getScheduler().runTaskTimer(SchneckenhausPlugin.INSTANCE, this::removeInvalidTickets, CHECK_TICKETS_DELAY, CHECK_TICKETS_DELAY);
    }

    @EventHandler
    private void loadShellsOnChunkLoad(ChunkLoadEvent event) {
        if (config.getWorlds().containsKey(event.getWorld().getName())) {
            return;
        }
        Chunk chunk = event.getChunk();
        for (Shell shell : plugin.getShellManager().getShells(chunk)) {
            if (!config.getChunkLoading().check(shell)) {
                continue;
            }
            Set<ShellLoadTicket> shellTickets = tickets.computeIfAbsent(shell.getId(), k -> new HashSet<>());
            shellTickets.add(new BlockShellLoadTicket(
                event.getWorld().getName(),
                chunk.getX(),
                chunk.getZ(),
                shell.getId()
            ));
            loadShell(shell.getId());
        }
    }

    @EventHandler
    private void loadShellOnPlacement(ShellPlaceEvent event) {
        Block block = event.getBlock();
        if (config.getWorlds().containsKey(block.getWorld().getName())) {
            return;
        }
        Shell shell = event.getShell();
        if (!config.getChunkLoading().check(shell)) {
            return;
        }
        Set<ShellLoadTicket> shellTickets = tickets.computeIfAbsent(shell.getId(), k -> new HashSet<>());
        shellTickets.add(new BlockShellLoadTicket(
            block.getWorld().getName(),
            block.getChunk().getX(),
            block.getChunk().getZ(),
            shell.getId()
        ));
        loadShell(shell.getId());
    }

    private void loadShellsInInventory() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (ItemStack item : player.getInventory()) {
                Shell shell = plugin.getShellManager().getShell(item);
                if (shell == null) {
                    continue;
                }
                if (!config.getChunkLoading().check(shell)) {
                    continue;
                }
                Set<ShellLoadTicket> shellTickets = tickets.computeIfAbsent(shell.getId(), k -> new HashSet<>());
                shellTickets.add(new ItemShellLoadTicket(player.getUniqueId(), shell.getId()));
                loadShell(shell.getId());
            }
        }
    }

    private void removeInvalidTickets() {
        for (int shellId : tickets.keySet()) {
            Set<ShellLoadTicket> shellTickets = tickets.get(shellId);
            shellTickets.removeIf(Predicate.not(ShellLoadTicket::isStillValid));
            if (shellTickets.isEmpty()) {
                unloadShell(shellId);
            }
        }
        tickets.values().removeIf(Collection::isEmpty);
    }

    private void loadShell(int id) {
        Shell shell = plugin.getShellManager().getShell(id);
        if (shell == null) {
            return;
        }
        for (Chunk chunk : shell.getPosition().getChunks()) {
            chunk.addPluginChunkTicket(plugin);
        }
    }

    private void unloadShell(int id) {
        Shell shell = plugin.getShellManager().getShell(id);
        if (shell == null) {
            return;
        }
        for (Chunk chunk : shell.getPosition().getChunks()) {
            chunk.removePluginChunkTicket(plugin);
        }
    }
}

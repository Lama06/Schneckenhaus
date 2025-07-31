package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.position.IdGridPosition;
import io.github.lama06.schneckenhaus.shell.Shell;
import io.github.lama06.schneckenhaus.util.BlockPosition;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.function.Predicate;

public final class ChunkLoaderSystem implements Listener {
    private final Map<Integer, Set<Ticket>> tickets = new HashMap<>();

    public ChunkLoaderSystem() {
        Bukkit.getScheduler().runTaskTimer(SchneckenPlugin.INSTANCE, this::addShellItemTickets, 5*20, 5*20);
        Bukkit.getScheduler().runTaskTimer(SchneckenPlugin.INSTANCE, this::removeInvalidTickets, 5*20, 5*20);
    }

    @EventHandler
    private void on(ChunkLoadEvent event) {
        if (event.getWorld().equals(SchneckenPlugin.INSTANCE.getWorld().getBukkit())) {
            return;
        }
        Chunk chunk = event.getChunk();
        for (BlockState blockState : chunk.getTileEntities()) {
            if (!(blockState instanceof TileState tileState)) {
                continue;
            }
            Integer id = Shell.BLOCK_ID.get(tileState);
            if (id == null) {
                continue;
            }
            Shell<?> shell = SchneckenPlugin.INSTANCE.getWorld().getShell(new IdGridPosition(id));
            if (shell == null) {
                continue;
            }
            Set<Ticket> shellTickets = tickets.computeIfAbsent(id, k -> new HashSet<>());
            shellTickets.add(new ShellBlockTicket(
                event.getWorld().getName(),
                chunk.getX(),
                chunk.getZ(),
                new BlockPosition(blockState.getBlock()),
                id
            ));
            loadShell(id);
        }
    }

    @EventHandler(priority = EventPriority.HIGH) // run after the id of the block is set
    private void on(BlockPlaceEvent event) {
        Block blockPlaced = event.getBlockPlaced();
        if (blockPlaced.getWorld().equals(SchneckenPlugin.INSTANCE.getWorld().getBukkit())) {
            return;
        }
        BlockState state = blockPlaced.getState();
        if (!(state instanceof TileState tileState)) {
            return;
        }
        Integer id = Shell.BLOCK_ID.get(tileState);
        if (id == null) {
            return;
        }
        Set<Ticket> shellTickets = tickets.computeIfAbsent(id, k -> new HashSet<>());
        shellTickets.add(new ShellBlockTicket(
            blockPlaced.getWorld().getName(),
            blockPlaced.getChunk().getX(),
            blockPlaced.getChunk().getZ(),
            new BlockPosition(blockPlaced),
            id
        ));
        loadShell(id);
    }

    private void addShellItemTickets() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (ItemStack item : player.getInventory()) {
                if (item == null) {
                    continue;
                }
                ItemMeta itemMeta = item.getItemMeta();
                if (itemMeta == null) {
                    continue;
                }
                Integer id = Shell.ITEM_ID.get(itemMeta);
                if (id == null) {
                    continue;
                }
                Set<Ticket> shellTickets = tickets.computeIfAbsent(id, k -> new HashSet<>());
                shellTickets.add(new ShellItemTicket(player.getUniqueId(), id));
                loadShell(id);
            }
        }
    }

    private void removeInvalidTickets() {
        for (int shellId : tickets.keySet()) {
            Set<Ticket> shellTickets = tickets.get(shellId);
            shellTickets.removeIf(Predicate.not(Ticket::isStillValid));
            if (shellTickets.isEmpty()) {
                unloadShell(shellId);
            }
        }
        tickets.values().removeIf(Collection::isEmpty);
    }

    private void loadShell(int id) {
        Shell<?> shell = SchneckenPlugin.INSTANCE.getWorld().getShell(new IdGridPosition(id));
        if (shell == null) {
            return;
        }
        for (Chunk chunk : shell.getPosition().getChunks()) {
            chunk.addPluginChunkTicket(SchneckenPlugin.INSTANCE);
        }
    }

    private void unloadShell(int id) {
        Shell<?> shell = SchneckenPlugin.INSTANCE.getWorld().getShell(new IdGridPosition(id));
        if (shell == null) {
            return;
        }
        for (Chunk chunk : shell.getPosition().getChunks()) {
            chunk.removePluginChunkTicket(SchneckenPlugin.INSTANCE);
        }
    }

    private interface Ticket {
        boolean isStillValid();
    }

    private record ShellBlockTicket(String worldName, int chunkX, int chunkZ, BlockPosition position, int shellId) implements Ticket {
        @Override
        public boolean isStillValid() {
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                return false;
            }
            if (!world.isChunkLoaded(chunkX, chunkZ)) {
                return false;
            }
            Block block = position.getBlock(world);
            if (!(block.getState() instanceof TileState tileState)) {
                return false;
            }
            Integer id = Shell.BLOCK_ID.get(tileState);
            return id != null && id == shellId;
        }
    }

    private record ShellItemTicket(UUID playerUuid, int shellId) implements Ticket {
        @Override
        public boolean isStillValid() {
            Player player = Bukkit.getPlayer(playerUuid);
            if (player == null) {
                return false;
            }
            for (ItemStack item : player.getInventory()) {
                if (item == null) {
                    continue;
                }
                ItemMeta meta = item.getItemMeta();
                if (meta == null) {
                    continue;
                }
                Integer id = Shell.ITEM_ID.get(meta);
                if (id != null && id == shellId) {
                    return true;
                }
            }
            return false;
        }
    }
}

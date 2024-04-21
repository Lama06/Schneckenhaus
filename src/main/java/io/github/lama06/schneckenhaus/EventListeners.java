package io.github.lama06.schneckenhaus;

import io.github.lama06.schneckenhaus.position.CoordinatesGridPosition;
import io.github.lama06.schneckenhaus.position.IdGridPosition;
import io.github.lama06.schneckenhaus.util.LocationPersistentDataType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Set;

public final class EventListeners implements Listener {
    public EventListeners() {
        Bukkit.getScheduler().runTaskTimer(SchneckenPlugin.INSTANCE, this::repairSnailShells, 1, 0);
    }

    @EventHandler
    private void assignNewIdToSnailShell(final CraftItemEvent event) {
        if (!(event.getRecipe() instanceof final Keyed keyedRecipe)) {
            return;
        }
        final Recipes.Data data = SchneckenPlugin.INSTANCE.getRecipeManager().getRecipes().get(keyedRecipe.getKey());
        if (data == null) {
            return;
        }
        final ItemStack item = event.getCurrentItem();
        final ItemMeta meta = item.getItemMeta();
        final PersistentDataContainer itemData = meta.getPersistentDataContainer();
        final int id = SchneckenPlugin.INSTANCE.getAndIncrementNextId();
        itemData.set(Data.SHULKER_ITEM_ID, PersistentDataType.INTEGER, id);
        item.setItemMeta(meta);
        final SnailShell snailShell = new SnailShell(new IdGridPosition(id));
        snailShell.create(data.size(), data.color(), (Player) event.getWhoClicked());
    }

    @EventHandler
    private void preserveSnailHouseIdWhenPlaced(final BlockPlaceEvent event) {
        final ItemStack itemInHand = event.getItemInHand();
        final PersistentDataContainer itemData = itemInHand.getItemMeta().getPersistentDataContainer();
        final Integer id = itemData.get(Data.SHULKER_ITEM_ID, PersistentDataType.INTEGER);
        if (id == null) {
            return;
        }
        final Block block = event.getBlock();
        if (!(block.getState() instanceof final ShulkerBox shulkerBox)) {
            return;
        }
        final PersistentDataContainer shulkerPdc = shulkerBox.getPersistentDataContainer();
        shulkerPdc.set(Data.SHULKER_BLOCK_ID, PersistentDataType.INTEGER, id);
        shulkerBox.update();
    }

    @EventHandler
    private void preserveSnailHouseIdWhenBroken(final BlockDropItemEvent event) {
        if (!(event.getBlockState() instanceof final ShulkerBox shulkerBox)) {
            return;
        }
        final PersistentDataContainer shulkerData = shulkerBox.getPersistentDataContainer();
        final Integer id = shulkerData.get(Data.SHULKER_BLOCK_ID, PersistentDataType.INTEGER);
        if (id == null) {
            return;
        }
        if (event.getItems().size() != 1) {
            return;
        }
        final ItemStack item = event.getItems().get(0).getItemStack();
        final ItemMeta meta = item.getItemMeta();
        final PersistentDataContainer itemData = meta.getPersistentDataContainer();
        itemData.set(Data.SHULKER_ITEM_ID, PersistentDataType.INTEGER, id);
        item.setItemMeta(meta);
    }

    @EventHandler
    private void teleportToSnailHouse(final PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        final Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }
        final BlockState state = clickedBlock.getState();
        if (!(state instanceof final ShulkerBox shulkerBox)) {
            return;
        }
        final PersistentDataContainer shulkerData = shulkerBox.getPersistentDataContainer();
        final Integer id = shulkerData.get(Data.SHULKER_BLOCK_ID, PersistentDataType.INTEGER);
        if (id == null) {
            return;
        }
        final SnailShell snailShell = new SnailShell(new IdGridPosition(id));
        if (!snailShell.exists()) {
            return;
        }
        final Player player = event.getPlayer();
        if (player.getWorld().equals(SchneckenPlugin.INSTANCE.getWorld())) {
            return;
        }
        final Location oldLocation = player.getLocation();
        player.teleport(snailShell.getPosition().getSpawnLocation());
        final PersistentDataContainer playerData = player.getPersistentDataContainer();
        playerData.set(Data.PLAYER_PREVIOUS_LOCATION, LocationPersistentDataType.INSTANCE, oldLocation);
        player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_DOOR_OPEN, 1, 1);
    }

    @EventHandler
    private void teleportBack(final PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        final Player player = event.getPlayer();
        final Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }
        if (!clickedBlock.getWorld().equals(SchneckenPlugin.INSTANCE.getWorld())) {
            return;
        }
        final CoordinatesGridPosition position = CoordinatesGridPosition.fromWorldPosition(clickedBlock.getLocation());
        if (position == null) {
            return;
        }
        final SnailShell snailShell = new SnailShell(position);
        if (!snailShell.exists()) {
            return;
        }
        if (!Set.of(position.getLowerDoorBlock(), position.getUpperDoorBlock()).contains(clickedBlock)) {
            return;
        }
        event.setCancelled(true);
        final PersistentDataContainer data = player.getPersistentDataContainer();
        final Location newLocation;
        if (data.has(Data.PLAYER_PREVIOUS_LOCATION, LocationPersistentDataType.INSTANCE)) {
            newLocation = data.get(Data.PLAYER_PREVIOUS_LOCATION, LocationPersistentDataType.INSTANCE);
        } else {
            World world = Bukkit.getWorld("world");
            if (world == null) { // Some servers don't have this world
                world = Bukkit.getWorlds().get(0);
            }
            newLocation = world.getSpawnLocation();
        }
        data.remove(Data.PLAYER_PREVIOUS_LOCATION);
        player.teleport(newLocation);
        player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_DOOR_CLOSE, 1, 1);
    }

    @EventHandler
    private void preventBreakSnailShell(final BlockBreakEvent event) {
        final Player player = event.getPlayer();
        if (!player.getWorld().equals(SchneckenPlugin.INSTANCE.getWorld())) {
            return;
        }
        final CoordinatesGridPosition position = CoordinatesGridPosition.fromWorldPosition(event.getBlock().getLocation());
        if (position == null) {
            return;
        }
        final SnailShell snailShell = new SnailShell(position);
        if (!snailShell.exists()) {
            return;
        }
        if (!snailShell.getBlocks().contains(event.getBlock())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    private void preventExplosion(final BlockExplodeEvent event) {
        if (!event.getBlock().getWorld().equals(SchneckenPlugin.INSTANCE.getWorld())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    private void preventExplosion(final EntityExplodeEvent event) {
        if (!event.getEntity().getWorld().equals(SchneckenPlugin.INSTANCE.getWorld())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    private void preventBlockIgnite(final BlockIgniteEvent event) {
        if (!event.getBlock().getWorld().equals(SchneckenPlugin.INSTANCE.getWorld())) {
            return;
        }
        event.setCancelled(true);
    }

    private void repairSnailShells() {
        for (final Player player : SchneckenPlugin.INSTANCE.getWorld().getPlayers()) {
            final CoordinatesGridPosition position = CoordinatesGridPosition.fromWorldPosition(player.getLocation());
            if (position == null) {
                continue;
            }
            final SnailShell snailShell = new SnailShell(position);
            if (!snailShell.exists()) {
                continue;
            }
            snailShell.placeBlocks();
        }
    }
}

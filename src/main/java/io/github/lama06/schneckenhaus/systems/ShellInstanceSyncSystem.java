package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.config.ShellInstanceSyncConfig;
import io.github.lama06.schneckenhaus.shell.ShellPlacement;
import io.github.lama06.schneckenhaus.shell.Shell;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Set;

public final class ShellInstanceSyncSystem extends System {
    private final ShellInstanceSyncConfig syncConfig = config.getShellInstanceSync();

    @Override
    public boolean isEnabled() {
        return syncConfig.isEnabled();
    }

    @Override
    public void start() {
        int delay = syncConfig.getDelay();
        if (syncConfig.isShells()) {
            Bukkit.getScheduler().runTaskTimer(plugin, this::syncShells, delay, delay);
        }
        if (syncConfig.isPlacedShells()) {
            Bukkit.getScheduler().runTaskTimer(plugin, this::syncPlacedShells, delay, delay);
        }
        if (syncConfig.isItems()) {
            Bukkit.getScheduler().runTaskTimer(plugin, this::syncItems, delay, delay);
        }
        if (syncConfig.isDroppedItems()) {
            Bukkit.getScheduler().runTaskTimer(plugin, this::syncDroppedItems, delay, delay);
        }
    }

    private void syncShells() {
        for (Shell shell : plugin.getShellManager().getInhabitedShells()) {
            if (!shouldSyncShell(shell)) {
                continue;
            }
            shell.place();
        }
    }

    private void syncItems() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            InventoryView view = player.getOpenInventory();
            syncItemsInInventory(view.getTopInventory());
            syncItemsInInventory(view.getBottomInventory());
        }
    }

    private void syncItemsInInventory(Inventory inventory) {
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            ItemStack oldItem = inventory.getItem(slot);
            Shell shell = plugin.getShellManager().getShell(oldItem);
            if (shell == null) {
                continue;
            }
            if (!shouldSyncShellItem(shell, oldItem)) {
                continue;
            }
            ItemStack newItem = shell.createItem();
            newItem.setAmount(oldItem.getAmount());
            inventory.setItem(slot, newItem);
        }
    }

    private void syncPlacedShells() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Set<ShellPlacement> nearbyShells = plugin.getShellManager().getShellPlacements(
                player.getLocation().getBlock(),
                syncConfig.getPlacedShellsRange()
            );
            for (ShellPlacement shellPlacement : nearbyShells) {
                Shell shell = shellPlacement.getShell();
                Block block = shellPlacement.getBlock();
                if (!shouldSyncPlacedShell(shell, block)) {
                    continue;
                }
                block.setType(shell.getPlacementBlockType());
                BlockState state = block.getState();
                shell.initializePlacementBlockState(state);
                state.update();
            }
        }
    }

    private void syncDroppedItems() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Collection<Item> nearbyItems = player.getWorld().getNearbyEntitiesByType(
                Item.class,
                player.getLocation(),
                syncConfig.getDroppedItemsRange()
            );
            for (Item item : nearbyItems) {
                Shell shell = plugin.getShellManager().getShell(item.getItemStack());
                if (shell == null) {
                    continue;
                }
                if (!shouldSyncShellItem(shell, item.getItemStack())) {
                    continue;
                }
                item.setItemStack(shell.createItem());
            }
        }
    }

    private boolean shouldSyncShell(Shell shell) {
        if (!syncConfig.check(shell)) {
            return false;
        }
        Integer delay = shell.getAnimationDelay();
        if (delay == null) {
            return false;
        }
        return shouldSyncBecauseOfAnimation(delay);
    }

    private boolean shouldSyncShellItem(Shell shell, ItemStack item) {
        if (!syncConfig.check(shell)) {
            return false;
        }
        Integer delay = shell.getFactory().getItemAnimationDelay(shell);
        if (delay != null) {
            return shouldSyncBecauseOfAnimation(delay);
        }
        return shell.getFactory().getItemType(shell) != item.getType();
    }

    private boolean shouldSyncPlacedShell(Shell shell, Block block) {
        if (!syncConfig.check(shell)) {
            return false;
        }
        Integer delay = shell.getPlacementAnimationDelay();
        if (delay != null) {
            return shouldSyncBecauseOfAnimation(delay);
        }
        return shell.getPlacementBlockType() != block.getType() && !shell.getAlternativePlacementBlockTypes().contains(block.getType());
    }

    private boolean shouldSyncBecauseOfAnimation(int animationDelay) {
        int taskDelay = syncConfig.getDelay();
        if (animationDelay < taskDelay) {
            return true;
        } else {
            return ((Bukkit.getCurrentTick() / taskDelay) % (animationDelay / taskDelay)) == 0;
        }
    }
}

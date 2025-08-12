package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.config.AnimationConfig;
import io.github.lama06.schneckenhaus.shell.PlacedShell;
import io.github.lama06.schneckenhaus.shell.Shell;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Set;

public final class ShellAnimationSystem extends System {
    private AnimationConfig animationConfig = config.getAnimation();;

    @Override
    public void start() {
        animationConfig = config.getAnimation();
        if (!animationConfig.isEnabled()) {
            return;
        }
        int delay = animationConfig.getAnimationTaskDelay();
        if (animationConfig.isAnimateShells()) {
            Bukkit.getScheduler().runTaskTimer(plugin, this::animateShells, delay, delay);
        }
        if (animationConfig.isAnimatePlacedShells()) {
            Bukkit.getScheduler().runTaskTimer(plugin, this::animatePlacedShells, delay, delay);
        }
        if (animationConfig.isAnimateItems()) {
            Bukkit.getScheduler().runTaskTimer(plugin, this::animateItems, delay, delay);
        }
        if (animationConfig.isAnimateDroppedItems()) {
            Bukkit.getScheduler().runTaskTimer(plugin, this::animateDroppedItems, delay, delay);
        }
    }

    private void animateShells() {
        for (Shell shell : plugin.getShellManager().getInhabitedShells()) {
            if (!shouldAnimate(shell.getAnimationDelay())) {
                continue;
            }
            shell.place();
        }
    }

    private void animateItems() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            InventoryView view = player.getOpenInventory();
            animateItemsInInventory(view.getTopInventory());
            animateItemsInInventory(view.getBottomInventory());
        }
    }

    private void animateItemsInInventory(Inventory inventory) {
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            ItemStack oldItem = inventory.getItem(slot);
            Shell shell = plugin.getShellManager().getShell(oldItem);
            if (shell == null) {
                continue;
            }
            if (!shouldAnimate(shell.getFactory().getItemAnimationDelay(shell))) {
                continue;
            }
            ItemStack newItem = shell.createItem();
            newItem.setAmount(oldItem.getAmount());
            inventory.setItem(slot, newItem);
        }
    }

    private void animatePlacedShells() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Set<PlacedShell> nearbyShells = plugin.getShellManager().getShells(
                player.getLocation().getBlock(),
                animationConfig.getPlacedShellAnimationRange()
            );
            for (PlacedShell placedShell : nearbyShells) {
                Shell shell = placedShell.shell();
                Integer delay = shell.getFactory().getItemAnimationDelay(shell);
                if (!shouldAnimate(delay)) {
                    continue;
                }
                placedShell.block().setType(shell.createItem().getType()); // TODO copy item data
            }
        }
    }

    private void animateDroppedItems() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Collection<Item> nearbyItems = player.getWorld().getNearbyEntitiesByType(
                Item.class,
                player.getLocation(),
                animationConfig.getDroppedItemsAnimationRange()
            );
            for (Item item : nearbyItems) {
                Shell shell = plugin.getShellManager().getShell(item.getItemStack());
                if (shell == null) {
                    continue;
                }
                if (!shouldAnimate(shell.getFactory().getItemAnimationDelay(shell))) {
                    continue;
                }
                item.setItemStack(shell.createItem());
            }
        }
    }

    private boolean shouldAnimate(Integer delay) {
        if (delay == null) {
            return false;
        }
        int taskDelay = animationConfig.getAnimationTaskDelay();
        if (delay < taskDelay) {
            return true;
        } else {
            return ((Bukkit.getCurrentTick() / taskDelay) % (delay / taskDelay)) == 0;
        }
    }
}

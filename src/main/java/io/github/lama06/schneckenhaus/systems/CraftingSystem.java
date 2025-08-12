package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.recipe.CraftingInput;
import io.github.lama06.schneckenhaus.shell.*;
import io.github.lama06.schneckenhaus.shell.permission.ShellPermissionMode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;

public final class CraftingSystem extends System {
    @Override
    public void start() {
        Bukkit.getScheduler().runTaskTimer(plugin, this::animateCrafting, 0, config.getAnimation().getAnimationTaskDelay());
    }

    @EventHandler
    private void onPrepareCraft(PrepareItemCraftEvent event) {
        Player player = (Player) event.getView().getPlayer();
        prepareCrafting(player, event.getInventory());
    }

    private void animateCrafting() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!(player.getOpenInventory().getTopInventory() instanceof CraftingInventory inventory)) {
                continue;
            }
            prepareCrafting(player, inventory);
        }
    }

    private void prepareCrafting(Player player, CraftingInventory inventory) {
        if (!Permission.CRAFT_SHELL.check(player)) {
            return;
        }
        ShellBuilder builder = getCraftingResult(inventory, player);
        if (builder == null) {
            return;
        }
        inventory.setResult(builder.getFactory().createItem(builder));
    }

    @EventHandler
    private void onCraft(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!Permission.CRAFT_SHELL.check(player)) {
            return;
        }
        if (!(event.getClickedInventory() instanceof CraftingInventory inventory)) {
            return;
        }
        if (event.getSlotType() != InventoryType.SlotType.RESULT) {
            return;
        }
        ShellBuilder builder = getCraftingResult(inventory, player);
        if (builder == null) {
            return;
        }

        Shell shell = builder.build();
        if (shell == null) {
            return;
        }
        event.setCancelled(true);
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, null);
        }
        player.getOpenInventory().setCursor(shell.createItem());
    }

    private ShellBuilder getCraftingResult(CraftingInventory inventory, Player player) {
        for (ShellFactory factory : ShellFactories.getFactories()) {
            ShellBuilder builder = factory.newBuilder();
            builder.setCreationType(ShellCreationType.CRAFTING);
            builder.setCreator(player.getUniqueId());
            builder.setOwner(player.getUniqueId());
            builder.setEnterPermissionMode(ShellPermissionMode.EVERYBODY);
            builder.setBuildPermissionMode(ShellPermissionMode.EVERYBODY);
            if (!factory.getCraftingResult(builder, new CraftingInput(inventory))) {
                continue;
            }
            return builder;
        }
        return null;
    }
}

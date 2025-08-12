package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.recipe.CraftingInput;
import io.github.lama06.schneckenhaus.shell.*;
import io.github.lama06.schneckenhaus.shell.permission.ShellPermissionMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;

public final class CraftingSystem extends System {
    @EventHandler
    private void onPrepareCraft(PrepareItemCraftEvent event) {
        Player player = (Player) event.getView().getPlayer();
        if (!Permission.CRAFT_SHELL.check(player)) {
            return;
        }
        ShellBuilder builder = getCraftingResult(event.getInventory(), player);
        if (builder == null) {
            return;
        }
        event.getInventory().setResult(builder.getFactory().createItem(builder));
    }

    @EventHandler
    private void onCraft(CraftItemEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!Permission.CRAFT_SHELL.check(player)) {
            return;
        }
        ShellBuilder builder = getCraftingResult(event.getInventory(), player);
        if (builder == null) {
            return;
        }
        Integer id = builder.build();
        if (id == null) {
            return;
        }
        Shell shell = plugin.getShellManager().getShell(id);
        if (shell == null) {
            return;
        }
        event.setCurrentItem(shell.createItem());
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

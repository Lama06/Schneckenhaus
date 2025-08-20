package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.config.ShellInstanceSyncConfig;
import io.github.lama06.schneckenhaus.util.CraftingInput;
import io.github.lama06.schneckenhaus.shell.*;
import io.github.lama06.schneckenhaus.shell.permission.ShellPermissionMode;
import io.github.lama06.schneckenhaus.util.ConcurrencyUtils;
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
        ShellInstanceSyncConfig syncConfig = config.getShellInstanceSync();
        if (syncConfig.isEnabled() && syncConfig.isItems()) {
            Bukkit.getScheduler().runTaskTimer(plugin, this::animateCrafting, 0, syncConfig.getDelay());
        }
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
        CraftingResult result = getCraftingResult(inventory, player);
        if (result == null) {
            return;
        }
        inventory.setResult(result.shellBuilder.getFactory().createItem(result.shellBuilder));
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
        CraftingResult result = getCraftingResult(inventory, player);
        if (result == null) {
            return;
        }

        inventory.setMatrix(result.remainingInput().getMatrix());
        event.setCancelled(true);

        result.shellBuilder.build().thenAcceptAsync(
            shell -> {
                if (shell == null) {
                    return;
                }

                player.getOpenInventory().setCursor(shell.createItem());
            },
            ConcurrencyUtils::runOnMainThread
        ).exceptionally(e -> {
            logger.error("failed to craft item", e);
            throw new RuntimeException();
        });
    }

    private CraftingResult getCraftingResult(CraftingInventory inventory, Player player) {
        for (ShellFactory factory : ShellFactories.getFactories()) {
            ShellBuilder builder = factory.newBuilder();
            builder.setCreationType(ShellCreationType.CRAFTING);
            builder.setCreator(player.getUniqueId());
            builder.setOwner(player.getUniqueId());
            builder.setEnterPermissionMode(ShellPermissionMode.EVERYBODY);
            builder.setBuildPermissionMode(ShellPermissionMode.EVERYBODY);
            CraftingInput input = new CraftingInput(inventory);
            if (!factory.getCraftingResult(builder, input)) {
                continue;
            }
            return new CraftingResult(builder, input);
        }
        return null;
    }

    private record CraftingResult(ShellBuilder shellBuilder, CraftingInput remainingInput) { }
}

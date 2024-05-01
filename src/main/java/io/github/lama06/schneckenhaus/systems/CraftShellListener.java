package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.Permissions;
import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.recipe.RegisteredShellRecipe;
import io.github.lama06.schneckenhaus.shell.Shell;
import org.bukkit.Keyed;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public final class CraftShellListener implements Listener {
    @EventHandler
    private void assignNewIdToCraftedShell(final CraftItemEvent event) {
        if (!(event.getRecipe() instanceof final Keyed keyedRecipe)) {
            return;
        }
        final SchneckenPlugin plugin = SchneckenPlugin.INSTANCE;
        final RegisteredShellRecipe<?> recipe = plugin.getRecipeManager().getRecipes().get(keyedRecipe.getKey());
        if (recipe == null) {
            return;
        }
        final Player player = (Player) event.getWhoClicked();
        if (!player.hasPermission(Permissions.CRAFT)) {
            event.setCancelled(false);
            return;
        }
        final Shell<?> shell = recipe.createShell(player);
        final ItemStack item = shell.createItem();
        event.setCurrentItem(item);
    }
}

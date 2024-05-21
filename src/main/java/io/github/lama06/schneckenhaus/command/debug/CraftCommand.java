package io.github.lama06.schneckenhaus.command.debug;

import io.github.lama06.schneckenhaus.SchneckenConfig;
import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.command.Command;
import io.github.lama06.schneckenhaus.command.Require;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class CraftCommand extends Command {
    private Map<String, List<Material>> getIngredients() {
        final SchneckenConfig config = SchneckenPlugin.INSTANCE.getSchneckenConfig();
        final List<Material> shulkerSimple = new ArrayList<>(config.shulker.ingredients);
        shulkerSimple.add(Material.SHULKER_BOX);
        for (int i = 0; i < 3; i++) {
            shulkerSimple.add(config.shulker.sizeIngredient);
        }
        final List<Material> shulkerColored = new ArrayList<>(config.shulker.ingredients);
        shulkerColored.add(Material.PINK_SHULKER_BOX);
        for (int i = 0; i < 3; i++) {
            shulkerColored.add(config.shulker.sizeIngredient);
        }
        final List<Material> chest = new ArrayList<>(config.chest.ingredients);
        chest.add(Material.CHEST);
        for (int i = 0; i < 3; i++) {
            chest.add(config.chest.sizeIngredient);
        }
        return Map.of(
                "shulker_simple", shulkerSimple,
                "shulker_colored", shulkerColored,
                "chest", chest
        );
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        final Player player = Require.player(sender);
        if (player == null) {
            return;
        }
        if (args.length == 0) {
            return;
        }
        final List<Material> ingredients = getIngredients().get(args[0]);
        if (ingredients == null) {
            return;
        }
        final InventoryView inventory = player.openWorkbench(null, true);
        final CraftingInventory crafting = (CraftingInventory) inventory.getTopInventory();
        for (int i = 0; i < 9 && i < ingredients.size(); i++) {
            crafting.setItem(i + 1, new ItemStack(ingredients.get(i)));
        }
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String[] args) {
        return List.copyOf(getIngredients().keySet());
    }
}

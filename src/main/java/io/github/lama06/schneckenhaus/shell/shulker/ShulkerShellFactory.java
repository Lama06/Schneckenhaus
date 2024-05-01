package io.github.lama06.schneckenhaus.shell.shulker;

import io.github.lama06.schneckenhaus.position.GridPosition;
import io.github.lama06.schneckenhaus.shell.builtin.BuiltinShellFactory;
import io.github.lama06.schneckenhaus.shell.builtin.BuiltinShellRecipe;
import io.github.lama06.schneckenhaus.util.MaterialUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

public final class ShulkerShellFactory extends BuiltinShellFactory<ShulkerShellConfig> {
    public static final ShulkerShellFactory INSTANCE = new ShulkerShellFactory();

    private ShulkerShellFactory() { }

    @Override
    public String getName() {
        return "shulker";
    }

    @Override
    public int getMinSize() {
        return 2;
    }

    @Override
    public int getMaxSize() {
        return 32 - 2; // Minus two because of the walls on each side
    }

    @Override
    public ShulkerShell instantiate(final GridPosition position, final ShulkerShellConfig config) {
        return new ShulkerShell(position, config);
    }

    @Override
    protected List<BuiltinShellRecipe<ShulkerShellConfig>> getBuiltinRecipes() {
        final List<BuiltinShellRecipe<ShulkerShellConfig>> recipes = new ArrayList<>();
        recipes.add(new BuiltinShellRecipe<>("default", Material.SHULKER_BOX) {
            @Override
            public ShulkerShellConfig getConfig(final int size) {
                return new ShulkerShellConfig(size, DyeColor.PINK);
            }
        });
        for (final DyeColor color : DyeColor.values()) {
            recipes.add(new BuiltinShellRecipe<>(color.toString(), MaterialUtil.getColoredShulkerBox(color)) {
                @Override
                public ShulkerShellConfig getConfig(final int size) {
                    return new ShulkerShellConfig(size, color);
                }
            });
        }
        return recipes;
    }

    @Override
    protected ShulkerShellConfig loadBuiltinConfig(final int size, final PersistentDataContainer data) {
        return new ShulkerShellConfig(size, ShulkerShellConfig.COLOR.get(data));
    }

    @Override
    protected List<String> tabCompleteBuiltinConfig(final CommandSender sender, final String[] args) {
        if (args.length != 0 && args.length != 1) {
            return List.of();
        }
        return Arrays.stream(DyeColor.values()).map(Enum::name).map(String::toLowerCase).toList();
    }

    @Override
    protected ShulkerShellConfig parseBuiltinConfig(final int size, final CommandSender sender, final String[] args) {
        final RandomGenerator rnd = ThreadLocalRandom.current();
        final DyeColor color;
        if (args.length == 1) {
            color = Arrays.stream(DyeColor.values()).filter(c -> c.name().equalsIgnoreCase(args[0])).findAny().orElse(null);
            if (color == null) {
                sender.spigot().sendMessage(new ComponentBuilder("Invalid color: " + args[1]).color(ChatColor.RED).build());
                return null;
            }
        } else {
            final DyeColor[] dyeColors = DyeColor.values();
            color = dyeColors[rnd.nextInt(dyeColors.length)];
        }
        return new ShulkerShellConfig(size, color);
    }

    @Override
    protected List<String> getBuiltinConfigCommandTemplates() {
        return List.of("", "<color>");
    }
}

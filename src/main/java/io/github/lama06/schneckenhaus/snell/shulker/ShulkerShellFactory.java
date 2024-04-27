package io.github.lama06.schneckenhaus.snell.shulker;

import io.github.lama06.schneckenhaus.position.GridPosition;
import io.github.lama06.schneckenhaus.snell.Shell;
import io.github.lama06.schneckenhaus.snell.ShellFactory;
import io.github.lama06.schneckenhaus.snell.ShellRecipe;
import io.github.lama06.schneckenhaus.util.MaterialUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

public final class ShulkerShellFactory extends ShellFactory<ShulkerShellConfig> {
    public static final ShulkerShellFactory INSTANCE = new ShulkerShellFactory();

    private ShulkerShellFactory() { }

    @Override
    public String getName() {
        return "shulker";
    }

    @Override
    public String getPluginConfigName() {
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
    public Shell instantiate(final GridPosition position, final ShulkerShellConfig config) {
        return new ShulkerShell(position, config);
    }

    @Override
    public Set<ShellRecipe<ShulkerShellConfig>> getRecipes() {
        final Set<ShellRecipe<ShulkerShellConfig>> recipes = new HashSet<>();
        for (final DyeColor color : DyeColor.values()) {
            recipes.add(new ShellRecipe<>(color.name(), MaterialUtil.getColoredShulkerBox(color)) {
                @Override
                public ShulkerShellConfig getConfig(final int size) {
                    return new ShulkerShellConfig(size, color);
                }
            });
        }
        recipes.add(new ShellRecipe<>("uncolored", Material.SHULKER_BOX) {
            @Override
            public ShulkerShellConfig getConfig(final int size) {
                return new ShulkerShellConfig(size, DyeColor.PURPLE);
            }
        });
        return recipes;
    }

    @Override
    protected ShulkerShellConfig instantiateConfig() {
        return new ShulkerShellConfig();
    }

    @Override
    protected void loadAdditionalConfig(final ShulkerShellConfig config, final PersistentDataContainer data) {
        config.setColor(ShulkerShellConfig.COLOR.get(data));
    }

    @Override
    protected List<String> tabCompleteAdditionalConfig(final CommandSender sender, final String[] args) {
        if (args.length != 0 && args.length != 1) {
            return List.of();
        }
        return Arrays.stream(DyeColor.values()).map(Enum::name).map(String::toLowerCase).toList();
    }

    @Override
    protected boolean parseAdditionalConfig(final ShulkerShellConfig config, final CommandSender sender, final String[] args) {
        final RandomGenerator rnd = ThreadLocalRandom.current();
        final DyeColor color;
        if (args.length == 1) {
            color = Arrays.stream(DyeColor.values()).filter(c -> c.name().equalsIgnoreCase(args[0])).findAny().orElse(null);
            if (color == null) {
                sender.spigot().sendMessage(new ComponentBuilder("Invalid color: " + args[1]).color(ChatColor.RED).build());
                return false;
            }
        } else {
            final DyeColor[] dyeColors = DyeColor.values();
            color = dyeColors[rnd.nextInt(dyeColors.length)];
        }
        config.setColor(color);
        return true;
    }

    @Override
    protected List<String> getAdditionalConfigCommandTemplates() {
        return List.of("", "<color>");
    }
}
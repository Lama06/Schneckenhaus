package io.github.lama06.schneckenhaus.shell.builtin;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.command.Require;
import io.github.lama06.schneckenhaus.shell.ShellFactory;
import io.github.lama06.schneckenhaus.shell.ShellRecipe;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;
import java.util.stream.IntStream;

public abstract class BuiltinShellFactory<C extends BuiltinShellConfig> extends ShellFactory<C> {
    public abstract int getMinSize();

    public abstract int getMaxSize();

    protected abstract List<BuiltinShellRecipe<C>> getBuiltinRecipes();

    @Override
    public final List<ShellRecipe<C>> getRecipes() {
        final ConfigurationSection config = getGlobalConfig();
        if (!config.getBoolean("enabled")) {
            return List.of();
        }

        final int initialSize = config.getInt("initial_size");
        final int sizePerIngredient = config.getInt("size_per_ingredient");
        final Material sizeIngredient = Registry.MATERIAL.match(getGlobalConfig().getString("size_ingredient"));
        final List<Material> additionalIngredients = config.getStringList("ingredients").stream().map(Registry.MATERIAL::match).toList();

        final List<ShellRecipe<C>> recipes = new ArrayList<>();
        for (final BuiltinShellRecipe<C> builtinRecipe : getBuiltinRecipes()) {
            for (int sizeIngredientAmount = 0; sizeIngredientAmount < getMaxSizeIngredientAmount(); sizeIngredientAmount++) {
                final int rawSize = initialSize + sizeIngredientAmount * sizePerIngredient;
                final int size = Math.min(getMaxSize(), Math.max(getMinSize(), rawSize));
                final String key = builtinRecipe.getKey() + "_" + size;
                final List<Material> ingredients = new ArrayList<>(additionalIngredients);
                ingredients.add(builtinRecipe.getIngredient());
                for (int i = 0; i < sizeIngredientAmount; i++) {
                    ingredients.add(sizeIngredient);
                }
                recipes.add(new ShellRecipe<>(key, ingredients, builtinRecipe.getConfig(size)));
            }
        }
        return recipes;
    }

    protected void loadAdditionalConfig(final C config, final PersistentDataContainer data) { }

    protected abstract C instantiateConfig();

    @Override
    public final C loadConfig(final PersistentDataContainer data) {
        final int size = BuiltinShellConfig.SIZE.get(data);
        final C config = instantiateConfig();
        config.setSize(size);
        loadAdditionalConfig(config, data);
        return config;
    }

    protected List<String> tabCompleteAdditionalConfig(final CommandSender sender, final String[] args) {
        return List.of();
    }

    @Override
    public final List<String> tabCompleteConfig(final CommandSender sender, final String[] args) {
        if (args.length == 0 || args.length == 1) {
            return IntStream.range(getMinSize(), getMaxSize() + 1).mapToObj(Integer::toString).toList();
        }
        return tabCompleteAdditionalConfig(sender, Arrays.copyOfRange(args, 1, args.length));
    }

    protected boolean parseAdditionalConfig(final C config, final CommandSender sender, final String[] args) {
        return true;
    }

    @Override
    public final C parseConfig(final CommandSender sender, final String[] args) {
        final C config = instantiateConfig();
        final RandomGenerator rnd = ThreadLocalRandom.current();
        final int size;
        final String[] remainingArgs;
        if (args.length >= 1) {
            Integer parsedSize = Require.integer(sender, args[0], getMinSize(), getMaxSize());
            if (parsedSize == null) {
                return null;
            }
            size = parsedSize;
            remainingArgs = Arrays.copyOfRange(args, 1, args.length);
        } else {
            size = getMinSize() + rnd.nextInt(getMaxSize() - getMinSize() + 1);
            remainingArgs = args;
        }
        config.setSize(size);
        if (!parseAdditionalConfig(config, sender, remainingArgs)) {
            return null;
        }
        return config;
    }

    protected List<String> getAdditionalConfigCommandTemplates() {
        return List.of("");
    }

    @Override
    public final List<String> getConfigCommandTemplates() {
        final List<String> templates = new ArrayList<>();
        final List<String> additionalTemplates = getAdditionalConfigCommandTemplates();
        if (additionalTemplates.contains("")) {
            templates.add("");
        }
        for (final String additionalTemplate : additionalTemplates) {
            templates.add("<size>" + (additionalTemplate.isEmpty() ? "" : " " + additionalTemplate));
        }
        return templates;
    }

    private ConfigurationSection getGlobalConfig() {
        return SchneckenPlugin.INSTANCE.getConfig().getConfigurationSection(getName());
    }

    private int getMaxSizeIngredientAmount() {
        final ConfigurationSection config = getGlobalConfig();
        final int ingredients = config.getStringList("ingredients").size();
        return 9 - 1 - ingredients;
    }
}

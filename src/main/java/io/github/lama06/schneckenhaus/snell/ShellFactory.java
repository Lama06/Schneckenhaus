package io.github.lama06.schneckenhaus.snell;

import io.github.lama06.schneckenhaus.command.Require;
import io.github.lama06.schneckenhaus.position.GridPosition;
import org.bukkit.command.CommandSender;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;
import java.util.stream.IntStream;

public abstract class ShellFactory<C extends ShellConfig> {
    public abstract String getName();

    public abstract String getPluginConfigName();

    public abstract Set<ShellRecipe<C>> getRecipes();

    public abstract Shell instantiate(final GridPosition position, final C config);

    public abstract int getMinSize();

    public abstract int getMaxSize();

    protected abstract C instantiateConfig();

    protected void loadAdditionalConfig(final C config, final PersistentDataContainer data) { }

    public final C loadConfig(final PersistentDataContainer data) {
        final int size = ShellConfig.SIZE.get(data);
        final C config = instantiateConfig();
        config.setSize(size);
        loadAdditionalConfig(config, data);
        return config;
    }

    protected List<String> tabCompleteAdditionalConfig(final CommandSender sender, final String[] args) {
        return List.of();
    }

    public final List<String> tabCompleteConfig(final CommandSender sender, final String[] args) {
        if (args.length == 0 || args.length == 1) {
            return IntStream.range(getMinSize(), getMaxSize() + 1).mapToObj(Integer::toString).toList();
        }
        return tabCompleteAdditionalConfig(sender, Arrays.copyOfRange(args, 1, args.length));
    }

    protected boolean parseAdditionalConfig(final C config, final CommandSender sender, final String[] args) {
        return true;
    }

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
}

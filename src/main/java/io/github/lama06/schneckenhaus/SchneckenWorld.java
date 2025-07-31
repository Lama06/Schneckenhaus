package io.github.lama06.schneckenhaus;

import io.github.lama06.schneckenhaus.data.Attribute;
import io.github.lama06.schneckenhaus.position.GridPosition;
import io.github.lama06.schneckenhaus.position.IdGridPosition;
import io.github.lama06.schneckenhaus.shell.*;
import io.github.lama06.schneckenhaus.util.PluginVersion;
import org.bukkit.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public final class SchneckenWorld implements PersistentDataHolder {
    public static final String NAME = "schneckenhaus";
    public static final Attribute<Integer> NEXT_ID = new Attribute<>("next_id", PersistentDataType.INTEGER);

    private static final String GENERATOR_SETTINGS = """
             {
                "layers": [
                    {
                        "block": "air",
                        "height": 1
                    }
                ],
                "biome": "plains"
             }
             """;

    private final World world;

    public SchneckenWorld() {
        this.world = loadWorld();
    }

    private World loadWorld() {
        final World world = WorldCreator.name(NAME)
                .environment(World.Environment.NORMAL)
                .type(WorldType.FLAT)
                .generatorSettings(GENERATOR_SETTINGS)
                .generateStructures(false)
                .createWorld();
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.MOB_GRIEFING, false);
        world.setGameRule(GameRule.FIRE_DAMAGE, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setTime(12000);
        if (!NEXT_ID.has(world)) {
            NEXT_ID.set(world, 1);
        }
        return world;
    }

    private int getAndIncrementNextShellId() {
        final int nextId = NEXT_ID.get(world);
        NEXT_ID.set(world, nextId + 1);
        return nextId;
    }

    public int getNumberOfShells() {
        return NEXT_ID.get(world) - 1;
    }

    public PersistentDataContainer getShellData(final GridPosition position) {
        return position.getCornerBlock().getChunk().getPersistentDataContainer();
    }

    private <C extends ShellConfig> Shell<C> getShell(
            final GridPosition position,
            final PersistentDataContainer data,
            final ShellFactory<C> factory
    ) {
        final C config = factory.loadConfig(data);
        return factory.instantiate(position, config);
    }

    public Shell<?> getShell(final GridPosition position) {
        if (position.getId() >= NEXT_ID.get(world)) {
            return null; // This snail shell hasn't been created yet.
        }
        new ShellUpdater(position).update();
        final PersistentDataContainer data = getShellData(position);
        if (Shell.DELETED.getOrDefault(data, false)) {
            return null;
        }
        final String typeName = Shell.TYPE.get(data);
        for (final ShellFactory<?> factory : ShellFactories.getFactories()) {
            if (!factory.getName().equals(typeName)) {
                continue;
            }
            return getShell(position, data, factory);
        }
        return null;
    }

    public <C extends ShellConfig> Shell <C> createShell(final ShellFactory<C> factory, final OfflinePlayer creator, final C config) {
        final int id = getAndIncrementNextShellId();
        final GridPosition position = new IdGridPosition(id);
        final PersistentDataContainer data = getShellData(position);
        Shell.TYPE.set(data, factory.getName());
        Shell.CREATOR.set(data, creator.getUniqueId());
        Shell.ACCESS_MODE.set(data, AccessMode.EVERYBODY);
        Shell.BLACKLIST.set(data, List.of());
        Shell.WHITELIST.set(data, List.of());
        ShellUpdater.DATA_VERSION.set(data, PluginVersion.current());
        config.store(data);
        final Shell<C> shell = factory.instantiate(position, config);
        shell.placeInitially();
        return shell;
    }

    public void getShellsByPlayer(UUID player, Consumer<List<Integer>> callback) {
        List<Integer> result = new ArrayList<>();
        int numberOfShells = getNumberOfShells();
        int[] shellsChecked = {0};
        for (int id = 1; id <= numberOfShells; id++) {
            int finalId = id;
            world.getChunkAtAsync(new IdGridPosition(id).getCornerBlock()).thenAccept(chunk -> {
                if (Shell.CREATOR.get(chunk).equals(player)) {
                    result.add(finalId);
                }
                shellsChecked[0]++;
                if (shellsChecked[0] == numberOfShells) {
                    Collections.sort(result);
                    callback.accept(result);
                }
            });
        }
    }

    @Override
    public PersistentDataContainer getPersistentDataContainer() {
        return world.getPersistentDataContainer();
    }

    public World getBukkit() {
        return world;
    }
}

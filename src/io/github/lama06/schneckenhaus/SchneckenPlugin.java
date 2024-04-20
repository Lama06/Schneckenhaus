package io.github.lama06.schneckenhaus;

import org.bukkit.*;
import org.bukkit.command.PluginCommand;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public final class SchneckenPlugin extends JavaPlugin {
    public static final String WORLD_NAME = "schneckenhaus";
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
    public static SchneckenPlugin INSTANCE;
    private World world;
    private Recipes recipes;

    public SchneckenPlugin() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        loadWorld();

        recipes = new Recipes();
        recipes.registerRecipes();

        Bukkit.getPluginManager().registerEvents(new EventListeners(), this);

        final PluginCommand command = getCommand("schneckenhaus");
        final SchneckenCommand executor = new SchneckenCommand();
        command.setExecutor(executor);
        command.setTabCompleter(executor);
    }

    private void loadWorld() {
        world = WorldCreator.name(WORLD_NAME)
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
        final PersistentDataContainer data = world.getPersistentDataContainer();
        if (!data.has(Data.WORLD_NEXT_ID, PersistentDataType.INTEGER)) {
            data.set(Data.WORLD_NEXT_ID, PersistentDataType.INTEGER, 1);
        }
    }

    /**
     * Returns the snail shell id that will be assigned to the next snail shell which is created.
     */
    public int getNextSnailShellId() {
        final PersistentDataContainer data = world.getPersistentDataContainer();
        return data.get(Data.WORLD_NEXT_ID, PersistentDataType.INTEGER);
    }

    /**
     * Returns the next snail shell id and increments it after that.
     */
    public int getAndIncrementNextId() {
        final PersistentDataContainer data = world.getPersistentDataContainer();
        final int nextId = data.get(Data.WORLD_NEXT_ID, PersistentDataType.INTEGER);
        data.set(Data.WORLD_NEXT_ID, PersistentDataType.INTEGER, nextId + 1);
        return nextId;
    }

    /**
     * Returns the world which contains all snail shells.
     */
    public World getWorld() {
        return world;
    }

    public Recipes getRecipeManager() {
        return recipes;
    }
}

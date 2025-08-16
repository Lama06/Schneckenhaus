package io.github.lama06.schneckenhaus;

import org.bukkit.*;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public final class WorldManager {
    private static final String EMPTY_GENERATOR_SETTINGS = """
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

    private final SchneckenhausPlugin plugin = SchneckenhausPlugin.INSTANCE;
    private final Logger logger = plugin.getSLF4JLogger();

    private final Map<String, World> worlds = new HashMap<>();

    public void load() {
        for (String worldName : plugin.getPluginConfig().getWorlds().keySet()) {
            if (Bukkit.getWorld(worldName) == null) {
                logger.info("creating new snail shell world: {}", worldName);
            }
            World world = WorldCreator.name(worldName)
                .environment(World.Environment.NORMAL)
                .type(WorldType.FLAT)
                .generatorSettings(EMPTY_GENERATOR_SETTINGS)
                .generateStructures(false)
                .createWorld();
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setTime(6000);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            world.setClearWeatherDuration(1000000);
            world.setGameRule(GameRule.MOB_GRIEFING, false);
            world.setGameRule(GameRule.FIRE_DAMAGE, false);
            world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
            worlds.put(worldName, world);
        }
    }

    public Map<String, World> getWorlds() {
        return worlds;
    }
}

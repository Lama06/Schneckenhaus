package io.github.lama06.schneckenhaus;

import io.github.lama06.schneckenhaus.config.WorldConfig;
import io.github.lama06.schneckenhaus.util.ConstantsHolder;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

import java.util.HashMap;
import java.util.Map;

public final class WorldManager extends ConstantsHolder {
    private static String getVoidGeneratorSettings(String biome) {
        return """
             {
                "layers": [
                    {
                        "block": "air",
                        "height": 1
                    }
                ],
                "biome": "%biome%"
             }
             """.replace("%biome%", biome);
    }

    private final Map<String, World> worlds = new HashMap<>();

    public void load() {
        for (String worldName : config.getWorlds().keySet()) {
            WorldConfig worldConfig = config.getWorlds().get(worldName);
            logger.info("loading snail shell world: {}", worldName);
            World world = WorldCreator.name(worldName)
                .environment(World.Environment.NORMAL)
                .type(WorldType.FLAT)
                .generatorSettings(getVoidGeneratorSettings(worldConfig.getBiome()))
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

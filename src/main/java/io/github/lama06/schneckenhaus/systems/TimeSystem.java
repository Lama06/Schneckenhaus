package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.config.WorldConfig;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;

public final class TimeSystem extends System {
    @Override
    public void start() {
        Bukkit.getScheduler().runTaskTimer(plugin, this::syncTime, 0, 50);
    }

    private void syncTime() {
        for (String worldName : config.getWorlds().keySet()) {
            WorldConfig worldConfig = config.getWorlds().get(worldName);
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                continue;
            }
            String timeSyncWorldName = worldConfig.getTimeSyncWorld();
            if (config.getWorlds().containsKey(timeSyncWorldName)) {
                continue;
            }
            World timeSyncWorld = Bukkit.getWorld(timeSyncWorldName);
            if (timeSyncWorld == null) {
                continue;
            }
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, timeSyncWorld.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE));
            world.setTime(timeSyncWorld.getTime());
        }
    }
}

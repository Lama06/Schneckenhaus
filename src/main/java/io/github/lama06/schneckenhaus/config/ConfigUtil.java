package io.github.lama06.schneckenhaus.config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ConfigUtil {
    public static Map<String, Object> serializeLocation(Location location, boolean serializeWorld) {
        if (location == null) {
            return null;
        }
        Map<String, Object> result = new LinkedHashMap<>();
        World world = location.getWorld();
        if (serializeWorld && world != null) {
            result.put("world", world.getName());
        }
        result.put("x", location.getX());
        result.put("y", location.getY());
        result.put("z", location.getZ());
        result.put("yaw", location.getYaw());
        result.put("pitch", location.getPitch());
        return result;
    }

    public static Location deserializeLocation(Map<?, ?> config) {
        if (config == null) {
            return null;
        }
        World world = null;
        if (config.get("world") instanceof String worldName) {
            world = Bukkit.getWorld(worldName);
        }
        if (!(config.get("x") instanceof Double x) || !(config.get("y") instanceof Double y) || !(config.get("z") instanceof Double z)) {
            return null;
        }
        Location location = new Location(world, x, y, z);
        if (config.get("yaw") instanceof Double yaw) {
            location.setYaw((float) yaw.doubleValue());
        }
        if (config.get("pitch") instanceof Double pitch) {
            location.setPitch((float) pitch.doubleValue());
        }
        return location;
    }
}

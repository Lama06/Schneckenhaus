package io.github.lama06.schneckenhaus.config;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.shell.chest.GlobalChestShellConfig;
import io.github.lama06.schneckenhaus.shell.custom.GlobalCustomShellConfig;
import io.github.lama06.schneckenhaus.shell.head.GlobalHeadShellConfig;
import io.github.lama06.schneckenhaus.shell.shulker.GlobalShulkerShellConfig;
import org.bukkit.DyeColor;
import org.bukkit.Location;

import java.util.*;

public final class SchneckenhausConfig {
    private final GlobalShulkerShellConfig shulker = new GlobalShulkerShellConfig();
    private final GlobalChestShellConfig chest = new GlobalChestShellConfig();
    private final GlobalHeadShellConfig head = new GlobalHeadShellConfig();
    private final Map<String, GlobalCustomShellConfig> custom = new LinkedHashMap<>();

    private Map<?, ?> homeShell = Map.of(
        "type", "shulker",
        "size", 16,
        "color", DyeColor.WHITE.name().toLowerCase(Locale.ROOT)
    );

    private final Map<String, WorldConfig> worlds = new HashMap<>();

    private final ConditionalFeatureConfig chunkLoading = new ConditionalFeatureConfig();
    private final ConditionalFeatureConfig hoppers = new ConditionalFeatureConfig();
    private final ShellInstanceSyncConfig shellInstancesSync = new ShellInstanceSyncConfig();
    private final ConditionalFeatureConfig theftPrevention = new ConditionalFeatureConfig();
    private final ConditionalTaskFeatureConfig escapePrevention = new ConditionalTaskFeatureConfig(20);
    private final ConditionalTaskFeatureConfig repairSystem = new ConditionalTaskFeatureConfig(200);
    private Location fallbackExitLocation;

    public SchneckenhausConfig() {
        worlds.put("schneckenhaus", new WorldConfig(true, List.of()));
    }

    public void deserialize(Map<?, ?> config) {
        if (config.get("shulker") instanceof Map<?, ?> shulker) {
            this.shulker.deserialize(shulker);
        }

        if (config.get("chest") instanceof Map<?, ?> chest) {
            this.chest.deserialize(chest);
        }

        if (config.get("head") instanceof Map<?, ?> head) {
            this.head.deserialize(head);
        }

        if (config.get("custom") instanceof Map<?, ?> customShellConfigs) {
            for (Object key : customShellConfigs.keySet()) {
                if (!(key instanceof String name)) {
                    continue;
                }
                if (!(config.get(name) instanceof Map<?, ?> customShellConfig)) {
                    continue;
                }
                GlobalCustomShellConfig customShell = GlobalCustomShellConfig.deserialize(customShellConfig);
                if (customShell == null) {
                    continue;
                }
                custom.put(name, customShell);
            }
        }

        if (config.get("home_shell") instanceof Map<?, ?> homeShell) {
            this.homeShell = homeShell;
        }

        if (config.get("worlds") instanceof Map<?, ?> worldsConfig) {
            worlds.clear();
            for (Object key : worldsConfig.keySet()) {
                if (!(key instanceof String name)) {
                    continue;
                }
                if (!(worldsConfig.get(name) instanceof Map<?, ?> worldConfig)) {
                    continue;
                }
                WorldConfig world = new WorldConfig();
                world.deserialize(worldConfig);
                worlds.put(name, world);
            }
        }

        if (config.get("chunk_loading") instanceof Map<?, ?> chunkLoading) {
            this.chunkLoading.deserialize(chunkLoading);
        }

        if (config.get("hoppers") instanceof Map<?, ?> hoppers) {
            this.hoppers.deserialize(hoppers);
        }

        if (config.get("shell_instances_sync") instanceof Map<?, ?> shellInstancesSync) {
            this.shellInstancesSync.deserialize(shellInstancesSync);
        }

        if (config.get("theft_prevention") instanceof Map<?, ?> theftPrevention) {
            this.theftPrevention.deserialize(theftPrevention);
        }

        if (config.get("escape_prevention") instanceof Map<?, ?> escapePrevention) {
            this.escapePrevention.deserialize(escapePrevention);
        }

        if (config.get("repair_system") instanceof Map<?, ?> repairSystem)
            this.repairSystem.deserialize(repairSystem);

        if (config.get("fallback_exit_location") instanceof Map<?, ?> fallbackExitLocation) {
            //noinspection unchecked
            this.fallbackExitLocation = Location.deserialize((Map<String, Object>) fallbackExitLocation);
        }
    }

    public Map<String, Object> serialize() {
        Map<String, Object> config = new LinkedHashMap<>();

        config.put("shulker", shulker.serialize());
        config.put("chest", chest.serialize());
        config.put("head", head.serialize());

        Map<String, Object> custom = new LinkedHashMap<>();
        for (String name : this.custom.keySet()) {
            custom.put(name, this.custom.get(name).serialize());
        }
        config.put("custom", custom);

        config.put("home_shell", homeShell);

        Map<String, Object> worlds = new HashMap<>();
        for (String name : this.worlds.keySet()) {
            worlds.put(name, this.worlds.get(name).serialize());
        }
        config.put("worlds", worlds);

        config.put("chunk_loading", chunkLoading.serialize());
        config.put("hoppers", hoppers.serialize());
        config.put("shell_instances_sync", shellInstancesSync.serialize());
        config.put("theft_prevention", theftPrevention.serialize());
        config.put("escape_prevention", escapePrevention.serialize());
        config.put("repair_system", repairSystem.serialize());
        config.put("fallback_exit_location", fallbackExitLocation == null ? null : fallbackExitLocation.serialize());

        config.put("data_version", SchneckenPlugin.INSTANCE.getPluginMeta().getVersion());

        return config;
    }

    public GlobalShulkerShellConfig getShulker() {
        return shulker;
    }

    public GlobalChestShellConfig getChest() {
        return chest;
    }

    public GlobalHeadShellConfig getHead() {
        return head;
    }

    public Map<String, GlobalCustomShellConfig> getCustom() {
        return custom;
    }

    public Map<?, ?> getHomeShell() {
        return homeShell;
    }

    public Map<String, WorldConfig> getWorlds() {
        return worlds;
    }

    public ConditionalFeatureConfig getChunkLoading() {
        return chunkLoading;
    }

    public ConditionalFeatureConfig getHoppers() {
        return hoppers;
    }

    public ShellInstanceSyncConfig getShellInstanceSync() {
        return shellInstancesSync;
    }

    public ConditionalFeatureConfig getTheftPrevention() {
        return theftPrevention;
    }

    public ConditionalTaskFeatureConfig getEscapePrevention() {
        return escapePrevention;
    }

    public ConditionalTaskFeatureConfig getRepairSystem() {
        return repairSystem;
    }

    public Location getFallbackExitLocation() {
        return fallbackExitLocation;
    }
}

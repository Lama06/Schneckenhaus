package io.github.lama06.schneckenhaus.config;

import io.github.lama06.schneckenhaus.SchneckenhausPlugin;
import io.github.lama06.schneckenhaus.shell.chest.ChestShellConfig;
import io.github.lama06.schneckenhaus.shell.custom.CustomShellConfig;
import io.github.lama06.schneckenhaus.shell.head.HeadShellConfig;
import io.github.lama06.schneckenhaus.shell.permission.ShellPermissionMode;
import io.github.lama06.schneckenhaus.shell.shulker.ShulkerShellConfig;
import org.bukkit.DyeColor;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class SchneckenhausConfig {
    private final ShulkerShellConfig shulker = new ShulkerShellConfig();
    private final ChestShellConfig chest = new ChestShellConfig();
    private final HeadShellConfig head = new HeadShellConfig();
    private final Map<String, CustomShellConfig> custom = new LinkedHashMap<>();

    private Map<?, ?> homeShell = Map.of(
        "type", "shulker",
        "size", 16,
        "color", DyeColor.WHITE.name().toLowerCase(Locale.ROOT)
    );

    private final Map<String, WorldConfig> worlds = new HashMap<>();

    private final ConditionalTaskFeatureConfig chunkLoading = new ConditionalTaskFeatureConfig(100);
    private final ConditionalFeatureConfig hoppers = new ConditionalFeatureConfig();
    private final ShellInstanceSyncConfig shellInstancesSync = new ShellInstanceSyncConfig();
    private final ConditionalFeatureConfig theftPrevention = new ConditionalFeatureConfig(false);
    private final ConditionalTaskFeatureConfig escapePrevention = new ConditionalTaskFeatureConfig(60);
    private final ConditionalTaskFeatureConfig repairSystem = new ConditionalTaskFeatureConfig(200);
    private Location fallbackExitLocation;

    private ShellPermissionMode defaultEnterPermissionMode = ShellPermissionMode.EVERYBODY;
    private ShellPermissionMode defaultBuildPermissionMode = ShellPermissionMode.EVERYBODY;

    public SchneckenhausConfig() {
        worlds.put("schneckenhaus", new WorldConfig(true));
    }

    public void deserialize(Map<?, ?> config) {
        if (config.get("data_version") instanceof String dataVersionString
            && (dataVersionString.startsWith("1.") || dataVersionString.startsWith("2."))) {
            return;
        }

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
                if (!(customShellConfigs.get(name) instanceof Map<?, ?> customShellConfig)) {
                    continue;
                }
                CustomShellConfig customShell = CustomShellConfig.deserialize(customShellConfig);
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
            this.fallbackExitLocation = ConfigUtil.deserializeLocation(fallbackExitLocation);
        }

        if (config.get("default_enter_permission_mode") instanceof String enterModeStr) {
            try {
                defaultEnterPermissionMode = ShellPermissionMode.valueOf(enterModeStr.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ignored) { }
        }

        if (config.get("default_build_permission_mode") instanceof String buildModeStr) {
            try {
                defaultBuildPermissionMode = ShellPermissionMode.valueOf(buildModeStr.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ignored) { }
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
        config.put("fallback_exit_location", ConfigUtil.serializeLocation(fallbackExitLocation, true));

        config.put("default_enter_permission_mode", defaultEnterPermissionMode.name());
        config.put("default_build_permission_mode", defaultBuildPermissionMode.name());

        config.put("data_version", SchneckenhausPlugin.INSTANCE.getPluginMeta().getVersion());

        return config;
    }

    public ShulkerShellConfig getShulker() {
        return shulker;
    }

    public ChestShellConfig getChest() {
        return chest;
    }

    public HeadShellConfig getHead() {
        return head;
    }

    public Map<String, CustomShellConfig> getCustom() {
        return custom;
    }

    public Map<?, ?> getHomeShell() {
        return homeShell;
    }

    public Map<String, WorldConfig> getWorlds() {
        return worlds;
    }

    public ConditionalTaskFeatureConfig getChunkLoading() {
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

    public ShellPermissionMode getDefaultEnterPermissionMode() {
        return defaultEnterPermissionMode;
    }

    public ShellPermissionMode getDefaultBuildPermissionMode() {
        return defaultBuildPermissionMode;
    }

}

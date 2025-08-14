package io.github.lama06.schneckenhaus.util;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import org.bukkit.Bukkit;

public final class ConcurrencyUtils {
    public static void runOnMainThread(Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            runnable.run();
        } else {
            Bukkit.getScheduler().runTask(SchneckenPlugin.INSTANCE, runnable);
        }
    }
}

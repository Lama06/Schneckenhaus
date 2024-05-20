package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public final class Systems {
    private static final Listener[] SYSTEMS = {
            new BreakShellSystem(),
            new PlayerLeaveShellSystem(),
            new ClickShellSystem(),
            new CraftShellSystem(),
            new DestroyShellSystem(),
            new PlaceShellSystem(),
            new RepairShellSystem()
    };

    public static void start() {
        for (final Listener listener : SYSTEMS) {
            Bukkit.getPluginManager().registerEvents(listener, SchneckenPlugin.INSTANCE);
        }
    }

    private Systems() { }
}

package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public final class Systems {
    private static final Listener[] LISTENERS = {
            new BreakShellListener(),
            new ClickShellDoorListener(),
            new ClickShellListener(),
            new CraftShellListener(),
            new DestoryShellListener(),
            new PlaceShellListener(),
    };

    public static void start() {
        for (final Listener listener : LISTENERS) {
            Bukkit.getPluginManager().registerEvents(listener, SchneckenPlugin.INSTANCE);
        }
        new RepairShellSystem();
    }
}

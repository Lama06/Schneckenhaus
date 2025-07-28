package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public final class Systems {
    private static final Listener[] SYSTEMS = {
        new BreakShellSystem(),
        new LeaveShellSystem(),
        new ClickShellSystem(),
        new CraftShellSystem(),
        new DestroyShellSystem(),
        new PlaceShellSystem(),
        new RepairShellSystem(),
        new HomeShellSystem(),
        new ShellMenuSystem(),
        new KickPlayersSystem(),
        new RainbowSystem(),
        new DispenseShellSystem(),
        new LockShellSystem(),
        new SetupLanguageSystem()
    };

    public static void start() {
        for (final Listener listener : SYSTEMS) {
            Bukkit.getPluginManager().registerEvents(listener, SchneckenPlugin.INSTANCE);
        }
    }

    private Systems() {
    }
}

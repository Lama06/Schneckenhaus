package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import org.bukkit.Bukkit;

public final class Systems {
    public static void start() {
        start(new BreakShellSystem());
        start(new CraftingSystem());
        start(new EnterShellSystem());
        start(new HomeShellSystem());
        start(new HomeShellSystem());
        start(new LeaveShellSystem());
        start(new LeaveShellSystem());
        start(new PermissionSystem());
        start(new PlaceShellSystem());
        start(new ProtectShellSystem());
        start(new RestrictEnderChestSystem());
        start(new ShellAnimationSystem());
        start(new ShellMenuSystem());
        start(new TranslationSystem());
    }

    private static void start(System system) {
        Bukkit.getPluginManager().registerEvents(system, SchneckenPlugin.INSTANCE);
        system.start();
    }
}

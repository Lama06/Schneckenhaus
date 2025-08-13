package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.systems.loading.LoadShellSystem;
import org.bukkit.Bukkit;

public final class Systems {
    public static void start() {
        start(new LoadShellSystem());
        start(new BreakShellSystem());
        start(new CraftingSystem());
        start(new EnterShellSystem());
        start(new EscapePreventionSystem());
        start(new HomeShellSystem());
        start(new HopperSystem());
        start(new LeaveShellSystem());
        start(new PermissionSystem());
        start(new PlaceShellSystem());
        start(new ProtectShellSystem());
        start(new RestrictEnderChestSystem());
        start(new ShellInstanceSyncSystem());
        start(new ShellMenuSystem());
        start(new TranslationSystem());
        start(new ShellRepairSystem());
    }

    private static void start(System system) {
        if (system.isEnabled()) {
            Bukkit.getPluginManager().registerEvents(system, SchneckenPlugin.INSTANCE);
            system.start();
        }
    }
}

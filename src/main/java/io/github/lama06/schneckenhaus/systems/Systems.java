package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.SchneckenhausPlugin;
import io.github.lama06.schneckenhaus.systems.loading.ShellLoadingSystem;
import org.bukkit.Bukkit;

public final class Systems {
    private final ShellLoadingSystem loadingSystem = new ShellLoadingSystem();

    public void start() {
        start(loadingSystem);
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
        start(new TimeSystem());
        start(new RemoveInvalidShellItemsSystem());
        start(new DispenserSystem());
        start(new PistonSystem());
    }

    private void start(System system) {
        if (system.isEnabled()) {
            Bukkit.getPluginManager().registerEvents(system, SchneckenhausPlugin.INSTANCE);
            system.start();
        }
    }

    public ShellLoadingSystem getLoadingSystem() {
        return loadingSystem;
    }
}

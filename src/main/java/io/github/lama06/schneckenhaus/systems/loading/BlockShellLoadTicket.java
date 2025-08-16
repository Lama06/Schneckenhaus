package io.github.lama06.schneckenhaus.systems.loading;

import io.github.lama06.schneckenhaus.SchneckenhausPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;

record BlockShellLoadTicket(
    String worldName,
    int chunkX,
    int chunkZ,
    int shellId
) implements ShellLoadTicket {
    @Override
    public boolean isStillValid() {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return false;
        }
        if (!world.isChunkLoaded(chunkX, chunkZ)) {
            return false;
        }
        return SchneckenhausPlugin.INSTANCE.getShellManager().getShellIds(worldName, chunkX, chunkZ).contains(shellId);
    }
}

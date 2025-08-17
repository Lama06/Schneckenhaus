package io.github.lama06.schneckenhaus.systems.loading;

import io.github.lama06.schneckenhaus.SchneckenhausPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

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

    @Override
    public @NotNull String toString() {
        return "block in world %s in chunk %s %s".formatted(worldName, chunkX, chunkZ);
    }
}

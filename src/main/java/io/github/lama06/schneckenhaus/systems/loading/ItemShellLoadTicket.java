package io.github.lama06.schneckenhaus.systems.loading;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

record ItemShellLoadTicket(UUID playerUuid, int shellId) implements ShellLoadTicket {
    @Override
    public boolean isStillValid() {
        Player player = Bukkit.getPlayer(playerUuid);
        if (player == null) {
            return false;
        }
        for (ItemStack item : player.getInventory()) {
            if (item == null) {
                continue;
            }
            Integer id = SchneckenPlugin.INSTANCE.getShellManager().getShellId(item);
            if (id != null && id == shellId) {
                return true;
            }
        }
        return false;
    }
}

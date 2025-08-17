package io.github.lama06.schneckenhaus.systems.loading;

import io.github.lama06.schneckenhaus.SchneckenhausPlugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
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
            Integer id = SchneckenhausPlugin.INSTANCE.getShellManager().getShellId(item);
            if (id != null && id == shellId) {
                return true;
            }
        }
        return false;
    }

    @Override
    public @NotNull String toString() {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerUuid);
        return "item in inventory of " + Objects.requireNonNullElse(player.getName(), playerUuid.toString());
    }
}

package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.player.SchneckenPlayer;
import io.github.lama06.schneckenhaus.position.IdGridPosition;
import io.github.lama06.schneckenhaus.shell.HomeShellConfig;
import io.github.lama06.schneckenhaus.shell.Shell;
import io.github.lama06.schneckenhaus.shell.shulker.ShulkerShellConfig;
import io.github.lama06.schneckenhaus.shell.shulker.ShulkerShellFactory;
import org.bukkit.DyeColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;

public final class HomeShellSystem implements Listener {
    @EventHandler
    private void giveHomeOnFirstJoin(PlayerJoinEvent event) {
        HomeShellConfig homeConfig = SchneckenPlugin.INSTANCE.getSchneckenConfig().home;
        if (!homeConfig.enabled) {
            return;
        }

        Integer homeId = SchneckenPlayer.HOME.get(event.getPlayer());
        if (homeId != null) {
            Shell<?> shell = SchneckenPlugin.INSTANCE.getWorld().getShell(new IdGridPosition(homeId));
            if (shell != null) {
                return;
            } else {
                SchneckenPlayer.HOME.remove(event.getPlayer()); // home was deleted, give player a new one
            }
        }

        ShulkerShellConfig shellConfig = new ShulkerShellConfig(SchneckenPlugin.INSTANCE.getSchneckenConfig().home.size, DyeColor.WHITE);
        Shell<ShulkerShellConfig> shell = SchneckenPlugin.INSTANCE.getWorld().createShell(ShulkerShellFactory.INSTANCE, event.getPlayer(), shellConfig);
        SchneckenPlayer.HOME.set(event.getPlayer(), shell.getId());
        event.getPlayer().give(shell.createItem());
    }

    @EventHandler
    private static void preventHomelessness(PlayerRespawnEvent event) {
        HomeShellConfig homeConfig = SchneckenPlugin.INSTANCE.getSchneckenConfig().home;
        if (!homeConfig.preventHomelessness) {
            return;
        }

        Integer homeId = SchneckenPlayer.HOME.get(event.getPlayer());
        if (homeId == null) {
            return;
        }
        Shell<?> shell = SchneckenPlugin.INSTANCE.getWorld().getShell(new IdGridPosition(homeId));
        if (shell == null) {
            return;
        }

        for (ItemStack item : event.getPlayer().getInventory()) {
            if (item == null) {
                continue;
            }
            Integer id = Shell.ITEM_ID.get(item.getItemMeta());
            if (id == null) {
                continue;
            }
            if (id.equals(homeId)) {
                return;
            }
        }

        event.getPlayer().give(shell.createItem());
    }

    @EventHandler
    private void preventDropHomeOnDeath(PlayerDeathEvent event) {
        if (!SchneckenPlugin.INSTANCE.getSchneckenConfig().home.preventHomelessness) {
            return;
        }

        Integer homeId = SchneckenPlayer.HOME.get(event.getPlayer());
        if (homeId == null) {
            return;
        }

        Iterator<ItemStack> iterator = event.getDrops().iterator();
        while (iterator.hasNext()) {
            ItemStack item = iterator.next();
            if (item == null) {
                continue;
            }
            Integer id = Shell.ITEM_ID.get(item.getItemMeta());
            if (id == null || !id.equals(homeId)) {
                continue;
            }
            iterator.remove();
        }
    }
}

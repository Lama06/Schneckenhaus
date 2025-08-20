package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.player.SchneckenhausPlayer;
import io.github.lama06.schneckenhaus.shell.*;
import io.github.lama06.schneckenhaus.util.ConcurrencyUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.Map;

public final class HomeShellSystem extends System {
    @EventHandler
    private void createHomeOnJoinIfMissing(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!Permission.HOME_SHELL.check(player)) {
            return;
        }
        SchneckenhausPlayer schneckenhausPlayer = new SchneckenhausPlayer(player);
        Shell homeShell = schneckenhausPlayer.getHomeShell();
        if (homeShell != null) {
            return;
        }

        Map<?, ?> homeShellConfig = config.getHomeShell();
        if (!(homeShellConfig.get("type") instanceof String type)) {
            logger.error("home shell config has no type attribute");
            return;
        }
        ShellFactory factory = ShellFactories.getByName(type);
        if (factory == null) {
            logger.error("unknown shell type in home shell config: {}", type);
            return;
        }
        ShellBuilder builder = factory.newBuilder();
        if (!factory.deserializeConfig(builder, homeShellConfig)) {
            logger.error("failed to deserialize home shell config");
            return;
        }
        builder.setCreationType(ShellCreationType.HOME);
        builder.setCreator(player.getUniqueId());
        builder.setOwner(player.getUniqueId());
        builder.build().thenAcceptAsync(
            shell -> {
                if (shell == null) {
                    return;
                }

                player.give(shell.createItem());
                schneckenhausPlayer.setHomeShell(shell.getId());
            },
            ConcurrencyUtils::runOnMainThread
        ).exceptionally(e -> {
            logger.error("failed to create home shell", e);
            return null;
        });
    }

    @EventHandler
    private void preventHomelessness(PlayerRespawnEvent event) {
        ensureHasHomeInInventory(event.getPlayer());
    }

    @EventHandler
    private void preventHomelessness(PlayerJoinEvent event) {
        ensureHasHomeInInventory(event.getPlayer());
    }

    private void ensureHasHomeInInventory(Player player) {
        if (!Permission.NEVER_HOMELESS.check(player)) {
            return;
        }

        Shell home = new SchneckenhausPlayer(player).getHomeShell();
        if (home == null) {
            return;
        }

        for (ItemStack item : player.getInventory()) {
            Shell shell = plugin.getShellManager().getShell(item);
            if (shell == null) {
                continue;
            }
            if (shell.getId() == home.getId()) {
                return;
            }
        }

        player.give(home.createItem());
    }

    @EventHandler
    private void preventDropHomeOnDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        if (!Permission.NEVER_HOMELESS.check(player)) {
            return;
        }

        Shell home = new SchneckenhausPlayer(player).getHomeShell();
        if (home == null) {
            return;
        }

        Iterator<ItemStack> iterator = event.getDrops().iterator();
        while (iterator.hasNext()) {
            ItemStack item = iterator.next();
            if (item == null) {
                continue;
            }
            Integer id = plugin.getShellManager().getShellId(item);
            if (id != null && id == home.getId()) {
                iterator.remove();
            }
        }
    }
}

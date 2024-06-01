package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.position.CoordinatesGridPosition;
import io.github.lama06.schneckenhaus.shell.Shell;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public final class LockShellSystem implements Listener {
    @EventHandler
    private void onClick(final PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }
        final Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }
        if (!Tag.DOORS.isTagged(clickedBlock.getType())) {
            return;
        }
        final Player player = event.getPlayer();
        final CoordinatesGridPosition position = CoordinatesGridPosition.fromWorldPosition(player.getLocation());
        if (position == null) {
            return;
        }
        final Shell<?> shell = SchneckenPlugin.INSTANCE.getWorld().getShell(position);
        if (shell == null) {
            return;
        }
        if (!shell.getBlocks().containsKey(clickedBlock)) {
            return;
        }
        if (!shell.getCreator().equals(player)) {
            final ComponentBuilder builder = new ComponentBuilder();
            builder.append("You can't ").color(ChatColor.RED);
            builder.append(shell.isLocked() ? "unlock" : "lock");
            builder.append(" this snail shell because you don't own it.");
            player.spigot().sendMessage(builder.build());
            return;
        }
        shell.setLocked(!shell.isLocked());
        final ComponentBuilder builder = new ComponentBuilder();
        builder.append("You successfully ").color(ChatColor.GREEN);
        builder.append(shell.isLocked() ? "locked" : "unlocked");
        builder.append(" this snail shell.\n");
        if (shell.isLocked()) {
            builder.append("Others except for you can no longer enter it.");
        } else {
            builder.append("Now everyone can enter it.");
        }
        builder.reset();
        player.spigot().sendMessage(builder.build());
    }
}

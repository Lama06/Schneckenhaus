package io.github.lama06.schneckenhaus.ui;

import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.player.SchneckenhausPlayer;
import io.github.lama06.schneckenhaus.shell.Shell;
import io.github.lama06.schneckenhaus.shell.ShellPlacement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class ShellPlacementsScreen extends Screen {
    private final Shell shell;

    public ShellPlacementsScreen(Player player, Shell shell) {
        super(player);
        this.shell = shell;
    }

    @Override
    protected Component getTitle() {
        return Message.SHELL_PLACEMENTS.asComponent(NamedTextColor.YELLOW);
    }

    @Override
    protected int getHeight() {
        return 6;
    }

    @Override
    protected void draw() {
        Set<ShellPlacement> placements = plugin.getShellManager().getShellPlacements(shell);
        int slot = 0;
        for (ShellPlacement placement : placements) {
            setItem(InventoryPosition.fromSlot(slot++), () -> createItem(placement), shell.getFactory().getItemAnimationDelay(shell), () -> {
                if (!Permission.PLACEMENTS_TELEPORT.check(player)) {
                    return;
                }
                Location location = placement.getTeleportLocation();
                if (!config.getWorlds().containsKey(location.getWorld().getName())) {
                    new SchneckenhausPlayer(player).clearPreviousLocations();
                }
                player.teleport(location);
            });
        }
    }

    private ItemStack createItem(ShellPlacement placement) {
        ItemStack item = shell.createItem(false);
        item.editMeta(meta -> {
            List<Component> lore = new ArrayList<>();
            if (Permission.PLACEMENTS_VIEW_POSITIONS.check(player)) {
                Block block = placement.block();
                lore.add(Message.WORLD.asComponent(NamedTextColor.AQUA)
                    .append(Component.text(": " + block.getWorld().getName()))
                );
                lore.add(Message.POSITION.asComponent(NamedTextColor.AQUA)
                    .append(Component.text(": %s %s %s".formatted(block.getX(), block.getY(), block.getZ())))
                );
            }
            if (Permission.PLACEMENTS_TELEPORT.check(player)) {
                lore.add(Message.CLICK_TO_TELEPORT.asComponent(NamedTextColor.YELLOW));
            }
            meta.lore(lore);
        });
        return item;
    }
}

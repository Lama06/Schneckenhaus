package io.github.lama06.schneckenhaus.ui;

import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.player.SchneckenhausPlayer;
import io.github.lama06.schneckenhaus.shell.Shell;
import io.github.lama06.schneckenhaus.shell.ShellPlacement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class ShellPlacementsScreen extends Screen {
    private final Shell shell;
    private boolean renaming;

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
        if (!renaming && Permission.PLACEMENTS_RENAME.check(player)) {
            ItemStack item = new ItemStack(Material.NAME_TAG);
            item.editMeta(meta -> {
                meta.customName(Message.RENAME.asComponent(NamedTextColor.YELLOW));
            });
            setItem(InventoryPosition.fromSlot(slot++), item, () -> {
                renaming = true;
                redraw();
            });
        }
        for (ShellPlacement placement : placements) {
            setItem(InventoryPosition.fromSlot(slot++), () -> createItem(placement), shell.getFactory().getItemAnimationDelay(shell), () -> {
                if (renaming) {
                    new InputScreen(
                        player,
                        Message.RENAME.asComponent(),
                        Objects.requireNonNullElse(placement.getName(), ""),
                        newName -> {
                            placement.setName(newName);
                            new ShellPlacementsScreen(player, shell).open();
                        },
                        () -> new ShellPlacementsScreen(player, shell).open()
                    ).open();
                    return;
                }

                if (!Permission.PLACEMENTS_TELEPORT.check(player)) {
                    return;
                }
                Location location = placement.getExitPositionOrFallback();
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
            String name = placement.getName();
            if (name != null) {
                meta.customName(MiniMessage.miniMessage().deserialize(name));
            }

            List<Component> lore = new ArrayList<>();
            if (Permission.PLACEMENTS_VIEW_POSITIONS.check(player)) {
                Block block = placement.getBlock();
                lore.add(Message.WORLD.asComponent(NamedTextColor.AQUA)
                    .append(Component.text(": " + block.getWorld().getName()))
                );
                lore.add(Message.POSITION.asComponent(NamedTextColor.AQUA)
                    .append(Component.text(": %s %s %s".formatted(block.getX(), block.getY(), block.getZ())))
                );
            }
            if (!renaming && Permission.PLACEMENTS_TELEPORT.check(player)) {
                lore.add(Message.CLICK_TO_TELEPORT.asComponent(NamedTextColor.YELLOW));
            }
            if (renaming) {
                lore.add(Message.CLICK_TO_CHANGE_NAME.asComponent(NamedTextColor.YELLOW));
            }
            meta.lore(lore);
        });
        return item;
    }
}

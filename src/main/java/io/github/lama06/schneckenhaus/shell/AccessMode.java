package io.github.lama06.schneckenhaus.shell;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public enum AccessMode {
    // DON'T RENAME ENUM CONSTANTS, THEY ARE USED FOR SERIALIZATION
    EVERYBODY(
        Component.text("Everybody", NamedTextColor.GREEN),
        Material.OAK_DOOR,
        List.of(Component.text("All players can enter this snail shell"))
    ) {
        @Override
        public boolean check(Shell<?> shell, Player player) {
            return true;
        }
    },
    BLACKLIST(
        Component.text("Blacklist", NamedTextColor.RED),
        Material.WITHER_ROSE,
        List.of(
            Component.text("Everyone except blacklisted players can enter this snail shell"),
            Constants.OP_NOTE
        )
    ) {
        @Override
        public boolean check(Shell<?> shell, Player player) {
            List<UUID> blacklist = Shell.BLACKLIST.getOrDefault(shell, List.of());
            return !blacklist.contains(player.getUniqueId()) || canAlwaysJoin(shell, player);
        }
    },
    WHITELIST(
        Component.text("Whitelist"),
        Material.WHITE_TULIP,
        List.of(
            Component.text("Besides its owner, only players listed here can enter this snail shell"),
            Constants.OP_NOTE
        )
    ) {
        @Override
        public boolean check(Shell<?> shell, Player player) {
            List<UUID> whitelist = Shell.WHITELIST.getOrDefault(shell, List.of());
            return whitelist.contains(player.getUniqueId()) || canAlwaysJoin(shell, player);
        }
    },
    NOBODY(
        Component.text("Nobody", NamedTextColor.RED),
        Material.IRON_DOOR,
        List.of(
            Component.text("Only the owner can enter this snail shell"),
            Constants.OP_NOTE
        )
    ) {
        @Override
        public boolean check(Shell<?> shell, Player player) {
            return canAlwaysJoin(shell, player);
        }
    };

    // must be a separate class, because used in enum constructors
    private static class Constants {
        private static final Component OP_NOTE = Component.text("Server operators can always enter this snail shell", NamedTextColor.GRAY);
    }

    private static boolean canAlwaysJoin(Shell<?> shell, Player player) {
        return player.isOp() || shell.getCreator().getUniqueId().equals(player.getUniqueId());
    }

    public final Component name;
    public final Material icon;
    public final List<Component> description;

    AccessMode(Component name, Material icon, List<Component> description) {
        this.name = name;
        this.icon = icon;
        this.description = description;
    }

    public abstract boolean check(Shell<?> shell, Player player);
}

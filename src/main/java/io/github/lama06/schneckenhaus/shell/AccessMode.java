package io.github.lama06.schneckenhaus.shell;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

import static io.github.lama06.schneckenhaus.language.Translator.t;

public enum AccessMode {
    // DON'T RENAME ENUM CONSTANTS, THEY ARE USED FOR SERIALIZATION
    EVERYBODY(
        Component.text(t("ui_access_control_everybody"), NamedTextColor.GREEN),
        Material.OAK_DOOR,
        List.of(Component.text(t("ui_access_control_everybody_description")))
    ) {
        @Override
        public boolean check(Shell<?> shell, Player player) {
            return true;
        }
    },
    BLACKLIST(
        Component.text(t("ui_access_control_blacklist"), NamedTextColor.RED),
        Material.WITHER_ROSE,
        List.of(
            Component.text(t("ui_access_control_blacklist_description")),
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
        Component.text(t("ui_access_control_whitelist")),
        Material.WHITE_TULIP,
        List.of(
            Component.text(t("ui_access_control_whitelist_description")),
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
        Component.text(t("ui_access_control_nobody"), NamedTextColor.RED),
        Material.IRON_DOOR,
        List.of(
            Component.text(t("ui_access_control_nobody_description")),
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
        private static final Component OP_NOTE = Component.text(t("ui_access_control_hint_op"), NamedTextColor.GRAY);
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

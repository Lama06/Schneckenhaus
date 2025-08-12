package io.github.lama06.schneckenhaus.shell.permission;

import io.github.lama06.schneckenhaus.language.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;

public enum ShellPermissionMode {
    // DON'T RENAME ENUM CONSTANTS, THEY ARE USED FOR SERIALIZATION
    EVERYBODY(
        Message.EVERYBODY.toComponent(NamedTextColor.GREEN),
        Material.OAK_DOOR
    ),
    BLACKLIST(
        Message.BLACKLIST.toComponent(NamedTextColor.RED),
        Material.WITHER_ROSE
    ),
    WHITELIST(
        Message.WHITELIST.toComponent(NamedTextColor.WHITE),
        Material.WHITE_TULIP
    ),
    NOBODY(
        Message.NOBODY.toComponent(NamedTextColor.RED),
        Material.IRON_DOOR
    );

    private final Component name;
    private final Material icon;

    ShellPermissionMode(Component name, Material icon) {
        this.name = name;
        this.icon = icon;
    }

    public Component getName() {
        return name;
    }

    public Material getIcon() {
        return icon;
    }
}

package io.github.lama06.schneckenhaus.shell.permission;

import io.github.lama06.schneckenhaus.language.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public enum ShellPermissionMode implements ComponentLike {
    // DON'T RENAME ENUM CONSTANTS, THEY ARE USED FOR SERIALIZATION
    EVERYBODY(
        Message.EVERYBODY,
        NamedTextColor.GREEN,
        Material.OAK_DOOR
    ),
    BLACKLIST(
        Message.BLACKLIST,
        NamedTextColor.RED,
        Material.WITHER_ROSE
    ),
    WHITELIST(
        Message.WHITELIST,
        NamedTextColor.WHITE,
        Material.WHITE_TULIP
    ),
    NOBODY(
        Message.NOBODY,
        NamedTextColor.RED,
        Material.IRON_DOOR
    );

    private final Message name;
    private final TextColor color;
    private final Material icon;

    ShellPermissionMode(Message name, TextColor color, Material icon) {
        this.name = name;
        this.color = color;
        this.icon = icon;
    }

    public Message getName() {
        return name;
    }

    public TextColor getColor() {
        return color;
    }

    public Material getIcon() {
        return icon;
    }

    @Override
    public @NotNull Component asComponent() {
        return name.asComponent(color);
    }
}

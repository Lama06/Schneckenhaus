package io.github.lama06.schneckenhaus;

import io.github.lama06.schneckenhaus.util.EnumPersistentDataType;
import io.github.lama06.schneckenhaus.util.UuidPersistentDataType;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

/**
 * Provides {@link NamespacedKey}s this plugin uses to store data in {@link PersistentDataContainer}s.
 */
public final class Data {
    /**
     * The snail shell id that will be assigned to the next snail shell.
     * Type: {@link PersistentDataType#INTEGER}.
     */
    public static final NamespacedKey WORLD_NEXT_ID = new NamespacedKey(SchneckenPlugin.INSTANCE, "next_id");

    /**
     * The snail shell id of a shulker box item.
     * Type: {@link PersistentDataType#INTEGER}.
     */
    public static final NamespacedKey SHULKER_ITEM_ID = new NamespacedKey(SchneckenPlugin.INSTANCE, "id");

    /**
     * The snail shell id of a shulker box block.
     * Type: {@link PersistentDataType#INTEGER}.
     */
    public static final NamespacedKey SHULKER_BLOCK_ID = new NamespacedKey(SchneckenPlugin.INSTANCE, "id");

    /**
     * The size of a snail shell.
     * Type: {@link PersistentDataType#INTEGER}.
     */
    public static final NamespacedKey SNAIL_SHELL_SIZE = new NamespacedKey(SchneckenPlugin.INSTANCE, "size");
    /**
     * The color of a snail shell.
     * Type: {@link EnumPersistentDataType#DYE_COLOR}.
     */
    public static final NamespacedKey SNAIL_SHELL_COLOR = new NamespacedKey(SchneckenPlugin.INSTANCE, "color");
    /**
     * The unique id of the player who created a snail shell.
     * Type: {@link UuidPersistentDataType#INSTANCE}.
     */
    public static final NamespacedKey SNAIL_SHELL_CREATOR = new NamespacedKey(SchneckenPlugin.INSTANCE, "creator");

    /**
     * When the player enters a snail shell, their location is stored.
     * When the player leaves the snail shell again, they will be teleported back to this location.
     * Type: {@link UuidPersistentDataType#INSTANCE}.
     */
    public static final NamespacedKey PLAYER_PREVIOUS_LOCATION = new NamespacedKey(SchneckenPlugin.INSTANCE, "previous_location");

    private Data() { }
}

package io.github.lama06.schneckenhaus.util;

import io.github.lama06.schneckenhaus.language.Message;
import org.bukkit.Material;

public enum WoodType {
    // Names used for serialization
    OAK(Material.OAK_LOG, Material.OAK_PLANKS, Material.OAK_SAPLING, Message.OAK),
    SPRUCE(Material.SPRUCE_LOG, Material.SPRUCE_PLANKS, Material.SPRUCE_SAPLING, Message.SPRUCE),
    BIRCH(Material.BIRCH_LOG, Material.BIRCH_PLANKS, Material.BIRCH_SAPLING, Message.BIRCH),
    JUNGLE(Material.JUNGLE_LOG, Material.JUNGLE_PLANKS, Material.JUNGLE_SAPLING, Message.JUNGLE),
    ACACIA(Material.ACACIA_LOG, Material.ACACIA_PLANKS, Material.ACACIA_SAPLING, Message.ACACIA),
    DARK_OAK(Material.DARK_OAK_LOG, Material.DARK_OAK_PLANKS, Material.DARK_OAK_SAPLING, Message.DARK_OAK),
    MANGROVE(Material.MANGROVE_LOG, Material.MANGROVE_PLANKS, Material.MANGROVE_PROPAGULE, Message.MANGROVE),
    CHERRY(Material.CHERRY_LOG, Material.CHERRY_PLANKS, Material.CHERRY_SAPLING, Message.CHERRY),
    PALE_OAK(Material.PALE_OAK_LOG, Material.PALE_OAK_PLANKS, Material.PALE_OAK_SAPLING, Message.PALE_OAK),
    CRIMSON(Material.CRIMSON_STEM, Material.CRIMSON_PLANKS, Material.CRIMSON_FUNGUS, Message.CRIMSON),
    WARPED(Material.WARPED_STEM, Material.WARPED_PLANKS, Material.WARPED_FUNGUS, Message.WARPED);

    private final Material log;
    private final Material planks;
    private final Material sapling;
    private final Message message;

    WoodType(Material log, Material planks, Material sapling, Message message) {
        this.log = log;
        this.planks = planks;
        this.sapling = sapling;
        this.message = message;
    }

    public Material getLog() {
        return log;
    }

    public Material getPlanks() {
        return planks;
    }

    public Material getSapling() {
        return sapling;
    }

    public Message getMessage() {
        return message;
    }
}

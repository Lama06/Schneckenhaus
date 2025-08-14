package io.github.lama06.schneckenhaus.util;

import org.bukkit.Material;

public enum WoodType {
    // Names used for serialization
    OAK(Material.OAK_PLANKS, Material.OAK_SAPLING),
    SPRUCE(Material.SPRUCE_PLANKS, Material.SPRUCE_SAPLING),
    BIRCH(Material.BIRCH_PLANKS, Material.BAMBOO_SAPLING),
    JUNGLE(Material.JUNGLE_PLANKS, Material.JUNGLE_SAPLING),
    ACACIA(Material.ACACIA_PLANKS, Material.ACACIA_SAPLING),
    DARK_OAK(Material.DARK_OAK_PLANKS, Material.DARK_OAK_SAPLING),
    MANGROVE(Material.MANGROVE_PLANKS, Material.MANGROVE_PROPAGULE),
    CHERRY(Material.CHERRY_PLANKS, Material.CHERRY_SAPLING),
    PALE_OAK(Material.PALE_OAK_PLANKS, Material.PALE_OAK_SAPLING),
    CRIMSON(Material.CRIMSON_PLANKS, Material.CRIMSON_FUNGUS),
    WARPED(Material.WARPED_PLANKS, Material.WARPED_FUNGUS);

    private final Material planks;
    private final Material sapling;

    WoodType(Material planks, Material sapling) {
        this.planks = planks;
        this.sapling = sapling;
    }

    public Material getPlanks() {
        return planks;
    }

    public Material getSapling() {
        return sapling;
    }
}

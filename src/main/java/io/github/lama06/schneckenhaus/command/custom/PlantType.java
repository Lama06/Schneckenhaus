package io.github.lama06.schneckenhaus.command.custom;

import org.bukkit.Material;

import java.util.Set;

public enum PlantType {
    DRIPSTONE(-1, Set.of(Material.POINTED_DRIPSTONE)),
    BAMBOO(1, Set.of(Material.BAMBOO)),
    TWISTING_VINES(1, Set.of(Material.TWISTING_VINES, Material.TWISTING_VINES_PLANT)),
    WEEPING_VINES(-1, Set.of(Material.WEEPING_VINES, Material.WEEPING_VINES_PLANT));

    public final int growDirection;
    public final Set <Material> materials;

    PlantType(int growDirection, Set<Material> materials) {
        this.growDirection = growDirection;
        this.materials = materials;
    }
}

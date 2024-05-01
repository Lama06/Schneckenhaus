package io.github.lama06.schneckenhaus.shell;

import org.bukkit.Material;

import java.util.List;

public record ShellRecipe<C extends ShellConfig>(String key, List<Material> ingredients, C config) { }

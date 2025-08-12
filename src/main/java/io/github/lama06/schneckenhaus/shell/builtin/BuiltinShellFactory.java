package io.github.lama06.schneckenhaus.shell.builtin;

import io.github.lama06.schneckenhaus.config.ItemConfig;
import io.github.lama06.schneckenhaus.recipe.CraftingInput;
import io.github.lama06.schneckenhaus.shell.ShellBuilder;
import io.github.lama06.schneckenhaus.shell.ShellFactory;

public abstract class BuiltinShellFactory extends ShellFactory {
    public abstract GlobalBuiltinShellConfig getGlobalConfig();

    public boolean getCraftingResult(ShellBuilder builder, CraftingInput input) {
        GlobalBuiltinShellConfig config = getGlobalConfig();
        if (!config.isCrafting()) {
            return false;
        }
        for (ItemConfig item : config.getIngredients()) {
            if (!input.remove(item)) {
                return false;
            }
        }
        return true;
    }
}

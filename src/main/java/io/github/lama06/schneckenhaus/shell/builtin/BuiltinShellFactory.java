package io.github.lama06.schneckenhaus.shell.builtin;

import io.github.lama06.schneckenhaus.config.ItemConfig;
import io.github.lama06.schneckenhaus.util.CraftingInput;
import io.github.lama06.schneckenhaus.shell.ShellBuilder;
import io.github.lama06.schneckenhaus.shell.ShellFactory;

public abstract class BuiltinShellFactory extends ShellFactory {
    public abstract BuiltinShellConfig getConfig();

    public boolean getCraftingResult(ShellBuilder builder, CraftingInput input) {
        BuiltinShellConfig config = getConfig();
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

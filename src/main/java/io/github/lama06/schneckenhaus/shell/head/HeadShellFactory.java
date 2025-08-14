package io.github.lama06.schneckenhaus.shell.head;

import io.github.lama06.schneckenhaus.recipe.CraftingInput;
import io.github.lama06.schneckenhaus.shell.ShellBuilder;
import io.github.lama06.schneckenhaus.shell.ShellData;
import io.github.lama06.schneckenhaus.shell.builtin.BuiltinShellFactory;
import io.github.lama06.schneckenhaus.shell.builtin.GlobalBuiltinShellConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public final class HeadShellFactory extends BuiltinShellFactory {
    public static final HeadShellFactory INSTANCE = new HeadShellFactory();


    @Override
    public GlobalBuiltinShellConfig getGlobalConfig() {
        return config.getHead();
    }

    @Override
    public String getName() {
        return "head";
    }

    @Override
    public ShellBuilder newBuilder() {
        return new HeadShellBuilder();
    }

    @Override
    public boolean getCraftingResult(ShellBuilder builder, CraftingInput input) {
        if (!super.getCraftingResult(builder, input)) {
            return false;
        }
        HeadShellBuilder headBuilder = (HeadShellBuilder) builder;
        headBuilder.setHeadOwner(builder.getCreator());
        return true;
    }

    @Override
    protected Material getItemType(ShellData data) {
        return Material.PLAYER_HEAD;
    }

    @Override
    public ItemStack createItem(ShellData data) {
        ItemStack item = super.createItem(data);
        item.editMeta(SkullMeta.class, meta -> {
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(data.getCreator()));
        });
        return item;
    }

    @Override
    public HeadShell loadShell(int id) {
        HeadShell shell = new HeadShell(id);
        if (!shell.load()) {
            return null;
        }
        return shell;
    }
}

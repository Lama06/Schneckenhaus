package io.github.lama06.schneckenhaus.shell.chest;

import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.ui.InventoryPosition;
import io.github.lama06.schneckenhaus.ui.Screen;
import io.github.lama06.schneckenhaus.util.WoodType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class ChestShellWoodScreen extends Screen {
    private final ChestShell shell;

    public ChestShellWoodScreen(ChestShell shell, Player player) {
        super(player);
        this.shell = shell;
    }

    @Override
    protected Component getTitle() {
        return Message.WOOD.asComponent(NamedTextColor.YELLOW);
    }

    @Override
    protected int getHeight() {
        return 2;
    }

    @Override
    protected void draw() {
        int slot = 0;
        for (WoodType wood : WoodType.values()) {
            boolean selected = wood == shell.getWood();
            ItemStack item = new ItemStack(wood.getSapling());
            item.editMeta(meta -> {
                meta.customName(wood.getMessage().asComponent(selected ? NamedTextColor.GREEN : NamedTextColor.WHITE));
                meta.lore(List.of(Message.getSelectedOrClickToSelect(selected).asComponent(NamedTextColor.YELLOW)));
            });
            setItem(InventoryPosition.fromSlot(slot++), item, () -> {
                shell.setWood(wood);
                redraw();
            });
        }
    }
}

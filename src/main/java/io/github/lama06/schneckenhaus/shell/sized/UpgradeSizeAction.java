package io.github.lama06.schneckenhaus.shell.sized;

import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.shell.action.ShellScreenAction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

final class UpgradeSizeAction extends ShellScreenAction {
    private static final int ANIMATION_DELAY = 20;

    private static final List<Material> ICONS = List.of(
        Material.SMALL_AMETHYST_BUD,
        Material.MEDIUM_AMETHYST_BUD,
        Material.LARGE_AMETHYST_BUD,
        Material.AMETHYST_CLUSTER
    );

    private final SizedShell shell;
    private int newSize;

    UpgradeSizeAction(SizedShell shell, Player player) {
        super(shell, player);
        this.shell = shell;
    }

    @Override
    public ItemStack getItem() {
        if (!Permission.UPGRADE_SNAIL_SHELL_SIZE.check(player)) {
            return null;
        }

        SizedShellConfig config = shell.getFactory().getConfig();

        int maxSize = Math.min(shell.getFactory().getMaxSize(), config.getMaxUpgradeSize());
        if (shell.getSize() >= maxSize) {
            return null;
        }
        newSize = Math.min(maxSize, shell.getSize() + config.getSizePerUpgradeIngredient());

        boolean canAfford = config.getUpgradeIngredient().canRemoveFrom(player.getInventory());

        Material icon = ICONS.get((Bukkit.getCurrentTick() / ANIMATION_DELAY) % ICONS.size());
        ItemStack item = new ItemStack(icon);
        item.editMeta(meta -> {
            meta.customName(Message.SIZE_UPGRADE.asComponent(NamedTextColor.YELLOW));
            meta.lore(List.of(
                Message.CURRENT_SIZE.asComponent().append(Component.text(": " + shell.getSize())),
                Message.SIZE_AFTER_UPGRADE.asComponent().append(Component.text(": " + newSize)),
                Message.COST.asComponent(canAfford ? NamedTextColor.GREEN : NamedTextColor.RED)
                    .append(Component.text(": "))
                    .append(config.getUpgradeIngredient())
            ));
        });

        return item;
    }

    @Override
    public Integer getItemAnimationDelay() {
        return ANIMATION_DELAY;
    }

    @Override
    public void onClick() {
        if (!shell.getFactory().getConfig().getUpgradeIngredient().removeFrom(player.getInventory())) {
            player.sendMessage(Message.ERROR_NOT_AFFORDABLE.asComponent(NamedTextColor.RED));
            return;
        }
        shell.setSize(newSize);
        player.sendMessage(Message.SIZE_UPGRADE_SUCCESS.asComponent(NamedTextColor.GREEN));
    }
}

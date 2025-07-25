package io.github.lama06.schneckenhaus.screen;

import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.shell.Shell;
import io.github.lama06.schneckenhaus.shell.shulker.ShulkerShell;
import io.github.lama06.schneckenhaus.util.EnumUtil;
import io.github.lama06.schneckenhaus.util.InventoryUtil;
import io.github.lama06.schneckenhaus.util.MaterialUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class ShellScreen extends Screen {
    private final Shell<?> shell;
    private BukkitTask changeColorTask;

    public ShellScreen(Shell<?> shell, Player player) {
        super(player);
        this.shell = shell;
    }

    @Override
    protected Component getTitle() {
        return Component.text("Snail Shell", NamedTextColor.YELLOW);
    }

    @Override
    protected int getHeight() {
        return 3;
    }

    @Override
    protected void draw() {
        InventoryUtil.fillMargin(inventory);

        int x = 1;


        // Shell
        ItemStack shellItem = shell.createItem();
        shellItem.editMeta(meta -> {
            List<Component> lore = new ArrayList<>(meta.lore());
            lore.add(Component.text("Click to add to your inventory", NamedTextColor.YELLOW));
            meta.lore(lore);
        });
        setItem(x++, 1, shellItem, () -> {
            player.give(shell.createItem());
        });


        // Owner
        ItemStack ownerItem = new ItemStack(Material.PLAYER_HEAD);
        ownerItem.editMeta(SkullMeta.class, meta -> {
            meta.displayName(Component.text("Owner: " + shell.getCreator().getName(), NamedTextColor.WHITE));
            meta.lore(List.of(
                Component.text("UUID: " + shell.getCreator().getUniqueId(), NamedTextColor.DARK_GRAY),
                Component.text("Click to transfer ownership", NamedTextColor.YELLOW)
            ));
            meta.setOwningPlayer(shell.getCreator());
        });
        setItem(x++, 1, ownerItem, () -> {
            InputScreen.openPlayerNameInput(
                player,
                shell.getCreator().getName(),
                newOwner -> {
                    new ConfirmationScreen(
                        player,
                        "Transfer Ownership",
                        () -> {},
                        () -> {
                            Shell.CREATOR.set(shell, newOwner.getUniqueId());
                            player.sendMessage(Component.text("Ownership successfully transferred", NamedTextColor.GREEN));
                        }
                    ).open();
                }
            );
        });


        // Access control
        ItemStack accessControl = new ItemStack(Material.OAK_DOOR);
        accessControl.editMeta(meta -> {
            meta.displayName(Component.text("Access Control"));
            meta.lore(List.of(
                Component.text("Manage your whitelist / blacklist"),
                Component.text("Click to open", NamedTextColor.YELLOW)
            ));
        });
        setItem(x++, 1, accessControl, () -> new AccessControlScreen(player, shell).open());


        // Change color
        if (shell instanceof ShulkerShell shulkerShell) {
            int changeColorX = x++;
            DyeColor[] colors = DyeColor.values();
            DyeColor[] currentItemColor = {colors[0]};

            changeColorTask = Bukkit.getScheduler().runTaskTimer(
                SchneckenPlugin.INSTANCE,
                () -> {
                    ItemStack glass = new ItemStack(MaterialUtil.getColoredGlassPane(currentItemColor[0]));
                    glass.editMeta(meta -> {
                        meta.displayName(Component.text("Color: " + EnumUtil.beautifyName(shulkerShell.getColor()))
                            .color(TextColor.color(shulkerShell.getColor().getColor().asRGB())));
                        meta.lore(List.of(
                            Component.text("Click to change", NamedTextColor.YELLOW)
                        ));
                    });
                    setItem(changeColorX, 1, glass, () -> new ChangeShellColorScreen(player, shulkerShell).open());
                    if (currentItemColor[0].ordinal() == colors.length - 1) {
                        currentItemColor[0] = colors[0];
                    } else {
                        currentItemColor[0] = EnumUtil.getNext(currentItemColor[0]);
                    }
                },
                0, 10
            );
        }
    }

    @Override
    protected void onClose() {
        if (changeColorTask != null) {
            changeColorTask.cancel();
        }
    }
}

package io.github.lama06.schneckenhaus.screen;

import io.github.lama06.schneckenhaus.Permissions;
import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.shell.Shell;
import io.github.lama06.schneckenhaus.shell.builtin.BuiltinShell;
import io.github.lama06.schneckenhaus.shell.builtin.BuiltinShellConfig;
import io.github.lama06.schneckenhaus.shell.builtin.BuiltinShellFactory;
import io.github.lama06.schneckenhaus.shell.builtin.BuiltinShellGlobalConfig;
import io.github.lama06.schneckenhaus.shell.chest.ChestShell;
import io.github.lama06.schneckenhaus.shell.chest.ChestShellFactory;
import io.github.lama06.schneckenhaus.shell.shulker.ShulkerShell;
import io.github.lama06.schneckenhaus.shell.shulker.ShulkerShellFactory;
import io.github.lama06.schneckenhaus.util.EnumUtil;
import io.github.lama06.schneckenhaus.util.InventoryUtil;
import io.github.lama06.schneckenhaus.util.MaterialUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.github.lama06.schneckenhaus.language.Translator.t;

public class ShellScreen extends Screen {
    private Shell<?> shell;
    private BukkitTask changeColorTask;

    public ShellScreen(Shell<?> shell, Player player) {
        super(player);
        this.shell = shell;
    }

    @Override
    protected Component getTitle() {
        return Component.text(t("snail_shell"), NamedTextColor.YELLOW);
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
        if (player.hasPermission(Permissions.CREATE_SHELL_COPIES)) {
            shellItem.editMeta(meta -> {
                List<Component> lore = new ArrayList<>(meta.hasLore() ? meta.lore() : List.of());
                lore.add(Component.text(t("ui_shell_add_to_inventory"), NamedTextColor.YELLOW));
                meta.lore(lore);
            });
        }
        setItem(x++, 1, shellItem, () -> {
            if (player.hasPermission(Permissions.CREATE_SHELL_COPIES)) {
                player.give(shell.createItem());
            }
        });


        addTransferOwnershipButton(x++);


        // Access control
        ItemStack accessControl = new ItemStack(Material.OAK_DOOR);
        accessControl.editMeta(meta -> {
            meta.customName(Component.text(t("ui_access_control_title")));
            meta.lore(List.of(
                Component.text(t("ui_access_control_description")),
                Component.text(t("ui_access_control_click_action"), NamedTextColor.YELLOW)
            ));
        });
        setItem(x++, 1, accessControl, () -> new AccessControlScreen(player, shell).open());

        if (addChangeSizeButton(x)) {
            x++;
        }

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
                        meta.displayName(Component.text(t("ui_shell_color") + t(shulkerShell.getColor()))
                            .color(TextColor.color(shulkerShell.getColor().getColor().asRGB())));
                        meta.lore(List.of(
                            Component.text(t("ui_click_to_change"), NamedTextColor.YELLOW)
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

    private void addTransferOwnershipButton(int x) {
        ItemStack ownerItem = new ItemStack(Material.PLAYER_HEAD);
        ownerItem.editMeta(SkullMeta.class, meta -> {
            meta.displayName(Component.text(t("ui_shell_owner") + shell.getCreator().getName(), NamedTextColor.WHITE));
            meta.lore(List.of(
                Component.text(t("ui_shell_owner_transfer"), NamedTextColor.YELLOW)
            ));
            meta.setOwningPlayer(shell.getCreator());
        });
        setItem(x, 1, ownerItem, () -> {
            if (!Permissions.require(player, "schneckenhaus.transfer_ownership")) {
                return;
            }

            InputScreen.openPlayerNameInput(
                player,
                shell.getCreator().getName(),
                newOwner -> {
                    new ConfirmationScreen(
                        player,
                        t("ui_transfer_ownership_title"),
                        () -> {},
                        () -> {
                            Shell.CREATOR.set(shell, newOwner.getUniqueId());
                            player.sendMessage(Component.text(t("ui_transfer_ownership_success"), NamedTextColor.GREEN));
                        }
                    ).open();
                }
            );
        });
    }

    private boolean addChangeSizeButton(int x) {
        if (!(shell instanceof BuiltinShell<?> builtinShell)) {
            return false;
        }
        BuiltinShellFactory<?> factory = switch (builtinShell) {
            case ShulkerShell shulkerShell -> ShulkerShellFactory.INSTANCE;
            case ChestShell chestShell -> ChestShellFactory.INSTANCE;
        };
        if (builtinShell.getSize() >= factory.getMaxSize()) {
            return false;
        }
        BuiltinShellGlobalConfig config = factory.getGlobalConfig();
        boolean hasSizeIngredient = player.getInventory().contains(config.sizeIngredient);
        int newSize = Math.min(factory.getMaxSize(), builtinShell.getSize() + config.sizePerIngredient);

        ItemStack changeSizeItem = new ItemStack(Material.SLIME_BALL);
        changeSizeItem.editMeta(meta -> {
            meta.customName(Component.text(t("ui_increase_size_button_label") + newSize));
            meta.lore(List.of(
                Component.text(t("ui_increase_size_ingredient"))
                    .color(hasSizeIngredient ? NamedTextColor.GREEN : NamedTextColor.RED)
                    .append(Component.translatable(config.sizeIngredient)),
                hasSizeIngredient ?
                    Component.text(t("ui_increase_size_click_action"), NamedTextColor.YELLOW) :
                    Component.text(t("ui_increase_size_ingredient_error"), NamedTextColor.RED)
            ));
        });
        setItem(x, 1, changeSizeItem, () -> {
            if (!hasSizeIngredient) {
                return;
            }
            Map<Integer, ItemStack> notRemoved = player.getInventory().removeItem(new ItemStack(config.sizeIngredient, 1));
            if (!notRemoved.isEmpty()) {
                return;
            }

            Map<Block, BlockData> blocksBefore = shell.getBlocks();
            BuiltinShellConfig.SIZE.set(shell, newSize);
            shell = SchneckenPlugin.INSTANCE.getWorld().getShell(builtinShell.getPosition()); // re-get shell because size changed
            Map<Block, BlockData> blocksAfter = shell.getBlocks();
            shell.place();
            for (Block blockBefore : blocksBefore.keySet()) {
                if (blocksAfter.containsKey(blockBefore)) {
                    continue;
                }
                blockBefore.setType(Material.AIR);
            }

            onClose(); // stop color change task
            redraw();
        });
        return true;
    }

    @Override
    protected void onClose() {
        if (changeColorTask != null) {
            changeColorTask.cancel();
        }
    }
}

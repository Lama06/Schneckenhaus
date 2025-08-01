package io.github.lama06.schneckenhaus.systems;

import io.github.lama06.schneckenhaus.Permissions;
import io.github.lama06.schneckenhaus.SchneckenPlugin;
import io.github.lama06.schneckenhaus.position.IdGridPosition;
import io.github.lama06.schneckenhaus.shell.Shell;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.Collection;

import static io.github.lama06.schneckenhaus.language.Translator.t;

public final class BreakShellSystem implements Listener {
    @EventHandler(priority = EventPriority.LOW) // call before #dropShellForPlayerInCreativeMode
    private void preventTheft(final BlockBreakEvent event) {
        if (!SchneckenPlugin.INSTANCE.getSchneckenConfig().theftPrevention) {
            return;
        }
        if (event.getPlayer().hasPermission(Permissions.BYPASS_THEFT_PREVENTION)) {
            return;
        }
        if (!(event.getBlock().getState() instanceof final TileState tileState)) {
            return;
        }
        Integer id = Shell.BLOCK_ID.get(tileState);
        if (id == null) {
            return;
        }
        Shell<?> shell = SchneckenPlugin.INSTANCE.getWorld().getShell(new IdGridPosition(id));
        if (shell == null) {
            return;
        }
        if (event.getPlayer().getUniqueId().equals(shell.getCreator().getUniqueId())) {
            return;
        }
        event.setCancelled(true);
        event.getPlayer().sendMessage(Component.text(t("snail_shell_break_disallowed"), NamedTextColor.RED));
    }

    @EventHandler
    private void preserveShellIdWhenBroken(final BlockDropItemEvent event) {
        if (!(event.getBlockState() instanceof final TileState tileState)) {
            return;
        }
        final Integer id = Shell.BLOCK_ID.get(tileState);
        if (id == null) {
            return;
        }
        if (event.getItems().size() != 1) {
            return;
        }
        final ItemStack item = event.getItems().get(0).getItemStack();
        final ItemMeta meta = item.getItemMeta();
        final PersistentDataContainer itemData = meta.getPersistentDataContainer();
        Shell.ITEM_ID.set(itemData, id);
        item.setItemMeta(meta);
    }

    @EventHandler(ignoreCancelled = true)
    public void dropShellForPlayerInCreativeMode(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            return;
        }

        Block block = event.getBlock();
        if (!(block.getState() instanceof TileState tileState)) {
            return;
        }
        Integer id = Shell.BLOCK_ID.get(tileState);
        if (id == null) {
            return;
        }
        event.setDropItems(false);

        Collection<ItemStack> drops = block.getDrops();
        if (drops.size() != 1) {
            return;
        }
        ItemStack item = drops.iterator().next();
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer itemData = meta.getPersistentDataContainer();
        Shell.ITEM_ID.set(itemData, id);
        item.setItemMeta(meta);

        block.getWorld().dropItemNaturally(block.getLocation(), item);
    }
}

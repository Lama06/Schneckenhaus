package io.github.lama06.schneckenhaus.shell.custom;

import io.github.lama06.schneckenhaus.config.ConfigUtil;
import io.github.lama06.schneckenhaus.config.ItemConfig;
import io.github.lama06.schneckenhaus.util.BlockArea;
import io.github.lama06.schneckenhaus.util.BlockPosition;
import org.bukkit.*;

import java.util.*;
import java.util.stream.Collectors;

public final class CustomShellConfig {
    public static CustomShellConfig deserialize(Map<?, ?> config) {
        CustomShellConfig result = new CustomShellConfig();

        if (config.get("item") instanceof String item) {
            result.item = Objects.requireNonNullElse(Registry.MATERIAL.get(NamespacedKey.fromString(item)), result.item);
        }

        if (config.get("crafting") instanceof Boolean crafting) {
            result.crafting = crafting;
        }

        if (config.get("ingredients") instanceof List<?> ingredients) {
            ingredients.stream().map(ItemConfig::parse).filter(Objects::nonNull).forEach(result.ingredients::add);
        }

        if (!(config.get("template_world") instanceof String templateWorld)) {
            return null;
        }
        result.templateWorld = templateWorld;

        if (!(config.get("template_position") instanceof String templatePosition)) {
            return null;
        }
        result.templatePosition = BlockArea.fromString(templatePosition);
        if (result.templatePosition == null) {
            return null;
        }

        if (config.get("initial_blocks") instanceof List<?> initialBlocks) {
            initialBlocks.stream()
                .filter(string -> string instanceof String).map(string -> (String) string)
                .map(BlockPosition::fromString).filter(Objects::nonNull)
                .forEach(result.initialBlocks::add);;
        }

        if (config.get("alternative_blocks") instanceof Map<?, ?> alternativeBlocksConfig) {
            for (Object positionConfig : alternativeBlocksConfig.keySet()) {
                if (!(positionConfig instanceof String positionConfigString)) {
                    continue;
                }
                BlockPosition position = BlockPosition.fromString(positionConfigString);
                if (position == null) {
                    continue;
                }
                if (!(alternativeBlocksConfig.get(positionConfig) instanceof List<?> materialsConfig)) {
                    continue;
                }
                Set<Material> materials = new HashSet<>();
                for (Object materialConfig : materialsConfig) {
                    if (!(materialConfig instanceof String materialConfigString)) {
                        continue;
                    }
                    NamespacedKey materialKey = NamespacedKey.fromString(materialConfigString);
                    if (materialKey == null) {
                        continue;
                    }
                    Material material = Registry.MATERIAL.get(materialKey);
                    if (material == null) {
                        continue;
                    }
                    materials.add(material);
                }
                if (!materials.isEmpty()) {
                    result.alternativeBlocks.put(position, materials);
                }
            }
        }

        if (config.get("spawn_position") instanceof Map<?, ?> spawnPosition) {
            result.spawnPosition = ConfigUtil.deserializeLocation(spawnPosition);
        }

        if (config.get("exit_blocks") instanceof List<?> exitBlocks) {
            exitBlocks.stream()
                .filter(string -> string instanceof String).map(string -> (String) string)
                .map(BlockPosition::fromString).filter(Objects::nonNull)
                .forEach(result.exitBlocks::add);
        }

        if (config.get("menu_block") instanceof String menuBlock) {
            result.menuBlock = BlockPosition.fromString(menuBlock);
        }

        return result;
    }

    private Material item = Material.CHEST;

    private boolean crafting = true;
    private final List<ItemConfig> ingredients = new ArrayList<>();

    private String templateWorld;
    private BlockArea templatePosition;
    private final Set<BlockPosition> initialBlocks = new HashSet<>();
    private final Map<BlockPosition, Set<Material>> alternativeBlocks = new HashMap<>();

    private Location spawnPosition;
    private final Set<BlockPosition> exitBlocks = new HashSet<>();
    private BlockPosition menuBlock;

    public Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("item", item.getKey().toString());
        result.put("crafting", crafting);
        result.put("ingredients", ingredients.stream().map(ItemConfig::serialize).toList());
        result.put("template_world", templateWorld);
        result.put("template_position", templatePosition.toString());
        result.put("initial_blocks", initialBlocks.stream().map(BlockPosition::toString).toList());
        result.put("alternative_blocks", alternativeBlocks.keySet().stream().collect(Collectors.toMap(
            BlockPosition::toString,
            pos -> alternativeBlocks.get(pos).stream().map(block -> block.getKey().toString()).toList()
        )));
        result.put("spawn_position", ConfigUtil.serializeLocation(spawnPosition, false));
        result.put("exit_blocks", exitBlocks.stream().map(BlockPosition::toString).toList());
        result.put("menu_block", menuBlock == null ? null : menuBlock.toString());
        return result;
    }

    public Material getItem() {
        return item;
    }

    public void setItem(Material item) {
        this.item = item;
    }

    public boolean isCrafting() {
        return crafting;
    }

    public void setCrafting(boolean crafting) {
        this.crafting = crafting;
    }

    public List<ItemConfig> getIngredients() {
        return ingredients;
    }

    public String getTemplateWorld() {
        return templateWorld;
    }

    public void setTemplateWorld(String templateWorld) {
        this.templateWorld = templateWorld;
    }

    public BlockArea getTemplatePosition() {
        return templatePosition;
    }

    public void setTemplatePosition(BlockArea templatePosition) {
        this.templatePosition = templatePosition;
    }

    public Set<BlockPosition> getInitialBlocks() {
        return initialBlocks;
    }

    public Map<BlockPosition, Set<Material>> getAlternativeBlocks() {
        return alternativeBlocks;
    }

    public Location getSpawnPosition() {
        return spawnPosition;
    }

    public void setSpawnPosition(Location spawnPosition) {
        this.spawnPosition = spawnPosition;
    }

    public Set<BlockPosition> getExitBlocks() {
        return exitBlocks;
    }

    public BlockPosition getMenuBlock() {
        return menuBlock;
    }

    public void setMenuBlock(BlockPosition menuBlock) {
        this.menuBlock = menuBlock;
    }
}

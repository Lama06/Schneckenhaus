package io.github.lama06.schneckenhaus.command.select;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.google.common.collect.Range;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import io.github.lama06.schneckenhaus.Permission;
import io.github.lama06.schneckenhaus.command.argument.EnumArgumentType;
import io.github.lama06.schneckenhaus.command.argument.ShellTypeArgumentType;
import io.github.lama06.schneckenhaus.command.parameter.ParameterCommand;
import io.github.lama06.schneckenhaus.language.Message;
import io.github.lama06.schneckenhaus.shell.ShellCreationType;
import io.github.lama06.schneckenhaus.shell.ShellFactory;
import io.github.lama06.schneckenhaus.shell.permission.ShellPermissionMode;
import io.github.lama06.schneckenhaus.util.ConstantsHolder;
import io.github.lama06.schneckenhaus.util.WoodType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.range.IntegerRangeProvider;
import io.papermc.paper.command.brigadier.argument.resolvers.PlayerProfileListResolver;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.DyeColor;
import org.bukkit.World;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public final class SelectCommand extends ConstantsHolder {
    private static final String SQL = """
        WITH params AS (
            SELECT
                -- shells table
                ? AS id_min,
                ? AS id_max,
        
                ? AS world,
                ? AS position_min,
                ? AS position_max,
        
                ? AS type,
        
                ? AS creation_type,
                ? AS creator,
                ? AS creation_time_min,
                ? AS creation_time_max,
        
                ? AS name,
        
                ? AS enter_permission_mode,
                ? AS build_permission_mode,
        
        
                -- sized shells table
                ? AS size_min,
                ? AS size_max,
        
        
                -- shulker shells table
                ? AS color,
                ? AS rainbow,
        
        
                -- chest shells table
                ? AS wood,
        
        
                -- custom shells table
                ? AS template,
        
        
                -- head shells table
                ? AS head,
        
        
                ? AS tag,
                ? AS owner,
        
                ? AS sort_criterion,
                ? AS sort_order,

                ? AS "limit",
                ? AS "offset"
        )
        SELECT
            shells.id,
            CASE params.sort_criterion
                WHEN 'id' THEN shells.id
                WHEN 'position' THEN shells.position
                WHEN 'creation_time' THEN shells.creation_time
                WHEN 'size' THEN sized_shells.size
                ELSE NULL
            END AS sort_criterion_value -- different name than params.sort_criterion, otherwise buggy ambiguity in ORDER BY clause
                                        -- even the SQL Discord server was confused about this 15.08.2025
        FROM params JOIN shells
            LEFT JOIN sized_shells ON shells.id = sized_shells.id
            LEFT JOIN shulker_shells ON shells.id = shulker_shells.id
            LEFT JOIN chest_shells ON shells.id = chest_shells.id
            LEFT JOIN custom_shells ON shells.id = custom_shells.id
            LEFT JOIN head_shells ON shells.id = head_shells.id
        WHERE
            (params.id_min IS NULL OR shells.id >= params.id_min) AND
            (params.id_max IS NULL OR shells.id <= params.id_max) AND
            (params.world IS NULL OR shells.world = params.world) AND
            (params.position_min IS NULL OR shells.position >= params.position_min) AND
            (params.position_max IS NULL OR shells.position <= params.position_max) AND
            (params.type IS NULL OR shells.type = params.type) AND
            (params.creation_type IS NULL OR shells.creation_type = params.creation_type) AND
            (params.creator IS NULL OR shells.creator = params.creator) AND
            (params.creation_time_min IS NULL OR shells.creation_time >= params.creation_time_min) AND
            (params.creation_time_max IS NULL OR shells.creation_time <= params.creation_time_max) AND
            (params.name IS NULL OR shells.name = params.name) AND
            (params.enter_permission_mode IS NULL OR shells.enter_permission_mode = params.enter_permission_mode) AND
            (params.build_permission_mode IS NULL OR shells.build_permission_mode = params.build_permission_mode) AND
        
            (params.size_min IS NULL OR sized_shells.size >= params.size_min) AND
            (params.size_max IS NULL OR sized_shells.size <= params.size_max) AND
        
            (params.color IS NULL OR shulker_shells.color = params.color) AND
            (params.rainbow IS NULL OR shulker_shells.rainbow = params.rainbow) AND
        
            (params.wood IS NULL OR chest_shells.wood = params.wood) AND
        
            (params.template IS NULL OR custom_shells.template = params.template) AND
        
            (params.head IS NULL OR head_shells.head_owner = params.head) AND
        
            (params.tag IS NULL OR EXISTS (
                SELECT 1
                FROM shell_tags
                WHERE id = shells.id AND tag = params.tag
            )) AND
        
            (params.owner IS NULL OR coalesce((
                SELECT shell_permissions.owner
                FROM shell_permissions
                WHERE shell_permissions.id = shells.id AND shell_permissions.player = params.owner
            ), FALSE))
        ORDER BY
            CASE WHEN params.sort_order = 'ascending' THEN sort_criterion_value ELSE 1 END ASC NULLS LAST,
            CASE WHEN params.sort_order = 'descending' THEN sort_criterion_value ELSE 1 END DESC NULLS LAST
        LIMIT coalesce((SELECT "limit" FROM params), -1)  -- -1 means no limit
        OFFSET coalesce((SELECT "offset" FROM params), 0)
        """;

    private PreparedStatement statement;

    public CommandNode<CommandSourceStack> create() {
        return ParameterCommand.builder("select")
            .requires(Permission.COMMAND_SELECT::check)
            .parameter("id", ArgumentTypes.integerRange())
            .parameter("world", ArgumentTypes.world())
            .parameter("position", ArgumentTypes.integerRange())
            .parameter("type", ShellTypeArgumentType.INSTANCE)
            .parameter(Commands.literal("creation")
                .then(Commands.literal("reason")
                    .then(Commands.argument("creationType", new EnumArgumentType<>(ShellCreationType.class)))
                )
                .then(Commands.literal("time")
                    .then(Commands.literal("after")
                        .then(Commands.argument("creationTimeMin", StringArgumentType.string()))
                    )
                    .then(Commands.literal("before")
                        .then(Commands.argument("creationTimeMax", StringArgumentType.string()))
                    )
                    .then(Commands.literal("between")
                        .then(Commands.argument("creationTimeMin", StringArgumentType.string())
                            .then(Commands.literal("and")
                                .then(Commands.argument("creationTimeMax", StringArgumentType.string()))
                            )
                        )
                    )
                )
            )
            .parameter("creator", ArgumentTypes.playerProfiles())
            .parameter("name", StringArgumentType.string())
            .parameter(Commands.literal("permission")
                .then(Commands.literal("enter")
                    .then(Commands.argument("enterPermissionMode", new EnumArgumentType<>(ShellPermissionMode.class)))
                )
                .then(Commands.literal("build")
                    .then(Commands.argument("buildPermissionMode", new EnumArgumentType<>(ShellPermissionMode.class)))
                )
            )
            .parameter("size", ArgumentTypes.integerRange())
            .parameter("color", new EnumArgumentType<>(DyeColor.class))
            .parameter("rainbow", BoolArgumentType.bool())
            .parameter("wood", new EnumArgumentType<>(WoodType.class))
            .parameter("template", StringArgumentType.string())
            .parameter("head", ArgumentTypes.playerProfiles())
            .parameter("tag", StringArgumentType.string())
            .parameter("owner", ArgumentTypes.playerProfiles())
            .parameter(Commands.literal("sort")
                .then(Commands.literal("by")
                    .then(Commands.argument("sortCriterion", new EnumArgumentType<>(SortCriterion.class))
                        .then(Commands.argument("sortOrder", new EnumArgumentType<>(SortOrder.class)))
                    )
                )
            )
            .parameter("limit", IntegerArgumentType.integer())
            .parameter("offset", IntegerArgumentType.integer(0))
            .parameter("combine", new EnumArgumentType<>(CombinationMode.class))
            .executes(this::execute)
            .build();
    }

    private int execute(CommandContext<CommandSourceStack> context, Map<String, Object> params) throws CommandSyntaxException {
        List<Integer> selection = new ArrayList<>();
        try {
            if (statement == null) {
                statement = connection.prepareStatement(SQL);
            }
            statement.clearParameters();

            int i = 1;

            setRange(params.get("id"), i);
            i += 2;

            if (params.get("world") instanceof World world) {
                statement.setString(i, world.getName());
            }
            i++;

            setRange(params.get("position"), i);
            i += 2;

            if (params.get("type") instanceof ShellFactory type) {
                statement.setString(i, type.getId());
            }
            i++;

            if (params.get("creationType") instanceof ShellCreationType creationType) {
                statement.setString(i, creationType.name().toLowerCase(Locale.ROOT));
            }
            i++;

            if (params.get("creator") instanceof PlayerProfileListResolver playerResolver) {
                Collection<PlayerProfile> players = playerResolver.resolve(context.getSource());
                statement.setString(i, players.iterator().next().getId().toString());
            }
            i++;

            if (params.get("creationTimeMin") instanceof String creationTimeMin) {
                statement.setString(i, creationTimeMin);
            }
            i++;
            if (params.get("creationTimeMax") instanceof String creationTimeMax) {
                statement.setString(i, creationTimeMax);
            }
            i++;

            if (params.get("name") instanceof String name) {
                statement.setString(i, name);
            }
            i++;

            if (params.get("enterPermissionMode") instanceof ShellPermissionMode enterPermissionMode) {
                statement.setString(i, enterPermissionMode.name().toLowerCase(Locale.ROOT));
            }
            i++;
            if (params.get("buildPermissionMode") instanceof ShellPermissionMode buildPermissionMode) {
                statement.setString(i, buildPermissionMode.name().toLowerCase(Locale.ROOT));
            }
            i++;

            setRange(params.get("size"), i);
            i += 2;

            if (params.get("color") instanceof DyeColor color) {
                statement.setString(i, color.name().toLowerCase(Locale.ROOT));
            }
            i++;

            if (params.get("rainbow") instanceof Boolean rainbow) {
                statement.setBoolean(i, rainbow);
            }
            i++;

            if (params.get("wood") instanceof WoodType wood) {
                statement.setString(i, wood.name().toLowerCase(Locale.ROOT));
            }
            i++;

            if (params.get("template") instanceof String template) {
                statement.setString(i, template);
            }
            i++;

            if (params.get("head") instanceof PlayerProfileListResolver playerResolver) {
                Collection<PlayerProfile> players = playerResolver.resolve(context.getSource());
                statement.setString(i, players.iterator().next().getId().toString());
            }
            i++;

            if (params.get("tag") instanceof String tag) {
                statement.setString(i, tag);
            }
            i++;

            if (params.get("owner") instanceof PlayerProfileListResolver playerResolver) {
                Collection<PlayerProfile> players = playerResolver.resolve(context.getSource());
                statement.setString(i, players.iterator().next().getId().toString());
            }
            i++;

            if (params.get("sortCriterion") instanceof SortCriterion sortCriterion) {
                statement.setString(i, sortCriterion.name().toLowerCase(Locale.ROOT));
            }
            i++;

            if (params.get("sortOrder") instanceof SortOrder sortOrder) {
                statement.setString(i, sortOrder.name().toLowerCase(Locale.ROOT));
            }
            i++;

            if (params.get("limit") instanceof Integer limit) {
                statement.setInt(i, limit);
            }
            i++;

            if (params.get("offset") instanceof Integer offset) {
                statement.setInt(i, offset);
            }

            if (statement.getParameterMetaData().getParameterCount() != i) {
                throw new IllegalStateException();
            }

            ResultSet result = statement.executeQuery();
            while (result.next()) {
                selection.add(result.getInt(1));
            }
        } catch (SQLException e) {
            logger.error("failed to select shells: {}", context.getInput(), e);
            return 0;
        }

        CombinationMode combinationMode = CombinationMode.REPLACE;
        if (params.get("combine") instanceof CombinationMode mode) {
            combinationMode = mode;
        }
        List<Integer> oldSelection = plugin.getCommand().getSelection(context.getSource());
        List<Integer> combinedSelection = combinationMode.combine(oldSelection, selection);

        context.getSource().getSender().sendMessage(Message.SELECT_SHELLS_SUCCESS.asComponent(NamedTextColor.GREEN, combinedSelection.size()));
        plugin.getCommand().setSelection(context.getSource(), combinedSelection);

        return selection.size();
    }

    private void setRange(Object param, int i) throws SQLException {
        if (param instanceof IntegerRangeProvider provider) {
            Range<Integer> range = provider.range();
            if (range.hasLowerBound()) {
                statement.setInt(i, range.lowerEndpoint());
            }
            if (range.hasUpperBound()) {
                statement.setInt(i + 1, range.upperEndpoint());
            }
        }
    }
}

package io.github.lama06.schneckenhaus.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.lama06.schneckenhaus.language.Message;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;

public final class CommandUtils {
    private static final SimpleCommandExceptionType NOT_PLAYER = new SimpleCommandExceptionType(Message.COMMAND_ERROR_NOT_PLAYER::toString);

    private CommandUtils() { }

    public static Player requirePlayer(CommandSourceStack source) throws CommandSyntaxException {
        if (!(source.getExecutor() instanceof Player player)) {
            throw NOT_PLAYER.create();
        }
        return player;
    }
}

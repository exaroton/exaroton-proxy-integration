package com.exaroton.proxy.commands;

import com.exaroton.api.server.Server;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Optional;
import java.util.Set;

/**
 * Abstract base class for platform-agnostic command source access.
 */
public abstract class CommandSourceAccessor {

    /**
     * Create a new CommandSourceAccessor.
     */
    public CommandSourceAccessor() {

    }

    /**
     * Checks if the source has the given permission.
     * @param permission The permission to check.
     * @return {@code true} if the source has the permission, {@code false} otherwise.
     */
    public abstract boolean hasPermission(String permission);

    /**
     * Get the name of the player who executed the command.
     * @return The name of the player who executed the command, if applicable.
     */
    public abstract Optional<String> getPlayerName();

    /**
     * Filter a list of player names to only include valid names.
     * @param playerNames The list of player names to filter.
     * @return A set of valid player names.
     */
    public abstract Set<String> filterPlayers(Set<String> playerNames);

    /**
     * Transfer a list of players to a server
     * @param server The server to transfer the players to.
     * @param playerNames Set of player names to transfer.
     */
    public abstract void transferPlayers(Server server, Set<String> playerNames);

    /**
     * Get an adventure audience for the command source.
     * @return An adventure audience for the command source.
     */
    protected abstract Audience getAudience();

    /**
     * Sends an error message to the source.
     * Error messages are typically red.
     * @param message The message to send.
     */
    public void sendFailure(Component message) {
        getAudience().sendMessage(message.color(NamedTextColor.RED));
    }

    /**
     * Sends a success message to the source.
     * The color of these messages should not be modified.
     * @param message The message to send.
     */
    public void sendSuccess(Component message) {
        getAudience().sendMessage(message);
    }
}

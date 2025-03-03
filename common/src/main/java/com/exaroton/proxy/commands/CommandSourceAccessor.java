package com.exaroton.proxy.commands;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.nio.file.Path;
import java.util.Collection;

/**
 * An interface for platform-agnostic command source access.
 */
public abstract class CommandSourceAccessor {
    /**
     * Checks if the source has the given permission.
     * @param permission The permission to check.
     * @return {@code true} if the source has the permission, {@code false} otherwise.
     */
    public abstract boolean hasPermission(Permission permission);

    /**
     * Get the directory of the Minecraft server/client.
     * @return the directory of the Minecraft server/client
     */
    public abstract Path getRootDirectory();

    /**
     * Get a collection of all log directories. These directories do not need to exist.
     * @return a collection of all directories that may contain logs.
     */
    public abstract Collection<LogDirectory> getLogDirectories();

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

    /**
     * Get the file name of the current log file
     * @return the file name of the current log file
     */
    public String getCurrentLogFileName() {
        return "latest.log";
    }
}

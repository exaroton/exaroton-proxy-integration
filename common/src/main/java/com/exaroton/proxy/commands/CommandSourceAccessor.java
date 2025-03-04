package com.exaroton.proxy.commands;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * An interface for platform-agnostic command source access.
 */
public abstract class CommandSourceAccessor {
    /**
     * Checks if the source has the given permission.
     * @param permission The permission to check.
     * @return {@code true} if the source has the permission, {@code false} otherwise.
     */
    public abstract boolean hasPermission(String permission);

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

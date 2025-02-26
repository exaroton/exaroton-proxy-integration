package com.exaroton.proxy.command;

import com.exaroton.proxy.commands.ICommandSourceAccessor;
import com.exaroton.proxy.components.AdventureComponent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * Adventure implementation of {@link ICommandSourceAccessor}.
 * Provides methods to send messages to the command source.
 */
public abstract class AdventureCommandSourceAccessor implements ICommandSourceAccessor<AdventureComponent> {
    @Override
    public final void sendFailure(AdventureComponent message) {
        getAudience().sendMessage(message.getBoxed().color(NamedTextColor.RED));
    }

    @Override
    public final void sendSuccess(AdventureComponent message, boolean allowLogging) {
        getAudience().sendMessage(message.getBoxed());
    }

    /**
     * Get an adventure audience for the command source.
     * @return An adventure audience for the command source.
     */
    protected abstract Audience getAudience();
}

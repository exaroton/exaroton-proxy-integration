package com.exaroton.proxy.commands;

import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.audience.Audience;

public class VelocityCommandSourceAccessor extends CommandSourceAccessor {
    /**
     * The original velocity command source
     */
    protected final CommandSource source;

    /**
     * Create a new velocity command source accessor.
     *
     * @param source The original velocity command source.
     */
    public VelocityCommandSourceAccessor(CommandSource source) {
        this.source = source;
    }

    @Override
    public boolean hasPermission(String permission) {
        return source.hasPermission(permission);
    }

    @Override
    protected Audience getAudience() {
        return source;
    }
}

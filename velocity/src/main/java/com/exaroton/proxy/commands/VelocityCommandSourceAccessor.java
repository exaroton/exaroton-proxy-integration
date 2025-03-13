package com.exaroton.proxy.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.audience.Audience;

import java.util.Optional;

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
    public Optional<String> getPlayerName() {
        if (source instanceof Player) {
            return Optional.of(((Player) source).getUsername());
        }
        return Optional.empty();
    }

    @Override
    protected Audience getAudience() {
        return source;
    }
}

package com.exaroton.proxy.commands;

import com.exaroton.api.server.Server;
import com.exaroton.proxy.VelocityPlugin;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.audience.Audience;

import java.util.Optional;
import java.util.Set;

public class VelocityCommandSourceAccessor extends CommandSourceAccessor {
    private final VelocityPlugin velocityPlugin;
    /**
     * The original velocity command source
     */
    protected final CommandSource source;

    /**
     * Create a new velocity command source accessor.
     *
     * @param source         The original velocity command source.
     * @param velocityPlugin The velocity plugin.
     */
    public VelocityCommandSourceAccessor(VelocityPlugin velocityPlugin, CommandSource source) {
        this.velocityPlugin = velocityPlugin;
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
    public void transferPlayers(Server server, Set<String> playerNames) {
        velocityPlugin.getProxyServerManager().transferPlayers(server, playerNames);
    }

    @Override
    protected Audience getAudience() {
        return source;
    }
}

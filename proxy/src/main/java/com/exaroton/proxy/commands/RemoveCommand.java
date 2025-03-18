package com.exaroton.proxy.commands;

import com.exaroton.api.server.Server;
import com.exaroton.api.server.ServerStatus;
import com.exaroton.proxy.CommonProxyPlugin;
import com.exaroton.proxy.Components;
import net.kyori.adventure.text.Component;

import java.util.Optional;
import java.util.Set;

public class RemoveCommand extends ServerCommand {
    /**
     * Create a new command
     *
     * @param plugin        The plugin
     */
    public RemoveCommand(CommonProxyPlugin plugin) {
        super(plugin, "remove");
    }

    @Override
    public void execute(CommandSourceAccessor source, Server server) {
        if (!plugin.getProxyServerManager().hasServer(server)) {
            source.sendFailure(Component.text("Server is not registered with this proxy."));
            return;
        }

        if (plugin.getProxyServerManager().removeServer(server)) {
            source.sendSuccess(Component.text("Removed server ")
                    .append(Components.addressText(server))
                    .append(Component.text(" from the proxy.")));
        } else {
            source.sendFailure(Component.text("Failed to remove server ")
                    .append(Components.addressText(server))
                    .append(Component.text(" from the proxy.")));
        }
    }

    @Override
    protected Optional<Set<ServerStatus>> getAllowableServerStatuses() {
        return Optional.empty();
    }
}

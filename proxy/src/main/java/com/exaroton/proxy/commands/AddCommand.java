package com.exaroton.proxy.commands;

import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.Server;
import com.exaroton.api.server.ServerStatus;
import com.exaroton.proxy.CommonProxyPlugin;
import com.exaroton.proxy.servers.proxy.ProxyServerManager;
import com.mojang.brigadier.context.CommandContext;
import net.kyori.adventure.text.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

public class AddCommand extends ServerCommand {
    protected final ProxyServerManager serverManager;

    /**
     * Create a new command
     *
     * @param plugin        The plugin
     * @param apiClient     The exaroton API client
     * @param serverManager The server manager of the proxy
     */
    public AddCommand(CommonProxyPlugin plugin,
                      ExarotonClient apiClient,
                      ProxyServerManager serverManager) {
        super(plugin, apiClient, "stop");
        this.serverManager = serverManager;
    }

    @Override
    protected <T> void execute(CommandContext<T> context,
                               BuildContext<T> buildContext,
                               Server server) {
        CommandSourceAccessor source = buildContext.mapSource(context.getSource());

        if (!server.hasStatus(ServerStatus.ONLINE)) {
            source.sendFailure(Component.text("Server has to be online before it can be added to your proxy"));
            return;
        }

        if (serverManager.hasServer(server)) {
            source.sendFailure(Component.text("Server is already in the proxy"));
            return;
        }

        if (serverManager.addServer(server)) {
            source.sendSuccess(Component.text("Server added to proxy"));
        } else {
            source.sendFailure(Component.text("Failed to add server to proxy"));
        }
    }

    @Override
    protected Optional<Set<ServerStatus>> getAllowableServerStatuses() {
        return Optional.of(Set.of(ServerStatus.ONLINE));
    }
}

package com.exaroton.proxy.commands;

import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.Server;
import com.exaroton.api.server.ServerStatus;
import com.exaroton.proxy.CommonProxyPlugin;
import com.exaroton.proxy.Components;
import com.exaroton.proxy.servers.proxy.ProxyServerManager;
import com.mojang.brigadier.context.CommandContext;
import net.kyori.adventure.text.Component;

import java.util.Optional;
import java.util.Set;

public class RemoveCommand extends ServerCommand {
    protected final ProxyServerManager serverManager;

    /**
     * Create a new command
     *
     * @param plugin        The plugin
     * @param apiClient     The exaroton API client
     * @param serverManager The server manager of the proxy
     */
    public RemoveCommand(CommonProxyPlugin plugin,
                         ExarotonClient apiClient,
                         ProxyServerManager serverManager) {
        super(plugin, apiClient, "remove");
        this.serverManager = serverManager;
    }

    @Override
    protected <T> void execute(CommandContext<T> context,
                               BuildContext<T> buildContext,
                               Server server) {
        CommandSourceAccessor source = buildContext.mapSource(context.getSource());

        if (!serverManager.hasServer(server)) {
            source.sendFailure(Component.text("Server is not registered with this proxy"));
            return;
        }

        if (serverManager.removeServer(server)) {
            source.sendSuccess(Component.text("Removed server ")
                    .append(Components.addressText(server))
                    .append(Component.text(" from the proxy")));
        } else {
            source.sendFailure(Component.text("Failed to remove server ")
                    .append(Components.addressText(server))
                    .append(Component.text(" from the proxy")));
        }
    }

    @Override
    protected Optional<Set<ServerStatus>> getAllowableServerStatuses() {
        return Optional.empty();
    }
}

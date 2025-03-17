package com.exaroton.proxy.commands;

import com.exaroton.api.server.Server;
import com.exaroton.api.server.ServerStatus;
import com.exaroton.proxy.CommonProxyPlugin;
import com.exaroton.proxy.Components;
import com.mojang.brigadier.context.CommandContext;

import java.util.Optional;
import java.util.Set;

/**
 * Command to add a server to the proxy
 */
public class AddCommand extends ServerCommand {

    /**
     * Create a new command
     *
     * @param plugin        The plugin
     */
    public AddCommand(CommonProxyPlugin plugin) {
        super(plugin, "add");
    }

    @Override
    protected <T> void execute(CommandContext<T> context,
                               BuildContext<T> buildContext,
                               Server server) {
        CommandSourceAccessor source = buildContext.mapSource(context.getSource());

        if (!server.hasStatus(ServerStatus.ONLINE)) {
            source.sendFailure(Components.incorrectStatus(server, Set.of(ServerStatus.ONLINE), "added to your proxy"));
            return;
        }

        plugin.getProxyServerManager().addServer(server, source);
    }

    @Override
    protected Optional<Set<ServerStatus>> getAllowableServerStatuses() {
        return Optional.of(Set.of(ServerStatus.ONLINE));
    }
}

package com.exaroton.proxy.commands;

import com.exaroton.api.server.Server;
import com.exaroton.api.server.ServerStatus;
import com.exaroton.proxy.CommonProxyPlugin;
import com.mojang.brigadier.context.CommandContext;
import net.kyori.adventure.text.Component;

import java.util.Optional;
import java.util.Set;

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
            source.sendFailure(Component.text("Server has to be online before it can be added to your proxy"));
            return;
        }

        plugin.getProxyServerManager().addServer(server, source);
    }

    @Override
    protected Optional<Set<ServerStatus>> getAllowableServerStatuses() {
        return Optional.of(Set.of(ServerStatus.ONLINE));
    }
}

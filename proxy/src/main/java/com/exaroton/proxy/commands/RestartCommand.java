package com.exaroton.proxy.commands;

import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.Server;
import com.exaroton.api.server.ServerStatus;
import com.exaroton.proxy.CommonProxyPlugin;
import com.exaroton.proxy.Components;
import com.exaroton.proxy.servers.WaitForStatusSubscriber;
import com.mojang.brigadier.context.CommandContext;
import net.kyori.adventure.text.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

public class RestartCommand extends ServerCommand {
    protected static final Set<ServerStatus> RESTARTABLE_STATUSES = Set.of(ServerStatus.ONLINE);

    /**
     * Create a new command
     *
     * @param plugin    The plugin
     * @param apiClient The exaroton API client
     */
    public RestartCommand(CommonProxyPlugin plugin, ExarotonClient apiClient) {
        super(plugin, apiClient, "restart");
    }

    @Override
    protected <T> void execute(CommandContext<T> context,
                              BuildContext<T> buildContext,
                              Server server) throws IOException {
        CommandSourceAccessor source = buildContext.mapSource(context.getSource());

        if (!server.hasStatus(RESTARTABLE_STATUSES)) {
            source.sendFailure(Component.text("Server has to be online or starting to be stopped"));
            return;
        }

        var subscribers = plugin.getStatusSubscribers();
        subscribers.addProxyStatusSubscriber(server, null);
        new WaitForStatusSubscriber(subscribers.getListener(server), source, ServerStatus.ONLINE)
                .subscribe();

        server.restart();
        source.sendSuccess(Component.text("Stopping server")
                .appendSpace()
                .append(Components.addressText(server))
                .append(Component.text("."))
        );
    }

    @Override
    protected Optional<Set<ServerStatus>> getAllowableServerStatuses() {
        return Optional.of(RESTARTABLE_STATUSES);
    }
}

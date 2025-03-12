package com.exaroton.proxy.commands;

import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.Server;
import com.exaroton.api.server.ServerStatus;
import com.exaroton.proxy.CommonProxyPlugin;
import com.exaroton.proxy.Constants;
import com.exaroton.proxy.servers.WaitForStatusSubscriber;
import com.mojang.brigadier.context.CommandContext;
import net.kyori.adventure.text.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

public class StartCommand extends ServerCommand {

    /**
     * Create a new command
     *
     * @param plugin    The plugin
     * @param apiClient The exaroton API client
     */
    public StartCommand(CommonProxyPlugin plugin, ExarotonClient apiClient) {
        super(plugin, apiClient, "start", Permission.START);
    }

    @Override
    protected <T> void execute(CommandContext<T> context,
                              BuildContext<T> buildContext,
                              Server server) throws IOException {
        CommandSourceAccessor source = buildContext.mapSource(context.getSource());

        if (!server.hasStatus(ServerStatus.GROUP_OFFLINE)) {
            source.sendFailure(Component.text("Server has to be offline to be started"));
            return;
        }

        var subscribers = plugin.getStatusSubscribers();
        subscribers.addProxyStatusSubscriber(server, null);
        new WaitForStatusSubscriber(subscribers.getListener(server), source, ServerStatus.ONLINE)
                .subscribe();

        server.start();
        source.sendSuccess(Component.text("Starting server")
                .appendSpace()
                .append(Component.text(server.getAddress(), Constants.EXAROTON_GREEN))
                .append(Component.text("."))
        );
    }

    @Override
    protected Optional<Set<ServerStatus>> getAllowableServerStatuses() {
        return Optional.of(ServerStatus.GROUP_OFFLINE);
    }
}

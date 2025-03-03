package com.exaroton.proxy.commands;

import com.exaroton.api.APIException;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.Server;
import com.exaroton.api.server.ServerStatus;
import com.exaroton.proxy.CommonPlugin;
import com.exaroton.proxy.Constants;
import com.exaroton.proxy.servers.WaitForStatusSubscriber;
import com.mojang.brigadier.context.CommandContext;
import net.kyori.adventure.text.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class StartCommand extends ServerCommand {

    /**
     * Create a new command
     *
     * @param plugin    The plugin
     * @param apiClient The exaroton API client
     */
    public StartCommand(CommonPlugin plugin, ExarotonClient apiClient) {
        super(plugin, apiClient, "start", Permission.START);
    }

    @Override
    protected <T> int execute(CommandContext<T> context,
                              BuildContext<T> buildContext,
                              Server server) throws APIException {
        CommandSourceAccessor source = buildContext.mapSource(context.getSource());

        if (!server.hasStatus(ServerStatus.OFFLINE, ServerStatus.CRASHED)) {
            source.sendFailure(Component.text("Server has to be offline to be started"));
            return 0;
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

        return 0;
    }

    @Override
    protected Optional<Collection<Integer>> getAllowableServerStatuses() {
        return Optional.of(List.of(ServerStatus.OFFLINE, ServerStatus.CRASHED));
    }
}

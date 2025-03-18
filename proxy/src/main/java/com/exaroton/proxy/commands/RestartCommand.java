package com.exaroton.proxy.commands;

import com.exaroton.api.server.Server;
import com.exaroton.api.server.ServerStatus;
import com.exaroton.proxy.CommonProxyPlugin;
import com.exaroton.proxy.Components;
import com.exaroton.proxy.StatusGroups;
import com.exaroton.proxy.servers.WaitForStatusSubscriber;
import net.kyori.adventure.text.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

public class RestartCommand extends ServerCommand {
    /**
     * Create a new command
     *
     * @param plugin    The plugin
     */
    public RestartCommand(CommonProxyPlugin plugin) {
        super(plugin, "restart");
    }

    @Override
    public void execute(CommandSourceAccessor source, Server server) throws IOException {
        if (!server.hasStatus(StatusGroups.RESTARTABLE)) {
            source.sendFailure(Components.incorrectStatus(server, StatusGroups.RESTARTABLE, "restarted"));
            return;
        }

        var subscribers = plugin.getStatusSubscribers();
        subscribers.addProxyStatusSubscriber(server);
        new WaitForStatusSubscriber(subscribers.getListener(server), source, ServerStatus.ONLINE)
                .subscribe();

        server.restart();
        source.sendSuccess(Component.text("Restarting server")
                .appendSpace()
                .append(Components.addressText(server))
                .append(Component.text("."))
        );
    }

    @Override
    protected Optional<Set<ServerStatus>> getAllowableServerStatuses() {
        return Optional.of(StatusGroups.RESTARTABLE);
    }
}

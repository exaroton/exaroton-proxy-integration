package com.exaroton.proxy.commands;

import com.exaroton.api.server.Server;
import com.exaroton.api.server.ServerStatus;
import com.exaroton.proxy.CommonProxyPlugin;
import com.exaroton.proxy.Components;
import com.exaroton.proxy.StatusGroups;
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
     */
    public StartCommand(CommonProxyPlugin plugin) {
        super(plugin, "start");
    }

    @Override
    protected <T> void execute(CommandContext<T> context,
                              BuildContext<T> buildContext,
                              Server server) throws IOException {
        CommandSourceAccessor source = buildContext.mapSource(context.getSource());

        if (!server.hasStatus(StatusGroups.STARTABLE)) {
            source.sendFailure(Components.incorrectStatus(server, StatusGroups.STARTABLE, "started"));
            return;
        }

        var subscribers = plugin.getStatusSubscribers();
        subscribers.addProxyStatusSubscriber(server);
        new WaitForStatusSubscriber(subscribers.getListener(server), source, ServerStatus.ONLINE)
                .subscribe();

        server.start();
        source.sendSuccess(Component.text("Starting server")
                .appendSpace()
                .append(Components.addressText(server))
                .append(Component.text("."))
        );
    }

    @Override
    protected Optional<Set<ServerStatus>> getAllowableServerStatuses() {
        return Optional.of(StatusGroups.STARTABLE);
    }
}

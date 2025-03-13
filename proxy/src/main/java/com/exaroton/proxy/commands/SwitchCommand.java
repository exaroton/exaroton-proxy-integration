package com.exaroton.proxy.commands;

import com.exaroton.api.server.Server;
import com.exaroton.api.server.ServerStatus;
import com.exaroton.proxy.CommonProxyPlugin;
import com.exaroton.proxy.Components;
import com.exaroton.proxy.Sets;
import com.exaroton.proxy.servers.WaitForStatusSubscriber;
import com.mojang.brigadier.context.CommandContext;
import net.kyori.adventure.text.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

public class SwitchCommand extends ServerCommand {
    protected static final Set<ServerStatus> STARTING_STATUSES = Set.of(
            ServerStatus.STARTING,
            ServerStatus.LOADING,
            ServerStatus.RESTARTING,
            ServerStatus.PREPARING
    );

    protected static final Set<ServerStatus> SWITCHABLE_STATUSES = Sets.union(
            StartCommand.STARTABLE_STATUSES,
            STARTING_STATUSES,
            Set.of(ServerStatus.ONLINE)
    );

    /**
     * Create a new command
     *
     * @param plugin    The plugin
     */
    public SwitchCommand(CommonProxyPlugin plugin) {
        super(plugin, "switch");
    }

    @Override
    protected <T> void execute(CommandContext<T> context, BuildContext<T> buildContext, Server server) throws IOException {
        CommandSourceAccessor source = buildContext.mapSource(context.getSource());

        if (!server.hasStatus(SWITCHABLE_STATUSES)) {
            source.sendFailure(Components.incorrectStatus(server, SWITCHABLE_STATUSES, "switched to"));
            return;
        }

        if (source.getPlayerName().isEmpty()) {
            source.sendFailure(Component.text("The switch command can only be used by players. Use the transfer command instead."));
            return;
        }

        var subscribers = plugin.getStatusSubscribers();
        subscribers.addProxyStatusSubscriber(server, null);

        if (server.hasStatus(ServerStatus.ONLINE)) {

            if (!plugin.getProxyServerManager().hasServer(server)) {
                plugin.getProxyServerManager().addServer(server);
            }

            source.sendSuccess(Component.text("Switching to ")
                    .append(Components.addressText(server))
                    .append(Component.text("."))
            );
            plugin.getProxyServerManager().transferPlayer(server, source.getPlayerName().get());
            return;
        }

        new WaitForStatusSubscriber(subscribers.getListener(server), source, ServerStatus.ONLINE)
                .subscribe()
                .thenAccept(s -> plugin.getProxyServerManager().transferPlayer(server, source.getPlayerName().get()));

        if (server.hasStatus(StartCommand.STARTABLE_STATUSES)) {
            source.sendSuccess(Component.text("Starting server")
                    .appendSpace()
                    .append(Components.addressText(server))
                    .append(Component.text("."))
            );
            server.start();
        }

        source.sendSuccess(Component.text("You will be transferred once the server goes online."));
    }

    @Override
    protected Optional<Set<ServerStatus>> getAllowableServerStatuses() {
        return Optional.of(SWITCHABLE_STATUSES);
    }
}

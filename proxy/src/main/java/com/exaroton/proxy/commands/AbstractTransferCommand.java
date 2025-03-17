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

/**
 * Abstract base class for commands that transfer players (e.g. transfer, switch)
 */
public abstract class AbstractTransferCommand extends ServerCommand {
    /**
     * Create a new command
     *
     * @param plugin The plugin
     * @param name   The name of the subcommand
     */
    public AbstractTransferCommand(CommonProxyPlugin plugin, String name) {
        super(plugin, name);
    }

    /**
     * Execute the command
     * @param context The command context
     * @param buildContext The build context
     * @param server The server to transfer players to
     * @param playerNames The names of the players to transfer
     * @param subject A string describing who will be transferred (e.g. "You" or "The players")
     * @param <T> The type of the command source
     * @throws IOException If an error occurs while transferring players
     */
    protected <T> void execute(
            CommandContext<T> context,
            BuildContext<T> buildContext,
            Server server,
            Set<String> playerNames,
            String subject
    ) throws IOException {
        CommandSourceAccessor source = buildContext.mapSource(context.getSource());

        if (!server.hasStatus(StatusGroups.SWITCHABLE)) {
            source.sendFailure(Components.incorrectStatus(server, StatusGroups.SWITCHABLE, "switched to"));
            return;
        }

        var subscribers = plugin.getStatusSubscribers();
        subscribers.addProxyStatusSubscriber(server);

        if (server.hasStatus(ServerStatus.ONLINE)) {

            if (!plugin.getProxyServerManager().hasServer(server)) {
                plugin.getProxyServerManager().addServer(server);
            }

            source.sendSuccess(Component.text("Switching to ")
                    .append(Components.addressText(server))
                    .append(Component.text("."))
            );
            source.transferPlayers(server, playerNames);
            return;
        }

        new WaitForStatusSubscriber(subscribers.getListener(server), source, ServerStatus.ONLINE)
                .subscribe()
                .thenAccept(s -> source.transferPlayers(server, playerNames));

        if (server.hasStatus(StatusGroups.STARTABLE)) {
            source.sendSuccess(Component.text("Starting server")
                    .appendSpace()
                    .append(Components.addressText(server))
                    .append(Component.text("."))
            );
            server.start();
        }

        source.sendSuccess(Component.text(subject + " will be transferred once the server goes online."));
    }

    @Override
    protected Optional<Set<ServerStatus>> getAllowableServerStatuses() {
        return Optional.of(StatusGroups.SWITCHABLE);
    }
}

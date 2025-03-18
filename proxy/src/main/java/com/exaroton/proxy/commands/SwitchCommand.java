package com.exaroton.proxy.commands;

import com.exaroton.api.server.Server;
import com.exaroton.api.server.ServerStatus;
import com.exaroton.proxy.CommonProxyPlugin;
import com.exaroton.proxy.Components;
import com.exaroton.proxy.StatusGroups;
import com.exaroton.proxy.commands.arguments.PlayerList;
import com.exaroton.proxy.commands.arguments.PlayerListArgumentType;
import com.exaroton.proxy.servers.WaitForStatusSubscriber;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.kyori.adventure.text.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

public class SwitchCommand extends ServerCommand {
    private static final String ARGUMENT_PLAYERS = "players";

    /**
     * Create a new command
     *
     * @param plugin The plugin
     */
    public SwitchCommand(CommonProxyPlugin plugin) {
        super(plugin, "switch");
    }

    @Override
    protected <T> ArgumentBuilder<T, ?> buildWithServer(BuildContext<T> buildContext, RequiredArgumentBuilder<T, ?> builder) {
        return super.buildWithServer(buildContext, builder)
                .then(RequiredArgumentBuilder.<T, PlayerList>argument(ARGUMENT_PLAYERS, new PlayerListArgumentType(plugin))
                        .executes(context -> this.executeWithServer(
                                context,
                                buildContext,
                                (source, server) -> {
                                    var players = context.getArgument(ARGUMENT_PLAYERS, PlayerList.class);
                                    execute(source, server, players.getPlayers(), "The players");
                                }
                        )));
    }

    @Override
    public void execute(CommandSourceAccessor source, Server server) throws IOException {
        if (source.getPlayerName().isEmpty()) {
            source.sendFailure(Component.text("A list of players has to be provided if the command is not executed by a player."));
            return;
        }

        execute(source, server, Set.of(source.getPlayerName().get()), "You");
    }

    /**
     * Execute the command
     *
     * @param source      The command source
     * @param server      The server to transfer players to
     * @param playerNames The names of the players to transfer
     * @param subject     A string describing who will be transferred (e.g. "You" or "The players")
     * @throws IOException If an error occurs while transferring players
     */
    protected void execute(
            CommandSourceAccessor source,
            Server server,
            Set<String> playerNames,
            String subject
    ) throws IOException {
        final var userNames = source.filterPlayers(playerNames);

        if (userNames.isEmpty()) {
            source.sendFailure(Component.text("No valid players provided."));
            return;
        }

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
            source.transferPlayers(server, userNames);
            return;
        }

        new WaitForStatusSubscriber(subscribers.getListener(server), source, ServerStatus.ONLINE)
                .subscribe()
                .thenAccept(s -> source.transferPlayers(server, userNames));

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

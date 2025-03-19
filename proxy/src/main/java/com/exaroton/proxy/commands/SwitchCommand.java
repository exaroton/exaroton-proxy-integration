package com.exaroton.proxy.commands;

import com.exaroton.api.server.Server;
import com.exaroton.api.server.ServerStatus;
import com.exaroton.proxy.CommonProxyPlugin;
import com.exaroton.proxy.Components;
import com.exaroton.proxy.StatusGroups;
import com.exaroton.proxy.servers.WaitForStatusSubscriber;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.kyori.adventure.text.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class SwitchCommand extends ServerPlayersCommand {

    /**
     * Create a new command
     *
     * @param plugin The plugin
     */
    public SwitchCommand(CommonProxyPlugin plugin) {
        super(plugin, "switch");
    }

    @Override
    protected void execute(
            CommandSourceAccessor source,
            Server server,
            Set<String> playerNames,
            String subject
    ) throws Exception {
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

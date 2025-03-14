package com.exaroton.proxy.commands;

import com.exaroton.api.server.Server;
import com.exaroton.proxy.CommonProxyPlugin;
import com.exaroton.proxy.commands.arguments.PlayerList;
import com.exaroton.proxy.commands.arguments.PlayerListArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.kyori.adventure.text.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

public class TransferCommand extends AbstractTransferCommand {
    private static final String ARGUMENT_PLAYERS = "players";

    /**
     * Create a new command
     *
     * @param plugin The plugin
     */
    public TransferCommand(CommonProxyPlugin plugin) {
        super(plugin, "transfer");
    }

    @Override
    protected <T> ArgumentBuilder<T, ?> buildWithServer(BuildContext<T> buildContext, RequiredArgumentBuilder<T, ?> builder) {
        var x = super.buildWithServer(
                buildContext,
                RequiredArgumentBuilder.argument(ARGUMENT_PLAYERS, new PlayerListArgumentType(plugin))
        );
        return builder.then(x);
    }

    @Override
    protected <T> void execute(CommandContext<T> context, BuildContext<T> buildContext, Server server) throws IOException {
        CommandSourceAccessor source = buildContext.mapSource(context.getSource());
        Set<String> playerNames = context.getArgument(ARGUMENT_PLAYERS, PlayerList.class).getPlayers(plugin.getPlayers());

        if (playerNames.isEmpty()) {
            source.sendFailure(Component.translatable("argument.entity.notfound.player"));
            return;
        }

        execute(context, buildContext, server, playerNames, "The players");
    }
}

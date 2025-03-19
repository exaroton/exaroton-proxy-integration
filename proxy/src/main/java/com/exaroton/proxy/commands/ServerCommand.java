package com.exaroton.proxy.commands;

import com.exaroton.api.server.Server;
import com.exaroton.api.server.ServerStatus;
import com.exaroton.proxy.CommonProxyPlugin;
import com.exaroton.proxy.Constants;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.kyori.adventure.text.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * A command for server-related actions
 */
public abstract class ServerCommand extends Command<CommonProxyPlugin> implements ServerCommandCallback {
    protected static final String ARGUMENT_SERVER = "server";

    protected final String name;

    /**
     * Create a new command
     *
     * @param plugin The plugin
     * @param name   The name of the subcommand
     */
    public ServerCommand(CommonProxyPlugin plugin, String name) {
        super(plugin);
        this.name = name;
    }

    @Override
    public <T> LiteralArgumentBuilder<T> build(BuildContext<T> buildContext, LiteralArgumentBuilder<T> builder) {
        ArgumentBuilder<T, ?> x = buildWithServer(buildContext, RequiredArgumentBuilder.<T, String>argument(ARGUMENT_SERVER, StringArgumentType.string())
                .suggests(this::suggestServerName));

        return builder.then(LiteralArgumentBuilder.<T>literal(name)
                .requires(source -> buildContext.mapSource(source).hasPermission("exaroton." + name))
                .then(x)
        );
    }

    protected <T> ArgumentBuilder<T, ?> buildWithServer(BuildContext<T> buildContext, RequiredArgumentBuilder<T, ?> builder) {
        return builder.executes(context -> this.executeWithServer(context, buildContext, this));
    }

    protected <T> int executeWithServer(
            CommandContext<T> context,
            BuildContext<T> buildContext,
            ServerCommandCallback callback
    ) {
        CommandSourceAccessor source = buildContext.mapSource(context.getSource());
        String serverInput = context.getArgument(ARGUMENT_SERVER, String.class);

        this.plugin.findServer(serverInput).thenAccept(server -> {
            if (server.isEmpty()) {
                source.sendFailure(Component.text("Failed to find a server with the name " + serverInput));
                return;
            }

            try {
                callback.execute(source, server.get());
            } catch (Exception e) {
                executionException(source, e);
            }
        }).exceptionally(t -> {
            executionException(source, t);
            return null;
        });
        return 1;
    }

    private void executionException(CommandSourceAccessor source, Throwable t) {
        Constants.LOG.error("An error occurred while executing the command", t);
        source.sendFailure(Component.text("An error occurred while executing the command"));
    }

    /**
     * Get all server statuses that should be used for suggestions
     *
     * @return The server statuses or an empty optional if all statuses should be used
     */
    protected abstract Optional<Set<ServerStatus>> getAllowableServerStatuses();

    private <T> CompletableFuture<Suggestions> suggestServerName(CommandContext<T> context, SuggestionsBuilder builder) {
        // TODO: names from the proxy

        Optional<Set<ServerStatus>> allowableStatuses = getAllowableServerStatuses();
        try {
            return this.plugin.getServers().thenApply(servers -> {
                for (Server server : servers) {
                    if (allowableStatuses.isPresent() && !server.hasStatus(allowableStatuses.get())) {
                        continue;
                    }

                    for (String possibleInput : List.of(server.getName(), server.getAddress(), server.getId())) {
                        if (possibleInput.toLowerCase(Locale.ROOT).startsWith(builder.getRemainingLowerCase())) {
                            builder.suggest(possibleInput);
                        }
                    }
                }

                return builder.build();
            }).handleAsync((suggestions, throwable) -> {
                if (throwable != null) {
                    return suggestionException(throwable);
                }

                return CompletableFuture.completedFuture(suggestions);
            }).thenCompose(x -> x);
        } catch (Exception e) {
            return suggestionException(e);
        }
    }

    private CompletableFuture<Suggestions> suggestionException(Throwable t) {
        Constants.LOG.error("An error occurred while suggesting server names", t);
        return Suggestions.empty();
    }
}

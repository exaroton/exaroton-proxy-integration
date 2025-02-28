package com.exaroton.proxy.commands;

import com.exaroton.api.APIException;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.Server;
import com.exaroton.proxy.CommonPlugin;
import com.exaroton.proxy.Constants;
import com.exaroton.proxy.components.IComponent;
import com.exaroton.proxy.components.ComponentFactory;
import com.exaroton.proxy.components.IStyle;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * A command for server-related actions
 */
public abstract class ServerCommand<
        ComponentType extends IComponent<ComponentType, StyleType, ClickEventType>,
        StyleType extends IStyle<StyleType, ClickEventType>,
        ClickEventType
        > extends Command<ComponentType, StyleType, ClickEventType> {
    protected static final String ARGUMENT_SERVER = "server";

    protected final String name;
    protected final Permission permission;

    /**
     * Create a new command
     *
     * @param plugin           The plugin
     * @param apiClient        The exaroton API client
     * @param componentFactory A component factory
     * @param name             The name of the subcommand
     * @param permission       The required permission to execute the command
     */
    public ServerCommand(CommonPlugin plugin,
                         ExarotonClient apiClient,
                         ComponentFactory<ComponentType, StyleType, ClickEventType> componentFactory,
                         String name,
                         Permission permission) {
        super(plugin, apiClient, componentFactory);
        this.name = name;
        this.permission = permission;
    }

    @Override
    public <T> LiteralArgumentBuilder<T> build(BuildContext<T, ComponentType> buildContext, LiteralArgumentBuilder<T> builder) {
        return builder.then(LiteralArgumentBuilder.<T>literal(name)
                .requires(source -> buildContext.mapSource(source).hasPermission(permission))
                .then(RequiredArgumentBuilder.<T, String>argument(ARGUMENT_SERVER, StringArgumentType.string())
                        .suggests(this::suggestServerName)
                        .executes(context -> {
                            ICommandSourceAccessor<ComponentType> source = buildContext.mapSource(context.getSource());
                            String serverInput = context.getArgument(ARGUMENT_SERVER, String.class);

                            Optional<Server> server;
                            try {
                                server = this.plugin.findServer(serverInput);

                                if (server.isEmpty()) {
                                    source.sendFailure(componentFactory.literal("Failed to find a server with the name " + serverInput));
                                    return 1;
                                }

                                return execute(context, buildContext, server.get());
                            } catch (APIException e) {
                                source.sendFailure(
                                        componentFactory.literal("An API Error occurred. Check your log for details!")
                                );
                                return 1;
                            }
                        })
                )
        );
    }

    /**
     * Execute the command
     * @param context The command context
     * @param buildContext The build context
     * @param server The server
     * @return The command result
     * @param <T> The command source type
     * @throws APIException If an API error occurred. This will be caught, logged and the user will receive a message
     */
    protected abstract <T> int execute(CommandContext<T> context,
                                       BuildContext<T, ComponentType> buildContext,
                                       Server server) throws APIException;

    /**
     * Get all server statuses that should be used for suggestions
     * @return The server statuses or an empty optional if all statuses should be used
     */
    protected abstract Optional<Collection<Integer>> getAllowableServerStatuses();

    private  <T> CompletableFuture<Suggestions> suggestServerName(CommandContext<T> context, SuggestionsBuilder builder) {
        Optional<Collection<Integer>> allowableStatuses = getAllowableServerStatuses();
        try {
            for (Server server : this.plugin.getServers()) {
                if (allowableStatuses.isPresent() && !allowableStatuses.get().contains(server.getStatus())) {
                    continue;
                }

                for (String possibleInput : List.of(server.getName(), server.getAddress(), server.getId())) {
                    if (possibleInput.startsWith(builder.getRemaining())) {
                        builder.suggest(possibleInput);
                    }
                }
            }

            return builder.buildFuture();
        } catch (APIException e) {
            Constants.LOG.error("An API Error occurred. Check your log for details!", e);
            return Suggestions.empty();
        }
    }
}

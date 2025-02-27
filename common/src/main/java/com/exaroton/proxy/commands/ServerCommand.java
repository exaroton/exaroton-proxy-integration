package com.exaroton.proxy.commands;

import com.exaroton.api.APIException;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.Server;
import com.exaroton.proxy.CommonPlugin;
import com.exaroton.proxy.components.IComponent;
import com.exaroton.proxy.components.IComponentFactory;
import com.exaroton.proxy.components.IStyle;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

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
                         IComponentFactory<ComponentType, StyleType, ClickEventType> componentFactory,
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
                        .suggests((x, y) -> this.suggestServerName(x, y, buildContext))
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

    protected abstract <T> int execute(CommandContext<T> context,
                                       BuildContext<T, ComponentType> buildContext,
                                       Server server) throws APIException;

    protected <T> CompletableFuture<Suggestions> suggestServerName(CommandContext<T> x, SuggestionsBuilder y, BuildContext<T, ComponentType> buildContext) {
        return null;
    }
}

package com.exaroton.proxy.commands;

import com.exaroton.api.APIException;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.Server;
import com.exaroton.api.server.ServerStatus;
import com.exaroton.proxy.CommonPlugin;
import com.exaroton.proxy.components.IComponent;
import com.exaroton.proxy.components.ComponentFactory;
import com.exaroton.proxy.components.IStyle;
import com.exaroton.proxy.servers.WaitForStatusSubscriber;
import com.mojang.brigadier.context.CommandContext;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class StartCommand<
        ComponentType extends IComponent<ComponentType, StyleType, ClickEventType>,
        StyleType extends IStyle<StyleType, ClickEventType>,
        ClickEventType
        > extends ServerCommand<ComponentType, StyleType, ClickEventType> {

    /**
     * Create a new command
     *
     * @param plugin           The plugin
     * @param apiClient        The exaroton API client
     * @param componentFactory A component factory
     */
    public StartCommand(CommonPlugin plugin,
                        ExarotonClient apiClient,
                        ComponentFactory<ComponentType, StyleType, ClickEventType> componentFactory) {
        super(plugin, apiClient, componentFactory, "start", Permission.START);
    }

    @Override
    protected <T> int execute(CommandContext<T> context,
                              BuildContext<T, ComponentType> buildContext,
                              Server server) throws APIException {
        ICommandSourceAccessor<ComponentType> source = buildContext.mapSource(context.getSource());

        if (!server.hasStatus(ServerStatus.OFFLINE, ServerStatus.CRASHED)) {
            source.sendFailure(componentFactory.literal("Server has to be offline to be started"));
            return 0;
        }

        var subscribers = plugin.getStatusSubscribers();
        subscribers.addProxyStatusSubscriber(server, null);
        new WaitForStatusSubscriber<>(subscribers.getListener(server), source, componentFactory, ServerStatus.ONLINE)
                .subscribe();

        server.start();
        source.sendSuccess(componentFactory.literal("Starting server ")
                .append(componentFactory.exarotonGreen(server.getAddress()))
                .append("."), true);

        return 0;
    }

    @Override
    protected Optional<Collection<Integer>> getAllowableServerStatuses() {
        return Optional.of(List.of(ServerStatus.OFFLINE, ServerStatus.CRASHED));
    }
}

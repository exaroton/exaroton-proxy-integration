package com.exaroton.proxy.servers;

import com.exaroton.api.server.Server;
import com.exaroton.api.server.ServerStatus;
import com.exaroton.api.ws.subscriber.ServerStatusSubscriber;
import com.exaroton.proxy.commands.ICommandSourceAccessor;
import com.exaroton.proxy.components.Color;
import com.exaroton.proxy.components.IComponent;
import com.exaroton.proxy.components.ComponentFactory;
import com.exaroton.proxy.components.IStyle;

import java.util.concurrent.CompletableFuture;

public class WaitForStatusSubscriber<
        ComponentType extends IComponent<ComponentType, StyleType, ?>,
        StyleType extends IStyle<StyleType, ?>
        > extends ServerStatusSubscriber {
    private final CompositeStatusSubscriber parent;
    private final ICommandSourceAccessor<ComponentType> source;
    private final ComponentFactory<ComponentType, StyleType, ?> components;
    private final Integer targetStatus;
    private CompletableFuture<Server> future;

    public WaitForStatusSubscriber(CompositeStatusSubscriber parent,
                                   ICommandSourceAccessor<ComponentType> source,
                                   ComponentFactory<ComponentType, StyleType, ?> components,
                                   Integer targetStatus) {
        this.parent = parent;
        this.source = source;
        this.components = components;
        this.targetStatus = targetStatus;
    }

    public CompletableFuture<Server> subscribe() {
        if (future != null) {
            return future;
        }

        future = new CompletableFuture<>();
        parent.addSubscriber(this);
        return future;
    }

    @Override
    public void statusUpdate(Server oldServer, Server newServer) {
        if (newServer.getStatus() == targetStatus) {
            future.complete(newServer);
            parent.removeSubscriber(this);
        }

        if (oldServer.getStatus() != newServer.getStatus()) {
            ComponentType status;

            switch (newServer.getStatus()) {
                case ServerStatus.ONLINE:
                    status = components.literal("online")
                            .style(components.style().color(Color.EXAROTON_GREEN));
                    break;
                case ServerStatus.STARTING:
                    status = components.literal("offline")
                            .style(components.style().color(Color.RED));
                    break;
                default:
                    return;
            }

            ComponentType message = components.literal("Server ")
                    .append(components.exarotonGreen(newServer.getAddress()))
                    .append(components.literal(" changed status to "))
                    .append(status);
            source.sendSuccess(message, true);
        }
    }
}

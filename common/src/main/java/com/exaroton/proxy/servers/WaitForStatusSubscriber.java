package com.exaroton.proxy.servers;

import com.exaroton.api.server.Server;
import com.exaroton.api.server.ServerStatus;
import com.exaroton.api.ws.subscriber.ServerStatusSubscriber;
import com.exaroton.proxy.Components;
import com.exaroton.proxy.commands.CommandSourceAccessor;
import net.kyori.adventure.text.Component;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class WaitForStatusSubscriber implements ServerStatusSubscriber {
    private final CompositeStatusSubscriber parent;
    private final CommandSourceAccessor source;
    private final Set<ServerStatus> targetStatus;
    private CompletableFuture<Server> future;

    public WaitForStatusSubscriber(CompositeStatusSubscriber parent,
                                   CommandSourceAccessor source,
                                   ServerStatus... targetStatus) {
        this(parent, source, Set.of(targetStatus));
    }

    public WaitForStatusSubscriber(CompositeStatusSubscriber parent,
                                   CommandSourceAccessor source,
                                   Set<ServerStatus> targetStatus) {
        this.parent = parent;
        this.source = source;
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
    public void handleStatusUpdate(Server oldServer, Server newServer) {
        if (newServer.hasStatus(targetStatus)) {
            future.complete(newServer);
            parent.removeSubscriber(this);
        }

        if (oldServer.getStatus() != newServer.getStatus()) {
            Component message = Component.text("Server")
                    .appendSpace()
                    .append(Components.addressText(newServer))
                    .appendSpace()
                    .append(Component.text("changed status to"))
                    .appendSpace()
                    .append(Components.statusText(newServer.getStatus()))
                    .append(Component.text("."));
            source.sendSuccess(message);
        }
    }
}

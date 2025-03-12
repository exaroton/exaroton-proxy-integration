package com.exaroton.proxy.servers;

import com.exaroton.api.server.Server;
import com.exaroton.api.server.ServerStatus;
import com.exaroton.api.ws.subscriber.ServerStatusSubscriber;
import com.exaroton.proxy.Constants;
import com.exaroton.proxy.commands.CommandSourceAccessor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.concurrent.CompletableFuture;

public class WaitForStatusSubscriber implements ServerStatusSubscriber {
    private final CompositeStatusSubscriber parent;
    private final CommandSourceAccessor source;
    private final ServerStatus targetStatus;
    private CompletableFuture<Server> future;

    public WaitForStatusSubscriber(CompositeStatusSubscriber parent,
                                   CommandSourceAccessor source,
                                   ServerStatus targetStatus) {
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
        if (newServer.getStatus() == targetStatus) {
            future.complete(newServer);
            parent.removeSubscriber(this);
        }

        if (oldServer.getStatus() != newServer.getStatus()) {
            Component status;

            switch (newServer.getStatus()) {
                case ONLINE:
                    status = Component.text("online", Constants.EXAROTON_GREEN);
                    break;
                case OFFLINE:
                    status = Component.text("offline", NamedTextColor.RED);
                    break;
                default:
                    return;
            }

            Component message = Component.text("Server")
                    .appendSpace()
                    .append(Component.text(newServer.getAddress(), Constants.EXAROTON_GREEN))
                    .appendSpace()
                    .append(Component.text("changed status to"))
                    .appendSpace()
                    .append(status);
            source.sendSuccess(message);
        }
    }
}

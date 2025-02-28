package com.exaroton.proxy.servers;

import com.exaroton.api.server.Server;
import com.exaroton.api.ws.subscriber.ServerStatusSubscriber;

import java.util.concurrent.CompletableFuture;

public class WaitForStatusSubscriber extends ServerStatusSubscriber {
    private final CompositeStatusSubscriber parent;
    private final Integer targetStatus;
    private final CompletableFuture<Server> future;

    public WaitForStatusSubscriber(CompositeStatusSubscriber parent, Integer targetStatus, CompletableFuture<Server> future) {
        this.parent = parent;
        this.targetStatus = targetStatus;
        this.future = future;

        parent.addSubscriber(this);
    }

    @Override
    public void statusUpdate(Server oldServer, Server newServer) {
        if (newServer.getStatus() == targetStatus) {
            future.complete(newServer);
            parent.removeSubscriber(this);
        }
    }
}

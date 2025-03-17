package com.exaroton.proxy.servers;

import com.exaroton.api.server.Server;
import com.exaroton.api.server.ServerStatus;
import com.exaroton.api.ws.subscriber.ServerStatusSubscriber;
import com.exaroton.proxy.Constants;

public class UpdateProxyServersSubscriber implements ServerStatusSubscriber {
    private final ProxyServerManager<?> serverManager;

    /**
     * Create a new subscriber
     * @param serverManager proxy server manager
     */
    public UpdateProxyServersSubscriber(ProxyServerManager<?> serverManager) {
        super();
        this.serverManager = serverManager;
    }

    @Override
    public void handleStatusUpdate(Server oldServer, Server newServer) {
        if (newServer.getStatus() == oldServer.getStatus()) {
            return;
        }

        if (newServer.hasStatus(ServerStatus.ONLINE)) {
            if (serverManager.addServer(newServer)) {
                Constants.LOG.info("Added server {} to proxy", newServer.getAddress());
            }
        } else if (oldServer.hasStatus(ServerStatus.ONLINE)) {
            if (serverManager.removeServer(newServer)) {
                Constants.LOG.info("Removed server {} from proxy", newServer.getAddress());
            }
        }
    }
}

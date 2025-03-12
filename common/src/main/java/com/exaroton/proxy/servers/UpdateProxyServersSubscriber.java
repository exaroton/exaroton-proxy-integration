package com.exaroton.proxy.servers;

import com.exaroton.api.server.Server;
import com.exaroton.api.server.ServerStatus;
import com.exaroton.api.ws.subscriber.ServerStatusSubscriber;
import com.exaroton.proxy.Constants;
import com.exaroton.proxy.servers.proxy.IProxyServerManager;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class UpdateProxyServersSubscriber implements ServerStatusSubscriber {
    private final Map<String, String> names = new HashMap<>();
    private final IProxyServerManager serverManager;

    /**
     * Create a new subscriber
     * @param serverManager proxy server manager
     */
    public UpdateProxyServersSubscriber(IProxyServerManager serverManager) {
        super();
        this.serverManager = serverManager;
    }

    public void addServerName(String serverId, @Nullable String name) {
        if (name != null) {
            this.names.put(serverId, name);
        }
    }

    @Override
    public void handleStatusUpdate(Server oldServer, Server newServer) {
        if (newServer.getStatus() == oldServer.getStatus()) {
            return;
        }

        if (newServer.hasStatus(ServerStatus.ONLINE)) {
            if (serverManager.addServer(getName(newServer), newServer)) {
                Constants.LOG.info("Added server {} to proxy", getName(newServer));
            }
        } else if (oldServer.hasStatus(ServerStatus.ONLINE)) {
            if (serverManager.removeServer(getName(newServer), newServer)) {
                Constants.LOG.info("Removed server {} from proxy", getName(newServer));
            }
        }
    }

    private String getName(Server server) {
        return this.names.getOrDefault(server.getId(), server.getName());
    }
}

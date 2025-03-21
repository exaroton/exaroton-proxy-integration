package com.exaroton.proxy.servers;

import com.exaroton.api.server.Server;

import java.util.HashMap;
import java.util.Map;

/**
 * This class manages all status listeners to all servers.
 * There is one composite listener for each server that delegates events to other listeners.
 * @see CompositeStatusSubscriber
 */
public class StatusSubscriberManager {
    /**
     * The server cache that will be added to every new listener
     */
    private final ServerCache serverCache;

    /**
     * The subscriber that updates the proxy servers
     */
    private final UpdateProxyServersSubscriber updateProxyServersSubscriber;

    /**
     * All listeners (server id -> composite subscriber)
     */
    private final Map<String, CompositeStatusSubscriber> listeners = new HashMap<>();

    /**
     * Create a new status subscriber manager
     * @param serverCache server cache
     */
    public StatusSubscriberManager(ServerCache serverCache,
                                   ProxyServerManager<?> serverManager) {
        this.serverCache = serverCache;
        this.updateProxyServersSubscriber = new UpdateProxyServersSubscriber(serverManager);
    }

    /**
     * Get the listener for a specific server
     * @param server server
     * @return the composite listener
     */
    public CompositeStatusSubscriber getListener(Server server) {
        return listeners.computeIfAbsent(server.getId(), id -> this.createListener(server));
    }

    /**
     * Add the proxy status subscriber to a server
     * @param server server
     */
    public void addProxyStatusSubscriber(Server server) {
        CompositeStatusSubscriber subscriber = getListener(server);
        if (!subscriber.getSubscribers().contains(updateProxyServersSubscriber)) {
            subscriber.addSubscriber(updateProxyServersSubscriber);
        }
    }

    /**
     * Remove the proxy status subscriber from a server
     */
    public void disconnectAll() {
        for (CompositeStatusSubscriber listener : listeners.values()) {
            listener.disconnect();
        }
    }

    private CompositeStatusSubscriber createListener(Server server) {
        CompositeStatusSubscriber subscriber = new CompositeStatusSubscriber(serverCache, server);
        server.addStatusSubscriber(subscriber);
        return subscriber;
    }
}

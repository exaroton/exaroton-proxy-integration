package com.exaroton.proxy.servers;

import com.exaroton.api.server.Server;
import com.exaroton.api.ws.subscriber.ServerStatusSubscriber;

import java.util.Collection;
import java.util.HashSet;

/**
 * A status subscriber that delegates events to other subscribers.
 * This class is used to manage multiple subscribers for a single server.
 * It automatically unsubscribes from the server if there are no subscribers left.
 */
public class CompositeStatusSubscriber implements ServerStatusSubscriber {
    /**
     * All subscribers
     */
    protected final Collection<ServerStatusSubscriber> subscribers = new HashSet<>();

    /**
     * The server
     */
    protected final Server server;

    /**
     * Create a new composite subscriber
     * @param serverCache server cache
     * @param server server
     */
    public CompositeStatusSubscriber(ServerCache serverCache, Server server) {
        this.server = server;
        subscribers.add(serverCache);
    }

    /**
     * Add a new subscriber
     * @param subscriber subscriber
     */
    public void addSubscriber(ServerStatusSubscriber subscriber) {
        synchronized (subscribers) {
            subscribers.add(subscriber);
        }
    }

    /**
     * Remove a subscriber and unsubscribe from the server if there are no subscribers left
     *
     * @param subscriber subscriber
     */
    public void removeSubscriber(ServerStatusSubscriber subscriber) {
        synchronized (subscribers) {
            subscribers.remove(subscriber);

            // unsubscribe if only the server cache is left
            if (subscribers.stream().allMatch(s -> s instanceof ServerCache)) {
                server.unsubscribe();
            }
        }
    }

    /**
     * Get all subscribers
     * @return all subscribers
     */
    public Collection<ServerStatusSubscriber> getSubscribers() {
        synchronized (subscribers) {
            return new HashSet<>(subscribers);
        }
    }

    @Override
    public void handleStatusUpdate(Server oldServer, Server newServer) {
        for (ServerStatusSubscriber subscriber : getSubscribers()) {
            subscriber.handleStatusUpdate(oldServer, newServer);
        }
    }

    /**
     * Unsubscribe from the server
     */
    public void disconnect() {
        server.unsubscribe();
    }
}

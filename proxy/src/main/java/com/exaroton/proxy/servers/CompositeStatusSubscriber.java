package com.exaroton.proxy.servers;

import com.exaroton.api.server.Server;
import com.exaroton.api.ws.subscriber.ServerStatusSubscriber;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multimaps;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * A status subscriber that delegates events to other subscribers.
 * This class is used to manage multiple subscribers for a single server.
 * It automatically unsubscribes from the server if there are no subscribers left.
 */
public class CompositeStatusSubscriber implements ServerStatusSubscriber {
    /**
     * All subscribers
     */
    protected final Multimap<Phasing, ServerStatusSubscriber> subscribers = Multimaps.synchronizedSetMultimap(
            MultimapBuilder.enumKeys(Phasing.class).hashSetValues().build());

    /**
     * The server
     */
    protected final Server server;

    /**
     * Create a new composite subscriber
     *
     * @param serverCache server cache
     * @param server      server
     */
    public CompositeStatusSubscriber(ServerCache serverCache, Server server) {
        this.server = server;
        subscribers.put(Phasing.NORMAL, serverCache);
    }

    /**
     * Add a new subscriber
     *
     * @param subscriber subscriber
     */
    public void addSubscriber(ServerStatusSubscriber subscriber) {
        subscribers.put(Phasing.NORMAL, subscriber);
    }

    /**
     * Add a new subscriber
     *
     * @param subscriber subscriber
     */
    public void addSubscriber(Phasing phasing, ServerStatusSubscriber subscriber) {
        subscribers.put(phasing, subscriber);
    }

    /**
     * Remove a subscriber and unsubscribe from the server if there are no subscribers left
     *
     * @param subscriber subscriber
     */
    public void removeSubscriber(Phasing phasing, ServerStatusSubscriber subscriber) {
        subscribers.remove(phasing, subscriber);
        // unsubscribe if only the server cache is left
        if (subscribers.values().stream().allMatch(s -> s instanceof ServerCache)) {
            server.unsubscribe();
        }
    }

    /**
     * Get all subscribers
     *
     * @return all subscribers
     */
    public Collection<ServerStatusSubscriber> getSubscribers() {
        return new HashSet<>(subscribers.values());
    }

    public Collection<ServerStatusSubscriber> getSubscribers(Phasing phasing) {
        return new HashSet<>(subscribers.get(phasing));
    }

    @Override
    public void handleStatusUpdate(Server oldServer, Server newServer) {
        for (Phasing phasing : List.of(Phasing.NORMAL, Phasing.LATE)) {
            for (ServerStatusSubscriber subscriber : getSubscribers(phasing)) {
                subscriber.handleStatusUpdate(oldServer, newServer);
            }
        }
    }

    /**
     * Unsubscribe from the server
     */
    public void disconnect() {
        server.unsubscribe();
    }
}

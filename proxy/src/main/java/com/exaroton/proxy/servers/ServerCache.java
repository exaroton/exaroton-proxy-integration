package com.exaroton.proxy.servers;

import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.Server;
import com.exaroton.api.ws.subscriber.ServerStatusSubscriber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * A cache for exaroton servers
 */
public class ServerCache implements ServerStatusSubscriber {
    private static final Duration CACHE_DURATION = Duration.ofMinutes(1);

    @NotNull
    private final ExarotonClient apiClient;
    @Nullable
    private Instant lastUpdate;
    @NotNull
    private final Map<String, Server> servers = new HashMap<>();

    /**
     * Create a new server cache
     *
     * @param apiClient exaroton API client
     */
    public ServerCache(@NotNull ExarotonClient apiClient) {
        super();
        this.apiClient = apiClient;
    }

    /**
     * Refresh the server cache
     */
    public CompletableFuture<Void> refresh() {
        try {
            return apiClient.getServers().thenAccept(response -> {
                synchronized (this.servers) {
                    this.servers.clear();
                    for (Server server : response) {
                        this.servers.put(server.getId(), server);
                    }
                    this.lastUpdate = Instant.now();
                }
            });
        } catch (IOException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * Get all servers
     *
     * @return all servers
     */
    public CompletableFuture<Collection<Server>> getServers() {
        return refreshIfNecessary().thenApply(s -> {
            synchronized (this.servers) {
                return new ArrayList<>(servers.values());
            }
        });
    }

    /**
     * Get a server by name, id or address
     *
     * @param query name, id or address
     * @return the server if it was found or an empty optional
     */
    public CompletableFuture<Optional<Server>> getServer(@NotNull String query) {
        Objects.requireNonNull(query, "query cannot be null");
        return refreshIfNecessary().thenApply(x -> {
            Optional<Server> server;
            synchronized (this.servers) {
                server = Optional.ofNullable(this.servers.get(query));
            }

            return server.or(() -> getServer(s -> query.equalsIgnoreCase(s.getName())))
                    .or(() -> getServer(s -> query.equalsIgnoreCase(s.getAddress())));
        });
    }

    private Optional<Server> getServer(Function<Server, Boolean> matcher) {
        synchronized (servers) {
            return servers.values().stream().filter(matcher::apply).findFirst();
        }
    }

    private CompletableFuture<Void> refreshIfNecessary() {
        if (isValid()) {
            return CompletableFuture.completedFuture(null);
        }

        return refresh();
    }

    private boolean isValid() {
        if (lastUpdate == null) {
            return false;
        }

        return !Duration.between(lastUpdate, Instant.now().plus(CACHE_DURATION)).isNegative();
    }

    @Override
    public void handleStatusUpdate(Server oldServer, Server newServer) {
        synchronized (servers) {
            servers.put(newServer.getId(), newServer);
        }
    }
}

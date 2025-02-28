package com.exaroton.proxy.servers;

import com.exaroton.api.APIException;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.Server;
import com.exaroton.api.ws.subscriber.ServerStatusSubscriber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;

/**
 * A cache for exaroton servers
 */
public class ServerCache extends ServerStatusSubscriber {
    private static final Duration CACHE_DURATION = Duration.ofMinutes(1);

    @NotNull
    private final ExarotonClient apiClient;
    @Nullable
    private Instant lastUpdate;
    @NotNull
    private final Map<String, Server> servers = new HashMap<>();

    /**
     * Create a new server cache
     * @param apiClient exaroton API client
     */
    public ServerCache(@NotNull ExarotonClient apiClient) {
        super();
        this.apiClient = apiClient;
    }

    /**
     * Refresh the server cache
     * @throws APIException API error
     */
    public void refresh() throws APIException {
        this.servers.clear();
        for (Server server : apiClient.getServers()) {
            this.servers.put(server.getId(), server);
        }
        this.lastUpdate = Instant.now();
    }

    /**
     * Get all servers
     * @return all servers
     * @throws APIException API error while fetching servers
     */
    public Collection<Server> getServers() throws APIException {
        refreshIfNecessary();
        return servers.values();
    }

    /**
     * Get a server by name, id or address
     * @param query name, id or address
     * @return the server if it was found or an empty optional
     * @throws APIException API error while fetching servers
     */
    public Optional<Server> getServer(@NotNull String query) throws APIException {
        Objects.requireNonNull(query, "query cannot be null");
        refreshIfNecessary();

        return Optional.ofNullable(servers.get(query))
                .or(() -> getServer(s -> query.equals(s.getName())))
                .or(() -> getServer(s -> query.equals(s.getAddress())));
    }

    private Optional<Server> getServer(Function<Server, Boolean> matcher) {
        return servers.values().stream().filter(matcher::apply).findFirst();
    }

    private void refreshIfNecessary() throws APIException {
        if (!isValid()) {
            refresh();
        }
    }

    private boolean isValid() {
        if (lastUpdate == null) {
            return false;
        }

        return !Duration.between(lastUpdate, Instant.now().plus(CACHE_DURATION)).isNegative();
    }

    @Override
    public void statusUpdate(Server oldServer, Server newServer) {
        servers.put(newServer.getId(), newServer);
    }
}

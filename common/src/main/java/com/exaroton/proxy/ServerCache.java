package com.exaroton.proxy;

import com.exaroton.api.APIException;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.Server;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;

/**
 * A cache for exaroton servers
 */
public class ServerCache {
    private static final Duration CACHE_DURATION = Duration.ofMinutes(1);

    @NotNull
    private final ExarotonClient apiClient;
    @Nullable
    private Instant lastUpdate;
    @NotNull
    private final Collection<Server> servers = new HashSet<>();

    /**
     * Create a new server cache
     * @param apiClient exaroton API client
     */
    public ServerCache(@NotNull ExarotonClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Refresh the server cache
     * @throws APIException API error
     */
    public void refresh() throws APIException {
        this.servers.clear();
        this.servers.addAll(List.of(apiClient.getServers()));
        this.lastUpdate = Instant.now();
    }

    /**
     * Get all servers
     * @return all servers
     * @throws APIException API error while fetching servers
     */
    public Collection<Server> getServers() throws APIException {
        refreshIfNecessary();
        return servers;
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

        return getServer(s -> query.equals(s.getId()))
                .or(() -> getServer(s -> query.equals(s.getName())))
                .or(() -> getServer(s -> query.equals(s.getAddress())));
    }

    private Optional<Server> getServer(Function<Server, Boolean> matcher) {
        return servers.stream().filter(matcher::apply).findFirst();
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
}

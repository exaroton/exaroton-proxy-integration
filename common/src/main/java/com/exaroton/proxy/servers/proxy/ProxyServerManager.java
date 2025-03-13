package com.exaroton.proxy.servers.proxy;

import com.exaroton.api.server.Server;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Interface for managing servers in a proxy
 */
public abstract class ProxyServerManager {
    /**
     * Map of server ids to their proxy names
     */
    private final Map<String, String> names = new HashMap<>();
    // TODO: Initialize these names from proxy config

    /**
     * Add a server to the proxy
     * @param server server to add
     */
    public boolean addServer(Server server) {
        if (server.getSocketAddress().isEmpty()) {
            return false;
        }

        return this.addServer(getName(server), server.getSocketAddress().get(), server);
    }

    /**
     * Remove a server from the proxy
     * @param server server to remove
     */
    public boolean removeServer(Server server) {
        return this.removeServer(getName(server));
    }

    /**
     * Check if a server with the same name is already in the proxy
     * @param server server to check
     * @return true if the server is already in the proxy
     */
    public boolean hasServer(Server server) {
        return this.hasServer(getName(server));
    }

    /**
     * Check if a server with the same name is already in the proxy
     * @param name name of the server to check
     * @return true if the server is already in the proxy
     */
    protected abstract boolean hasServer(String name);

    /**
     * Get the address of a server by its name
     * @param name name of the server in the proxy
     * @return the address of the server or an empty optional if the server was not found
     */
    public abstract Optional<String> getAddress(String name);

    /**
     * add a server to the proxy
     * @param name identifier for this server from the proxy config or server name
     * @param address InetSocketAddress of the server to add
     * @param server server object
     * @return true if the server was added
     */
    protected abstract boolean addServer(String name, InetSocketAddress address, Server server);

    /**
     * remove a server from the proxy
     * @param name identifier for this server from the proxy config or server name
     * @return true if the server was removed
     */
    protected abstract boolean removeServer(String name);

    private String getName(Server server) {
        return this.names.getOrDefault(server.getId(), server.getName());
    }
}

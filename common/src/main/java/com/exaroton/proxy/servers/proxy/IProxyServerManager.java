package com.exaroton.proxy.servers.proxy;

import com.exaroton.api.server.Server;

import java.util.Optional;

/**
 * Interface for managing servers in a proxy
 */
public interface IProxyServerManager {
    /**
     * add a server to the proxy
     * @param name identifier for this server from the proxy config or server name
     * @param server server to add
     * @return true if the server was added
     */
    boolean addServer(String name, Server server);

    /**
     * remove a server from the proxy
     * @param name identifier for this server from the proxy config or server name
     * @param server server to remove
     * @return true if the server was removed
     */
    boolean removeServer(String name, Server server);

    /**
     * Get the address of a server by its name
     * @param name name of the server in the proxy
     * @return the address of the server or an empty optional if the server was not found
     */
    Optional<String> getAddress(String name);
}

package com.exaroton.proxy.servers.proxy;

import com.exaroton.api.server.Server;
import com.exaroton.proxy.Components;
import com.exaroton.proxy.Constants;
import com.exaroton.proxy.commands.CommandSourceAccessor;
import net.kyori.adventure.text.Component;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Interface for managing servers in a proxy
 */
public abstract class ProxyServerManager {
    /**
     * Map of server ids to their proxy names
     */
    private final Map<String, String> names = new HashMap<>();
    // TODO: Initialize these names from proxy config

    public void addServer(Server server, CommandSourceAccessor source) {
        if (hasServer(server)) {
            source.sendFailure(Component.text("Server is already in the proxy"));
            return;
        }

        if (addServer(server)) {
            source.sendSuccess(Component.text("Added server ")
                    .append(Components.addressText(server))
                    .append(Component.text(" to the proxy.")));
        } else {
            source.sendFailure(Component.text("Failed to add server ")
                    .append(Components.addressText(server))
                    .append(Component.text(" to the proxy.")));
        }
    }

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
     * Transfer a player to another server
     * @param server server to transfer the player to
     * @param player player to transfer
     */
    public void transferPlayer(Server server, String player) {
        String name = getName(server);

        if (!hasServer(name)) {
            Constants.LOG.error("Tried to transfer player {} to non-existing server: {}", player, name);
            return;
        }

        this.transferPlayer(name, player);
    }

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

    /**
     * Check if a server with the same name is already in the proxy
     * @param name name of the server to check
     * @return true if the server is already in the proxy
     */
    protected abstract boolean hasServer(String name);

    /**
     * Transfer a player to another server
     * @param server server to transfer the player to
     * @param player player to transfer
     */
    protected abstract void transferPlayer(String server, String player);

    private String getName(Server server) {
        return this.names.getOrDefault(server.getId(), server.getName());
    }

    /**
     * Transfer a player to another server
     * @param server server to transfer the player to
     * @param playerNames players to transfer
     */
    public void transferPlayers(Server server, Set<String> playerNames) {
        for (String player : playerNames) {
            transferPlayer(server, player);
        }
    }
}

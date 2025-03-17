package com.exaroton.proxy.servers;

import com.exaroton.api.server.Server;
import com.exaroton.proxy.CommonProxyPlugin;
import com.exaroton.proxy.Components;
import com.exaroton.proxy.Constants;
import com.exaroton.proxy.commands.CommandSourceAccessor;
import net.kyori.adventure.text.Component;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for managing servers in a proxy
 * @param <ServerInfo> Type of the server info class form the proxy api
 */
public abstract class ProxyServerManager<ServerInfo> {
    /**
     * Map of server ids to their proxy names
     */
    private final Map<String, ServerInfo> serverInfo = new HashMap<>();
    /**
     * Map of server names to their ids
     */
    private final Map<String, String> idsByName = new HashMap<>();

    public final void addServer(Server server, CommandSourceAccessor source) {
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
     * @return boolean true if the server was added
     */
    public final boolean addServer(Server server) {
        if (server.getSocketAddress().isEmpty()) {
            return false;
        }

        return this.addServer(getName(server), server.getSocketAddress().get(), server);
    }

    /**
     * Remove a server from the proxy
     * @param server server to remove
     * @return true if the server was removed
     */
    public final boolean removeServer(Server server) {
        return this.removeServer(getName(server));
    }

    /**
     * Check if a server with the same name is already in the proxy
     * @param server server to check
     * @return true if the server is already in the proxy
     */
    public final boolean hasServer(Server server) {
        return this.hasServer(getName(server));
    }

    /**
     * Transfer a player to another server
     * @param server server to transfer the player to
     * @param player player to transfer
     */
    public final void transferPlayer(Server server, String player) {
        String name = getName(server);

        if (!hasServer(name)) {
            Constants.LOG.error("Tried to transfer player {} to non-existing server: {}", player, name);
            return;
        }

        this.transferPlayer(name, player);
    }

    /**
     * Transfer a player to another server
     * @param server server to transfer the player to
     * @param playerNames players to transfer
     */
    public final void transferPlayers(Server server, Set<String> playerNames) {
        for (String player : playerNames) {
            transferPlayer(server, player);
        }
    }

    /**
     * Load servers from the proxy config
     * @param plugin the plugin to load the servers for
     * @return A future that completes when all servers are loaded
     */
    public final CompletableFuture<Void> loadServers(CommonProxyPlugin plugin) {
        var futures = new ArrayList<CompletableFuture<Void>>();
        for (ServerInfo info : getServers()) {
            Optional<String> address = getAddress(info).map(InetSocketAddress::getHostString);
            if (address.isEmpty()) {
                continue;
            }

            futures.add(plugin.findServer(address.get()).thenAccept(server -> {
                if (server.isEmpty()) {
                    return;
                }
                this.serverInfo.put(server.get().getId(), info);
                this.idsByName.put(getName(info), server.get().getId());
            }).exceptionally(t -> {
                Constants.LOG.error("Failed to get id for server {}: {}", getName(info), t.getMessage(), t);
                return null;
            }));
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    /**
     * Get the address of a server by its name
     * @param name name of the server in the proxy
     * @return the address of the server or an empty optional if the server was not found
     */
    public final Optional<String> getAddress(String name) {
        return this.getServerInfo(name)
                .flatMap(this::getAddress)
                .map(InetSocketAddress::getHostString);
    }

    /**
     * Get a list of all exaroton server addresses from the proxy
     * @return list of server addresses
     */
    public final Collection<String> getAddresses() {
        var result = new ArrayList<String>();

        for (ServerInfo info : this.serverInfo.values()) {
            getAddress(info)
                    .map(InetSocketAddress::getHostString)
                    .ifPresent(result::add);
        }

        return result;
    }

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

    /**
     * Get a list of all servers registered in the proxy
     * @return list of servers
     */
    protected abstract Collection<ServerInfo> getServers();

    /**
     * Get the server name in the proxy
     * @param server server to get the name for
     * @return server name
     */
    protected abstract String getName(ServerInfo server);

    /**
     * Get the address of a server
     * @param server server to get the address for
     * @return the address of the server or an empty optional (e.g. if the server is connected with a unix socket)
     */
    protected abstract Optional<InetSocketAddress> getAddress(ServerInfo server);

    /**
     * Get the server info by its name
     * @param name name of the server
     * @return server info or an empty optional if the server was not found
     */
    protected Optional<ServerInfo> getServerInfo(String name) {
        String id = this.idsByName.get(name);
        if (id == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(this.serverInfo.get(id));
    }

    private String getName(Server server) {
        return Optional.ofNullable(this.serverInfo.get(server.getId()))
                .map(this::getName)
                .orElseGet(server::getName);
    }
}

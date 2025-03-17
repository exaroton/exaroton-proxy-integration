package com.exaroton.proxy;

import com.exaroton.api.server.Server;
import com.exaroton.proxy.servers.ProxyServerManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Optional;

/**
 * Bungee implementation of the proxy server manager
 */
public class BungeeProxyServerManager extends ProxyServerManager<ServerInfo> {
    private final ProxyServer proxy;

    /**
     * Create a new bungee proxy server manager
     * @param proxy BungeeCord proxy
     */
    public BungeeProxyServerManager(ProxyServer proxy) {
        this.proxy = proxy;
    }

    @Override
    public boolean addServer(String name, InetSocketAddress address, Server server) {
        String motd = server.getMotd();
        boolean restricted = false;

        Optional<ServerInfo> existingServerInfo = getServerInfo(name);
        if (existingServerInfo.isPresent()) {
            motd = existingServerInfo.get().getMotd();
            restricted = existingServerInfo.get().isRestricted();
        }

        ServerInfo serverInfo = proxy.constructServerInfo(name, address, motd, restricted);
        return proxy.getServers().putIfAbsent(name, serverInfo) == null;
    }

    @Override
    public boolean removeServer(String name) {
        return proxy.getServers().remove(name) != null;
    }

    @Override
    protected boolean hasServer(String name) {
        return proxy.getServers().containsKey(name);
    }

    @Override
    protected void transferPlayer(String server, String player) {
        proxy.getPlayer(player).connect(proxy.getServerInfo(server));
    }

    @Override
    protected Collection<ServerInfo> getServers() {
        return proxy.getServers().values();
    }

    @Override
    protected String getName(ServerInfo server) {
        return server.getName();
    }

    @Override
    protected Optional<InetSocketAddress> getAddress(ServerInfo server) {
        var address = server.getSocketAddress();
        if (address instanceof InetSocketAddress) {
            return Optional.of((InetSocketAddress) address);
        }
        return Optional.empty();
    }
}

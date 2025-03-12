package com.exaroton.proxy;

import com.exaroton.api.server.Server;
import com.exaroton.proxy.servers.proxy.IProxyServerManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.net.InetSocketAddress;
import java.util.Optional;

/**
 * Bungee implementation of the proxy server manager
 */
public class BungeeProxyServerManager implements IProxyServerManager {
    private final ProxyServer proxy;

    /**
     * Create a new bungee proxy server manager
     * @param proxy
     */
    public BungeeProxyServerManager(ProxyServer proxy) {
        this.proxy = proxy;
    }

    @Override
    public boolean addServer(String name, Server server) {
        var address = server.getSocketAddress();
        if (address.isEmpty()) {
            return false;
        }

        // TODO: remember restricted option
        ServerInfo serverInfo = proxy.constructServerInfo(
                name,
                address.get(),
                server.getMotd(),
                false
        );
        return proxy.getServers().putIfAbsent(name, serverInfo) == null;
    }

    @Override
    public boolean removeServer(String name, Server server) {
        return proxy.getServers().remove(name) != null;
    }

    @Override
    public Optional<String> getAddress(String name) {
        // TODO: Check if this works
        return Optional.ofNullable(proxy.getServers().get(name))
                .map(ServerInfo::getAddress)
                .map(InetSocketAddress::getHostName);
    }
}

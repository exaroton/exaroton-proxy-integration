package com.exaroton.proxy;

import com.exaroton.api.server.Server;
import com.exaroton.proxy.servers.ProxyServerManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Bungee implementation of the proxy server manager
 */
public class BungeeProxyServerManager extends ProxyServerManager {
    private final ProxyServer proxy;
    // TODO: initialize on startup
    private final Map<String, ServerInfo> servers = new HashMap<>();

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

        if (servers.containsKey(name)) {
            ServerInfo serverInfo = servers.get(name);
            motd = serverInfo.getMotd();
            restricted = serverInfo.isRestricted();
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
    public Optional<String> getAddress(String name) {
        return Optional.ofNullable(proxy.getServers().get(name))
                .map(ServerInfo::getSocketAddress)
                .flatMap(address -> {
                    if (address instanceof InetSocketAddress) {
                        return Optional.of(((InetSocketAddress) address).getHostString());
                    }
                    return Optional.empty();
                });
    }
}

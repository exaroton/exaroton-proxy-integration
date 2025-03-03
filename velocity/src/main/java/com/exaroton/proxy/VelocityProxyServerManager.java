package com.exaroton.proxy;

import com.exaroton.api.server.Server;
import com.exaroton.proxy.servers.proxy.IProxyServerManager;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;

import java.net.InetSocketAddress;
import java.util.Optional;

public class VelocityProxyServerManager implements IProxyServerManager {
    private final ProxyServer proxy;

    public VelocityProxyServerManager(ProxyServer proxy) {
        this.proxy = proxy;
    }

    @Override
    public boolean addServer(String name, Server server) {
        if (proxy.getServer(name).isPresent()) {
            return false;
        }

        proxy.registerServer(new ServerInfo(name, new InetSocketAddress(server.getHost(), server.getPort())));
        return true;
    }

    @Override
    public boolean removeServer(String name, Server server) {
        Optional<RegisteredServer> registered = proxy.getServer(name);

        if (registered.isEmpty()) {
            return false;
        }

        proxy.unregisterServer(registered.get().getServerInfo());
        return true;

    }

    @Override
    public Optional<String> getAddress(String name) {
        return proxy.getServer(name)
                .map(RegisteredServer::getServerInfo)
                .map(ServerInfo::getAddress)
                .map(InetSocketAddress::getHostName);
    }
}

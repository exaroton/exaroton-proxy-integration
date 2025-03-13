package com.exaroton.proxy;

import com.exaroton.api.server.Server;
import com.exaroton.proxy.servers.proxy.ProxyServerManager;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;

import java.net.InetSocketAddress;
import java.util.Optional;

public class VelocityProxyServerManager extends ProxyServerManager {
    private final ProxyServer proxy;

    public VelocityProxyServerManager(ProxyServer proxy) {
        this.proxy = proxy;
    }

    @Override
    public boolean addServer(String name, InetSocketAddress address, Server server) {
        if (proxy.getServer(name).isPresent()) {
            return false;
        }

        proxy.registerServer(new ServerInfo(name, address));
        return true;
    }

    @Override
    public boolean removeServer(String name) {
        Optional<RegisteredServer> registered = proxy.getServer(name);

        if (registered.isEmpty()) {
            return false;
        }

        proxy.unregisterServer(registered.get().getServerInfo());
        return true;

    }

    @Override
    protected boolean hasServer(String name) {
        return proxy.getServer(name).isPresent();
    }

    @Override
    protected void transferPlayer(String serverName, String playerName) {
        Optional<RegisteredServer> server = proxy.getServer(serverName);
        if (server.isEmpty()) {
            Constants.LOG.error("Failed to transfer player {} to server {}: server not found", playerName, serverName);
            return;
        }

        Optional<Player> player = proxy.getPlayer(playerName);
        if (player.isEmpty()) {
            Constants.LOG.error("Failed to transfer player {}: player not found", playerName);
            return;
        }

        player.get().createConnectionRequest(server.get()).fireAndForget();
    }

    @Override
    public Optional<String> getAddress(String name) {
        return proxy.getServer(name)
                .map(RegisteredServer::getServerInfo)
                .map(ServerInfo::getAddress)
                .map(InetSocketAddress::getHostName);
    }
}

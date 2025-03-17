package com.exaroton.proxy;

import com.exaroton.proxy.servers.ProxyServerManager;
import net.md_5.bungee.api.CommandSender;

import java.util.Collection;
import java.util.stream.Collectors;

public class ProxyPluginImpl extends CommonProxyPlugin {

    private final BungeePlugin bungeePlugin;
    private BungeeMessageController messageController;

    public ProxyPluginImpl(BungeePlugin bungeePlugin) {
        this.bungeePlugin = bungeePlugin;
    }

    public BungeeMessageController getMessageController() {
        if (messageController == null) {
            messageController = new BungeeMessageController(bungeePlugin, this);
        }

        return messageController;
    }

    @Override
    public ProxyServerManager<?> createProxyServerManager() {
        return new BungeeProxyServerManager(bungeePlugin.getProxy());
    }

    @Override
    public Collection<String> getPlayers() {
        return bungeePlugin.getProxy().getPlayers().stream().map(CommandSender::getName).collect(Collectors.toList());
    }
}

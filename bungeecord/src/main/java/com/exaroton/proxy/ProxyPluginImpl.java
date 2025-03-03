package com.exaroton.proxy;

import com.exaroton.proxy.servers.proxy.IProxyServerManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ProxyPluginImpl extends CommonProxyPlugin<ProxiedPlayer> implements Listener {

    private final BungeePlugin bungeePlugin;

    public ProxyPluginImpl(BungeePlugin bungeePlugin) {
        this.bungeePlugin = bungeePlugin;
    }

    @Override
    protected void registerChannel(String channelId) {
        bungeePlugin.getProxy().registerChannel(channelId);
    }

    @Override
    protected void executeCommand(ProxiedPlayer source, String[] args) {
        bungeePlugin.getCommand().execute(source, args);
    }

    @Override
    protected IProxyServerManager getProxyServerManager() {
        return new BungeeProxyServerManager(bungeePlugin.getProxy());
    }

    @EventHandler
    public void handleMessage(PluginMessageEvent event) {
        if (event.getReceiver() instanceof ProxiedPlayer) {
            handleMessage(event.getTag(), (ProxiedPlayer) event.getReceiver(), event.getData());
        }
    }
}

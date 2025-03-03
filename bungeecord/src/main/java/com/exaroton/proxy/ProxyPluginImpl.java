package com.exaroton.proxy;

import com.exaroton.proxy.servers.proxy.IProxyServerManager;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ProxyPluginImpl extends CommonProxyPlugin<Server> implements Listener {

    private final BungeePlugin bungeePlugin;

    public ProxyPluginImpl(BungeePlugin bungeePlugin) {
        this.bungeePlugin = bungeePlugin;
    }

    @Override
    protected void registerChannel(String channelId) {
        bungeePlugin.getProxy().registerChannel(channelId);
    }

    @Override
    protected void executeCommand(Server source, String[] args) {
        bungeePlugin.getCommand().execute(new ServerCommandSender(source), args);
    }

    @Override
    protected IProxyServerManager getProxyServerManager() {
        return new BungeeProxyServerManager(bungeePlugin.getProxy());
    }

    @EventHandler
    public void handleMessage(PluginMessageEvent event) {
        if (event.getSender() instanceof Server) {
            handleMessage(event.getTag(), (Server) event.getSender(), event.getData());
        }
    }
}

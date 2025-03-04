package com.exaroton.proxy;

import com.exaroton.proxy.servers.proxy.IProxyServerManager;
import net.md_5.bungee.api.connection.Server;

public class ProxyPluginImpl extends CommonProxyPlugin<Server> {

    private final BungeePlugin bungeePlugin;
    private MessageControllerImpl messageController;

    public ProxyPluginImpl(BungeePlugin bungeePlugin) {
        this.bungeePlugin = bungeePlugin;
    }

    public MessageControllerImpl getMessageController() {
        if (messageController == null) {
            messageController = new MessageControllerImpl(bungeePlugin);
        }

        return messageController;
    }

    @Override
    protected IProxyServerManager getProxyServerManager() {
        return new BungeeProxyServerManager(bungeePlugin.getProxy());
    }
}

package com.exaroton.proxy;

import com.exaroton.proxy.servers.proxy.ProxyServerManager;

public class ProxyPluginImpl extends CommonProxyPlugin {

    private final BungeePlugin bungeePlugin;
    private BungeeMessageController messageController;

    public ProxyPluginImpl(BungeePlugin bungeePlugin) {
        this.bungeePlugin = bungeePlugin;
    }

    public BungeeMessageController getMessageController() {
        if (messageController == null) {
            messageController = new BungeeMessageController(bungeePlugin);
        }

        return messageController;
    }

    @Override
    protected ProxyServerManager getProxyServerManager() {
        return new BungeeProxyServerManager(bungeePlugin.getProxy());
    }
}

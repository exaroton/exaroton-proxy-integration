package com.exaroton.proxy;

import com.exaroton.proxy.servers.proxy.IProxyServerManager;

public class ProxyPluginImpl extends CommonProxyPlugin {

    private final BungeePlugin bungeePlugin;

    public ProxyPluginImpl(BungeePlugin bungeePlugin) {
        this.bungeePlugin = bungeePlugin;
    }

    @Override
    protected IProxyServerManager getProxyServerManager() {
        return new BungeeProxyServerManager(bungeePlugin.getProxy());
    }
}

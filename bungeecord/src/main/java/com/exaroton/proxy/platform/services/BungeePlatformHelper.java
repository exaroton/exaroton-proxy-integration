package com.exaroton.proxy.platform.services;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.file.GenericBuilder;
import com.exaroton.proxy.Constants;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * A platform helper for BungeeCord
 */
public class BungeePlatformHelper implements IPlatformHelper {
    /**
     * The BungeeCord proxy server instance
     */
    protected final ProxyServer proxyServer = ProxyServer.getInstance();
    /**
     * The plugin instance
     */
    protected final Plugin plugin = proxyServer.getPluginManager().getPlugin(Constants.PLUGIN_ID);

    @Override
    public String getPlatformName() {
        return "BungeeCord";
    }

    @Override
    public String getPluginVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public GenericBuilder<Config, FileConfig> getConfig() {
        var dataFolder = plugin.getDataFolder();
        //noinspection ResultOfMethodCallIgnored
        dataFolder.mkdirs();
        return FileConfig.builder(dataFolder.toPath().resolve("config.toml"));
    }
}

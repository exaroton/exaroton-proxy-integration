package com.exaroton.proxy;

import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.serde.ObjectDeserializer;
import com.electronwill.nightconfig.core.serde.ObjectSerializer;
import com.exaroton.api.APIException;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.Server;
import com.exaroton.proxy.commands.*;
import com.exaroton.proxy.servers.ServerCache;
import com.exaroton.proxy.servers.StatusSubscriberManager;
import com.exaroton.proxy.servers.proxy.IProxyServerManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.exaroton.proxy.components.IComponent;
import com.exaroton.proxy.components.IComponentFactory;
import com.exaroton.proxy.components.IStyle;
import com.exaroton.proxy.platform.Services;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class CommonPlugin {
    protected ExarotonClient apiClient;
    protected FileConfig configFile;
    protected Configuration config = new Configuration();
    protected ServerCache serverCache;
    protected StatusSubscriberManager statusSubscribers;

    public void init() {
        configFile = Services.platform().getConfig()
                .autoreload()
                .onAutoReload(this::onConfigLoaded)
                .autosave()
                .build();
        configFile.load();
        migrateOldConfigFields();
        onConfigLoaded(false);

        ObjectSerializer.standard().serializeFields(config, configFile);
        configFile.save();

        if (config.apiToken == null || config.apiToken.isEmpty() || config.apiToken.equals("example-token")) {
            throw new IllegalStateException("No API token provided. Please set the API token in the configuration file.");
        }

        apiClient = new ExarotonClient(config.apiToken).setUserAgent("proxy-plugin/"
                + Services.platform().getPlatformName() + "/" + Services.platform().getPluginVersion());
        serverCache = new ServerCache(apiClient);
        statusSubscribers = new StatusSubscriberManager(serverCache, getProxyServerManager());
    }

    /**
     * Find a specific server
     * @param name server name
     * @param refresh force refresh the server cache
     * @return the server if it was found or an empty optional
     * @throws APIException API error while fetching servers
     */
    public Optional<Server> findServer(String name, boolean refresh) throws APIException {
        // TODO: find server by name in proxy

        if (refresh) {
            serverCache.refresh();
        }

        return serverCache.getServer(name);
    }

    /**
     * Find a specific server and refresh the server cache
     * @param name server name
     * @return the server if it was found or an empty optional
     * @throws APIException API error while fetching servers
     */
    public Optional<Server> findServer(String name) throws APIException {
        return findServer(name, true);
    }

    /**
     * Get all available exaroton servers
     * @return all available exaroton servers
     * @throws APIException API error while fetching servers
     */
    public Collection<Server> getServers() throws APIException {
        return serverCache.getServers();
    }

    /**
     * Get the status subscriber manager
     * @return the status subscriber manager
     */
    public StatusSubscriberManager getStatusSubscribers() {
        return statusSubscribers;
    }

    /**
     * Get the proxy server manager
     * @return an implementation of IProxyServerManager for this proxy
     */
    protected abstract IProxyServerManager getProxyServerManager();

    protected void migrateOldConfigFields() {
        for (Map.Entry<String, String> entry : Map.of(
                "watch-servers", "watchServers",
                "auto-start", "autoStart",
                "auto-stop", "autoStop"
        ).entrySet()) {
            String oldKey = entry.getKey();
            String newKey = entry.getValue();

            if (configFile.get(newKey) == null) {
                configFile.set(newKey, configFile.get(oldKey));
                configFile.remove(oldKey);
            }
        }
    }

    protected void onConfigLoaded() {
        onConfigLoaded(true);
    }

    protected void onConfigLoaded(boolean log) {
        ObjectDeserializer.standard().deserializeFields(configFile, config);
        apiClient.setAPIToken(config.apiToken);

        if (log) {
            Constants.LOG.info("Reloaded configuration.");
        }
    }

    protected <
            ComponentType extends IComponent<ComponentType, StyleType, ClickEventType>,
            StyleType extends IStyle<StyleType, ClickEventType>,
            ClickEventType
            > Collection<Command<ComponentType, StyleType, ClickEventType>> getCommands(
            IComponentFactory<ComponentType, StyleType, ClickEventType> componentFactory
    ) {
        return List.of(
                new StartCommand<>(this, apiClient, componentFactory)
        );
    }

    protected <
            T,
            ComponentType extends IComponent<ComponentType, StyleType, ClickEventType>,
            StyleType extends IStyle<StyleType, ClickEventType>,
            ClickEventType
            > void registerCommands(
            CommandDispatcher<T> dispatcher,
            BuildContext<T, ComponentType> context,
            IComponentFactory<ComponentType, StyleType, ClickEventType> componentFactory
    ) {
        Constants.LOG.info("Registering command exaroton");
        var builder = LiteralArgumentBuilder.<T>literal("exaroton");

        for (var command : getCommands(componentFactory)) {
            dispatcher.register(command.build(context, builder));
        }
    }
}

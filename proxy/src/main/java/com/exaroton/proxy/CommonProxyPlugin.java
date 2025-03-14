package com.exaroton.proxy;

import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.serde.ObjectDeserializer;
import com.electronwill.nightconfig.core.serde.ObjectSerializer;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.Server;
import com.exaroton.proxy.commands.*;
import com.exaroton.proxy.platform.Services;
import com.exaroton.proxy.servers.ProxyServerManager;
import com.exaroton.proxy.servers.ServerCache;
import com.exaroton.proxy.servers.StatusSubscriberManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class CommonProxyPlugin {
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
        statusSubscribers = new StatusSubscriberManager(this, serverCache, getProxyServerManager());
    }

    /**
     * Find a specific server
     * @param name server name
     * @param refresh force refresh the server cache
     * @return the server if it was found or an empty optional
     */
    public CompletableFuture<Optional<Server>> findServer(final String name, boolean refresh) throws IOException {
        Optional<String> address = getProxyServerManager().getAddress(name);

        var refreshFuture = CompletableFuture.<Void>completedFuture(null);
        if (refresh) {
            refreshFuture = serverCache.refresh();
        }

        CompletableFuture<Optional<Server>> future = refreshFuture.thenApply(x -> Optional.empty());

        if (address.isPresent()) {
            future = findServer(address.get(), false);
        }

        return future.thenCompose(x -> {
            if (x.isPresent()) {
                return CompletableFuture.completedFuture(x);
            }

            try {
                return serverCache.getServer(name);
            } catch (IOException e) {
                return CompletableFuture.failedFuture(e);
            }
        });
    }

    /**
     * Find a specific server and refresh the server cache
     *
     * @param name server name
     * @return the server if it was found or an empty optional
     */
    public CompletableFuture<Optional<Server>> findServer(String name) throws IOException {
        return findServer(name, true);
    }

    /**
     * Get all available exaroton servers
     *
     * @return all available exaroton servers
     */
    public CompletableFuture<Collection<Server>> getServers() throws IOException {
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
    public abstract ProxyServerManager getProxyServerManager();

    /**
     * Get a list of all players currently connected to the proxy
     * @return iterable of player names
     */
    public abstract Collection<String> getPlayers();

    protected void migrateOldConfigFields() {
        for (Map.Entry<String, String> entry : Map.of(
                "watch-servers", "watchServers",
                "auto-start", "autoStartServers",
                "auto-stop", "autoStopServers"
        ).entrySet()) {
            String oldKey = entry.getKey();
            String newKey = entry.getValue();

            if (configFile.get(newKey) == null) {
                Object value = configFile.get(oldKey);
                if (value != null) {
                    configFile.set(newKey, configFile.get(oldKey));
                    configFile.remove(oldKey);
                }
            }
        }
    }

    protected void onConfigLoaded() {
        onConfigLoaded(true);
    }

    protected void onConfigLoaded(boolean log) {
        ObjectDeserializer.standard().deserializeFields(configFile, config);

        if (apiClient != null) {
            apiClient.setAPIToken(config.apiToken);
        }

        if (log) {
            Constants.LOG.info("Reloaded configuration.");
        }
    }

    protected Collection<Command<?>> getCommands()  {
        return List.of(
                new StartCommand(this),
                new StopCommand(this),
                new RestartCommand(this),
                new AddCommand(this),
                new RemoveCommand(this),
                new SwitchCommand(this),
                new TransferCommand(this)
        );
    }

    protected <T> void registerCommands(CommandDispatcher<T> dispatcher, BuildContext<T> context) {
        Constants.LOG.info("Registering command exaroton");
        var builder = LiteralArgumentBuilder.<T>literal("exaroton");

        for (var command : getCommands()) {
            dispatcher.register(command.build(context, builder));
        }
    }
}

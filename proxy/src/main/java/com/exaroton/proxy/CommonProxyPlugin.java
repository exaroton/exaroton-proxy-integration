package com.exaroton.proxy;

import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.electronwill.nightconfig.core.serde.ObjectDeserializer;
import com.electronwill.nightconfig.core.serde.ObjectSerializer;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.Server;
import com.exaroton.api.server.ServerStatus;
import com.exaroton.proxy.commands.*;
import com.exaroton.proxy.platform.Services;
import com.exaroton.proxy.servers.ProxyServerManager;
import com.exaroton.proxy.servers.ServerCache;
import com.exaroton.proxy.servers.StatusSubscriberManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Common class for the proxy plugins
 */
public abstract class CommonProxyPlugin {
    /**
     * exaroton API client
     */
    protected ExarotonClient apiClient;
    /**
     * Configuration file
     */
    protected FileConfig configFile;
    /**
     * Parsed configuration object
     */
    protected Configuration config = new Configuration();
    /**
     * Server cache
     */
    protected ServerCache serverCache;
    /**
     * Manager for server status subscribers
     */
    protected StatusSubscriberManager statusSubscribers;
    /**
     * Manager for proxy servers
     */
    protected ProxyServerManager<?> proxyServerManager;

    /**
     * Set up the plugin initially and start servers if configured. This should be called during proxy startup.
     * @return a future that completes when setup is done
     */
    public CompletableFuture<Void> setUp() {
        loadConfig();

        if (config.apiToken == null || config.apiToken.isEmpty() || config.apiToken.equals("example-token")) {
            throw new IllegalStateException("No API token provided. Please set the API token in the configuration file.");
        }

        apiClient = new ExarotonClient(config.apiToken).setUserAgent("proxy-plugin/"
                + Services.platform().getPlatformName() + "/" + Services.platform().getPluginVersion());
        serverCache = new ServerCache(apiClient);
        proxyServerManager = createProxyServerManager();
        statusSubscribers = new StatusSubscriberManager(serverCache, getProxyServerManager());

        return loadServers().thenCompose(x -> autoStart());
    }

    /**
     * Tear down the plugin and stop servers if configured. This should be called during proxy shutdown.
     * @return a future that completes when tear down is done
     */
    public CompletableFuture<Void> tearDown() {
        statusSubscribers.disconnectAll();
        return autoStop().orTimeout(1, TimeUnit.MINUTES);
    }

    /**
     * Find a specific server
     * @param name server name
     * @param refresh force refresh the server cache
     * @return the server if it was found or an empty optional
     */
    public CompletableFuture<Optional<Server>> findServer(final String name, boolean refresh) {
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

            return serverCache.getServer(name);
        });
    }

    /**
     * Find a specific server and refresh the server cache
     *
     * @param name server name
     * @return the server if it was found or an empty optional
     */
    public final CompletableFuture<Optional<Server>> findServer(String name) {
        return findServer(name, true);
    }

    /**
     * Get all available exaroton servers
     *
     * @return all available exaroton servers
     */
    public final CompletableFuture<Collection<Server>> getServers() {
        return serverCache.getServers();
    }

    /**
     * Get the status subscriber manager
     * @return the status subscriber manager
     */
    public final StatusSubscriberManager getStatusSubscribers() {
        return statusSubscribers;
    }

    /**
     * Get the proxy server manager
     * @return an implementation of IProxyServerManager for this proxy
     */
    public final ProxyServerManager<?> getProxyServerManager() {
        return proxyServerManager;
    }

    /**
     * Create a new proxy server manager
     * @return an implementation of IProxyServerManager for this proxy
     */
    protected abstract ProxyServerManager<?> createProxyServerManager();

    /**
     * Get a list of all players currently connected to the proxy
     * @return iterable of player names
     */
    public abstract Collection<String> getPlayers();

    /**
     * Initializes the configFile field
     */
    protected void initializeConfigFile() {
        if (configFile == null) {
            configFile = Services.platform().getConfig()
                    .autoreload()
                    .onAutoReload(this::onConfigLoaded)
                    .autosave()
                    .writingMode(WritingMode.REPLACE_ATOMIC)
                .build();
        }
    }

    /**
     * Loads the config, migrates old fields and updates the config file
     */
    protected void loadConfig() {
        initializeConfigFile();
        configFile.load();
        migrateOldConfigFields();
        onConfigLoaded(false);

        ObjectSerializer.standard().serializeFields(config, configFile);
        configFile.save();
    }

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

    /**
     * Load the servers from the config file to allow using their names as aliases. If the watch servers option is
     * enabled a status listener will be registered for each server that removes the server when it goes offline and
     * adds it back when it comes online.
     * @return a future that completes when all servers have been loaded
     */
    public CompletableFuture<Void> loadServers() {
        var future = getProxyServerManager().loadServers(this);
        return future.thenCompose(x -> this.watchServers());
    }

    /**
     * Watch all exaroton servers from the proxy if enabled
     * @return a future that completes when watching has started
     */
    private CompletableFuture<Void> watchServers() {
        if (!config.watchServers) {
            return CompletableFuture.completedFuture(null);
        }

        var serverManager = getProxyServerManager();
        var futures = new ArrayList<CompletableFuture<Void>>();

        for (String address : serverManager.getAddresses()) {
            futures.add(watchServer(serverManager, address)
                    .exceptionally(t -> {
                Constants.LOG.error("Failed to watch server {}: {}", address, t.getMessage(), t);
                return null;
            }));
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    /**
     * Watch a single server for status changes
     * @param serverManager the server manager to add/remove the server from
     * @param address the address of the server
     * @return A future that completes when the watching has started
     */
    private CompletableFuture<Void> watchServer(ProxyServerManager<?> serverManager, String address) {
        return findServer(address, false).thenAccept(optionalServer -> {
            if (optionalServer.isEmpty()) {
                return;
            }
            Server server = optionalServer.get();

            Constants.LOG.info("Watching server {} for status changes.", address);
            serverManager.removeServer(server);
            if (server.hasStatus(ServerStatus.ONLINE)) {
                serverManager.addServer(server);
            } else {
                Constants.LOG.info("Removing server {} from proxy because it is not online.", address);
            }

            statusSubscribers.addProxyStatusSubscriber(server);
        });
    }

    /**
     * Start servers that are configured to be started automatically
     * @return a future that completes when all start requests have been sent. This method does not wait for the
     * server to go online.
     */
    public CompletableFuture<Void> autoStart() {
        Collection<CompletableFuture<?>> futures = new HashSet<>();
        if (config.autoStartServers.enabled) {
            Constants.LOG.info("Starting servers...");
            for (var server : config.autoStartServers.servers) {
                futures.add(autoStart(server).exceptionally(t -> {
                    Constants.LOG.error("Failed to start server {}: {}", server, t.getMessage(), t);
                    return null;
                }));
            }
        }
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    /**
     * Automatically start a single server by name, address or id
     * @param query server name, address or id
     * @return A future that completes when the start request has been sent. This method does not wait for the
     * servers to go online.
     */
    private CompletableFuture<Void> autoStart(String query) {
        return findServer(query, false).thenCompose(server -> {
            if (server.isEmpty()) {
                Constants.LOG.error("Failed to start server {}: Server not found", query);
                return CompletableFuture.completedFuture(null);
            }

            if (server.get().hasStatus(ServerStatus.ONLINE)) {
                Constants.LOG.info("Server {} is already online.", query);
                getProxyServerManager().addServer(server.get());
                return CompletableFuture.completedFuture(null);
            }

            getStatusSubscribers().addProxyStatusSubscriber(server.get());

            if (server.get().hasStatus(StatusGroups.STARTING)) {
                Constants.LOG.info("Server {} is already starting.", query);
                return CompletableFuture.completedFuture(null);
            }

            try {
                return server.get().start().thenAccept(s -> Constants.LOG.info("Requested start for server {}.", query));
            } catch (IOException e) {
                return CompletableFuture.failedFuture(e);
            }
        });
    }

    /**
     * Stop servers that are configured to be stopped automatically
     * @return A future that completes when all stop requests have been sent. This method does not wait for the
     * servers to go offline.
     */
    public CompletableFuture<Void> autoStop() {
        Collection<CompletableFuture<?>> futures = new HashSet<>();
        if (config.autoStopServers.enabled) {
            Constants.LOG.info("Stopping servers...");
            for (var item : config.autoStopServers.servers) {
                futures.add(autoStop(item).exceptionally(t -> {
                    Constants.LOG.error("Failed to stop server {}: {}", item, t.getMessage(), t);
                    return null;
                }));
            }
        }
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    /**
     * Automatically stop a single server by name, address or id
     * @param query server name, address or id
     * @return A future that completes when the stop request has been sent. This method does not wait for the
     * server to go offline.
     */
    private CompletableFuture<Void> autoStop(String query) {
        return findServer(query, false).thenCompose(server -> {
            if (server.isEmpty()) {
                Constants.LOG.error("Failed to stop server {}: Server not found", query);
                return CompletableFuture.completedFuture(null);
            }

            if (server.get().hasStatus(ServerStatus.GROUP_OFFLINE)) {
                Constants.LOG.info("Server {} is already offline.", query);
                return CompletableFuture.completedFuture(null);
            }

            if (server.get().hasStatus(ServerStatus.GROUP_STOPPING)) {
                Constants.LOG.info("Server {} is already stopping.", query);
                return CompletableFuture.completedFuture(null);
            }

            try {
                return server.get().stop().thenAccept(s -> Constants.LOG.info("Requested stop for server {}.", query));
            } catch (IOException e) {
                return CompletableFuture.failedFuture(e);
            }
        });
    }

    protected Collection<Command<?>> getCommands()  {
        return List.of(
                new StartCommand(this),
                new StopCommand(this),
                new RestartCommand(this),
                new AddCommand(this),
                new RemoveCommand(this),
                new SwitchCommand(this)
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

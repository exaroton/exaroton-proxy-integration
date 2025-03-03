package com.exaroton.proxy;

import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.serde.ObjectDeserializer;
import com.electronwill.nightconfig.core.serde.ObjectSerializer;
import com.exaroton.api.APIException;
import com.exaroton.api.ExarotonClient;
import com.exaroton.api.server.Server;
import com.exaroton.proxy.commands.Command;
import com.exaroton.proxy.commands.StartCommand;
import com.exaroton.proxy.platform.Services;
import com.exaroton.proxy.servers.ServerCache;
import com.exaroton.proxy.servers.StatusSubscriberManager;
import com.exaroton.proxy.servers.proxy.IProxyServerManager;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @param <PlayerType> player type used when executing commands
 */
public abstract class CommonProxyPlugin<PlayerType> extends CommonPlugin {
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

        registerChannel(Constants.CHANNEL_ID);
    }

    protected abstract void registerChannel(@SuppressWarnings("SameParameterValue") String channelId);

    public void handleMessage(String channel, PlayerType player, byte[] data) {
        if (!channel.equalsIgnoreCase(Constants.CHANNEL_ID)) {
            return;
        }

        ByteArrayDataInput input = ByteStreams.newDataInput(data);
        int length = input.readInt();
        String[] args = new String[length];

        for (int i = 0; i < length; i++) {
            args[i] = input.readUTF();
        }

        try {
            executeCommand(player, args);
        } catch (Exception e) {
            Constants.LOG.error("An error occurred while executing a command", e);
        }
    }

    protected abstract void executeCommand(PlayerType source, String[] args);

    /**
     * Find a specific server
     * @param name server name
     * @param refresh force refresh the server cache
     * @return the server if it was found or an empty optional
     * @throws APIException API error while fetching servers
     */
    public Optional<Server> findServer(String name, boolean refresh) throws APIException {
        name = getProxyServerManager().getAddress(name).orElse(name);

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

    @Override
    protected Collection<Command<?>> getCommands()  {
        return List.of(
                new StartCommand(this, apiClient)
        );
    }
}

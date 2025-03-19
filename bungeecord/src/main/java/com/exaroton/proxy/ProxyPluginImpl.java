package com.exaroton.proxy;

import com.exaroton.proxy.servers.ProxyServerManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.stream.Collectors;

public class ProxyPluginImpl extends CommonProxyPlugin {

    private final BungeePlugin bungeePlugin;
    private BungeeMessageController messageController;

    public ProxyPluginImpl(BungeePlugin bungeePlugin) {
        this.bungeePlugin = bungeePlugin;
    }

    public BungeeMessageController getMessageController() {
        if (messageController == null) {
            messageController = new BungeeMessageController(bungeePlugin, this);
        }

        return messageController;
    }

    @Override
    public ProxyServerManager<?> createProxyServerManager() {
        return new BungeeProxyServerManager(bungeePlugin.getProxy());
    }

    @Override
    public Collection<String> getPlayers() {
        return bungeePlugin.getProxy().getPlayers().stream().map(CommandSender::getName).collect(Collectors.toList());
    }

    @Override
    protected void migrateOldConfigFields() {
        if (configFile.isEmpty()) {
            var oldPluginDir = bungeePlugin.getProxy().getPluginsFolder().toPath().resolve("ExarotonBungeePlugin");
            var oldConfigFile = oldPluginDir.resolve("config.yml").toFile();

            if (oldConfigFile.exists()) {
                Constants.LOG.info("Migrating old YAML config to TOML");
                var configProvider = ConfigurationProvider.getProvider(YamlConfiguration.class);
                try {
                    var oldConfig = configProvider.load(oldConfigFile);

                    writeConfig(oldConfig, "");

                    Files.delete(oldConfigFile.toPath());
                    try (var children = Files.list(oldPluginDir)) {
                        if (children.findAny().isPresent()) {
                            Constants.LOG.warn("Can't delete old plugin directory as it contains other files.");
                        } else {
                            Files.delete(oldPluginDir);
                        }
                    }
                } catch (IOException e) {
                    Constants.LOG.error("Failed to migrate old YAML config: {}", e.getMessage(), e);
                }
            }
        }

        super.migrateOldConfigFields();
    }

    private void writeConfig(Object value, String key) {
        if (value instanceof Configuration) {
            var config = (Configuration) value;
            for (String otherKey : config.getKeys()) {
                writeConfig(config.get(otherKey), (key.isEmpty() ? "" : (key + ".")) + otherKey);
            }
        } else {
            configFile.set(key, value);
        }
    }
}

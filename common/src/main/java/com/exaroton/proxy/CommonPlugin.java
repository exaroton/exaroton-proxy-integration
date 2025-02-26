package com.exaroton.proxy;

import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.serde.ObjectDeserializer;
import com.electronwill.nightconfig.core.serde.ObjectSerializer;
import com.exaroton.api.ExarotonClient;
import com.exaroton.proxy.commands.*;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.exaroton.proxy.components.IComponent;
import com.exaroton.proxy.components.IComponentFactory;
import com.exaroton.proxy.components.IStyle;
import com.exaroton.proxy.platform.Services;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CommonPlugin {
    protected ExarotonClient apiClient;
    protected FileConfig configFile;
    protected Configuration config = new Configuration();

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

        apiClient = new ExarotonClient(config.apiToken).setUserAgent("proxy-plugin/"
                + Services.platform().getPlatformName() + "/" + Services.platform().getPluginVersion());
    }

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
                // TODO
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

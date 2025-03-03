package com.exaroton.proxy;

import com.google.inject.Inject;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.exaroton.proxy.commands.VelocityBuildContext;
import com.exaroton.proxy.platform.Services;
import com.exaroton.proxy.platform.services.VelocityPlatformHelper;

import java.nio.file.Path;

public class VelocityPlugin extends CommonPlugin {
    static {
        Services.setClassLoader(VelocityPlugin.class.getClassLoader());
    }

    private final ProxyServer proxy;

    @Inject
    public VelocityPlugin(ProxyServer proxy, @DataDirectory Path dataDirectory) {
        this.proxy = proxy;
        VelocityPlatformHelper.init(proxy, dataDirectory);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        init();

        registerCommands();
    }

    protected void registerCommands() {
        Constants.LOG.info("Registering commands");
        var commandManager = proxy.getCommandManager();

        var commandMeta = commandManager.metaBuilder("exaroton")
                .plugin(this)
                .build();

        var context = new VelocityBuildContext();
        var builder = LiteralArgumentBuilder.<CommandSource>literal("exaroton");

        for (var command : getCommands()) {
            commandManager.register(commandMeta, new BrigadierCommand(command.build(context, builder)));
        }
    }
}

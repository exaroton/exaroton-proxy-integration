package com.exaroton.proxy;

import com.exaroton.proxy.commands.VelocityBuildContext;
import com.exaroton.proxy.platform.Services;
import com.exaroton.proxy.platform.services.VelocityPlatformHelper;
import com.exaroton.proxy.servers.ProxyServerManager;
import com.google.inject.Inject;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;

public class VelocityPlugin extends CommonProxyPlugin {
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
    public EventTask onProxyInitialization(ProxyInitializeEvent event) {
        registerCommands();
        proxy.getEventManager().register(this, new VelocityMessageController(this, proxy));
        return EventTask.resumeWhenComplete(setUp());
    }

    @Subscribe
    public EventTask onProxyShutdown(ProxyShutdownEvent event) {
        return EventTask.resumeWhenComplete(tearDown());
    }

    protected void registerCommands() {
        Constants.LOG.info("Registering commands");
        var commandManager = proxy.getCommandManager();

        var commandMeta = commandManager.metaBuilder("exaroton")
                .plugin(this)
                .build();

        var context = new VelocityBuildContext(this);
        var builder = LiteralArgumentBuilder.<CommandSource>literal("exaroton");

        for (var command : getCommands()) {
            commandManager.register(commandMeta, new BrigadierCommand(command.build(context, builder)));
        }
    }

    @Override
    public ProxyServerManager<?> createProxyServerManager() {
        return new VelocityProxyServerManager(proxy);
    }

    @Override
    public Collection<String> getPlayers() {

        return proxy.getAllPlayers().stream().map(Player::getUsername).collect(Collectors.toList());
    }
}

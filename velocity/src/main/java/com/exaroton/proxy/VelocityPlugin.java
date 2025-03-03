package com.exaroton.proxy;

import com.exaroton.proxy.servers.proxy.IProxyServerManager;
import com.google.inject.Inject;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.exaroton.proxy.commands.VelocityBuildContext;
import com.exaroton.proxy.platform.Services;
import com.exaroton.proxy.platform.services.VelocityPlatformHelper;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;

import java.nio.file.Path;

public class VelocityPlugin extends CommonProxyPlugin<ServerConnection> {
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

    @Override
    protected void registerChannel(String channelId) {
        proxy.getChannelRegistrar().register(MinecraftChannelIdentifier.from(channelId));
    }

    @Override
    protected void executeCommand(ServerConnection source, String[] args) {
        proxy.getCommandManager().executeAsync(new ServerConnectionCommandSource(source), "exaroton" + " " + String.join(" ", args));
    }

    @Override
    protected IProxyServerManager getProxyServerManager() {
        return new VelocityProxyServerManager(proxy);
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        if (event.getSource() instanceof ServerConnection) {
            handleMessage(event.getIdentifier().getId(), (ServerConnection) event.getSource(), event.getData());
        }
    }
}

package com.exaroton.proxy;

import com.exaroton.proxy.commands.CommandSourceAccessor;
import com.exaroton.proxy.network.ProxyMessageController;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;

import java.util.concurrent.CompletableFuture;

public class MessageControllerImpl extends ProxyMessageController<ServerConnection> {
    private static final ChannelIdentifier CHANNEL_ID = MinecraftChannelIdentifier.from(Constants.CHANNEL_ID);
    private final ProxyServer proxy;

    public MessageControllerImpl(ProxyServer proxy) {
        super();
        this.proxy = proxy;
    }

    @Override
    protected void send(ServerConnection target, byte[] data) {
        target.sendPluginMessage(CHANNEL_ID, data);
    }

    @Override
    protected void registerChannel() {
        proxy.getChannelRegistrar().register(CHANNEL_ID);
    }

    @Override
    protected CompletableFuture<?> executeCommand(CommandSourceAccessor source, String[] args) {
        var cmdLine = "exaroton" + " " + String.join(" ", args);
        return proxy.getCommandManager().executeAsync(new ServerConnectionCommandSource(source), cmdLine);
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        if (event.getSource() instanceof ServerConnection) {
            handleMessage((ServerConnection) event.getSource(), event.getIdentifier().getId(), event.getData());
        }
    }
}

package com.exaroton.proxy;

import com.exaroton.proxy.commands.CommandSourceAccessor;
import com.exaroton.proxy.network.ProxyMessageController;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.CompletableFuture;

public class BungeeMessageController extends ProxyMessageController<Server> implements Listener {
    private final BungeePlugin bungeePlugin;

    public BungeeMessageController(BungeePlugin bungeePlugin) {
        this.bungeePlugin = bungeePlugin;
        registerChannel();
    }

    @Override
    protected void send(Server target, byte[] data) {
        target.sendData(Constants.CHANNEL_ID, data);
    }

    @Override
    protected void registerChannel() {
        bungeePlugin.getProxy().registerChannel(Constants.CHANNEL_ID);
    }

    @Override
    protected CompletableFuture<?> executeCommand(CommandSourceAccessor source, String[] args) {
        return CompletableFuture.runAsync(() -> bungeePlugin.getCommand().execute(new CommandSourceCommandSender(source), args));
    }

    @EventHandler
    public void handleMessage(PluginMessageEvent event) {
        if (event.getSender() instanceof Server) {
            handleMessage((Server) event.getSender(), event.getTag(), event.getData());
        }
    }
}

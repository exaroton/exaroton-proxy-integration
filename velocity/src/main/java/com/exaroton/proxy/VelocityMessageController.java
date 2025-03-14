package com.exaroton.proxy;

import com.exaroton.proxy.commands.CommandSourceAccessor;
import com.exaroton.proxy.network.ProxyMessageController;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;

public class VelocityMessageController extends ProxyMessageController<ServerConnection, Player, VelocityPlugin> {
    private static final ChannelIdentifier CHANNEL_ID = MinecraftChannelIdentifier.from(Constants.CHANNEL_ID);
    private final ProxyServer proxy;

    public VelocityMessageController(VelocityPlugin plugin, ProxyServer proxy) {
        super(plugin);
        this.proxy = proxy;
        registerChannel();
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
    protected void executeCommand(CommandSourceAccessor source, String[] args) {
        var cmdLine = "exaroton" + " " + String.join(" ", args);
        proxy.getCommandManager().executeAsync(new ServerConnectionCommandSource(source), cmdLine);
    }

    @Override
    protected String getPlayerName(Player player) {
        return player.getUsername();
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        if (event.getSource() instanceof ServerConnection source) {
            handleMessage(source, event.getIdentifier().getId(), event.getData(), source.getPlayer());
        }
    }
}

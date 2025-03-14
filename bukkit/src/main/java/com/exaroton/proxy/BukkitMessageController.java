package com.exaroton.proxy;

import com.exaroton.proxy.network.*;
import com.exaroton.proxy.network.id.NetworkId;
import com.exaroton.proxy.network.messages.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class BukkitMessageController extends MessageController<Player> implements PluginMessageListener {
    private final BukkitPlugin plugin;
    private final HashMap<NetworkId, CommandSender> senders = new HashMap<>();

    public BukkitMessageController(BukkitPlugin plugin) {
        this.plugin = plugin;
        registerChannel();
    }

    public void executeCommand(CommandSender sender, String[] args) {
        String playerName = null;
        if (senders instanceof Player) {
            playerName = sender.getName();
        }

        var message = new ExecuteCommandMessage(playerName, args);
        var player = plugin.getServer().getOnlinePlayers().stream().findFirst();

        if (player.isEmpty()) {
            Constants.LOG.error("Executing exaroton commands on the proxy is only possible if there are players online");
            return;
        }

        senders.put(message.getCommandExecutionId(), sender);
        send(player.get(), message);
    }

    @Override
    protected void send(Player target, byte[] data) {
        target.sendPluginMessage(plugin, Constants.CHANNEL_ID, data);
    }

    @Override
    protected void handleMessage(Player origin, Message<?> message) {
        var source = senders.get(message.getCommandExecutionId());

        if (source == null) {
            Constants.LOG.error("Received message for unknown command execution id: {}", message.getCommandExecutionId());
            return;
        }

        switch (message) {
            case PermissionRequestMessage request -> {
                var result = source.hasPermission(request.getPermission());
                var response = new PermissionResponseMessage(request.getCommandExecutionId(), request.getRequestId(), result);
                send(origin, response);
            }
            case TextComponentMessage textComponentMessage ->
                    plugin.audience(source).sendMessage(textComponentMessage.getComponent());
            case TransferPlayersMessage transferPlayersMessage -> {
                for (String player : transferPlayersMessage.getPlayers()) {
                    var target = plugin.getServer().getPlayerExact(player);
                    if (target == null) {
                        Constants.LOG.error("Player {} not found", player);
                        continue;
                    }

                    Constants.LOG.info("Transferring player {} to server {}", player, transferPlayersMessage.getServer());
                    // TODO: Send bungee message?
                    // or send our own message to the proxy?
                }
            }
            case FreeExecutionIdMessage freeExecutionIdMessage -> senders.remove(freeExecutionIdMessage.getCommandExecutionId());
            default -> Constants.LOG.error("Unknown message type: {}", message.getType());
        }
    }

    @Override
    protected void registerChannel() {
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, Constants.CHANNEL_ID, this);
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, Constants.CHANNEL_ID);
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
        handleMessage(player, channel, message);
    }
}

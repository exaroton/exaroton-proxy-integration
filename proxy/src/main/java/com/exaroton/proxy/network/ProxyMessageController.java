package com.exaroton.proxy.network;

import com.exaroton.proxy.CommonProxyPlugin;
import com.exaroton.proxy.Constants;
import com.exaroton.proxy.commands.CommandSourceAccessor;
import com.exaroton.proxy.commands.PluginMessageCommandSourceAccessor;
import com.exaroton.proxy.network.id.CommandExecutionId;
import com.exaroton.proxy.network.id.PermissionRequestId;
import com.exaroton.proxy.network.messages.*;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * A controller for handling plugin messages on a proxy
 *
 * @param <ServerConnection> server connection type to send/receive messages from/to
 */
public abstract class ProxyMessageController<
        ServerConnection,
        PlayerConnection,
        Common extends CommonProxyPlugin
        > extends MessageController<ServerConnection, PlayerConnection> {
    protected final Common common;

    /**
     * A map of permission checks that are waiting for a response
     */
    private final Map<PermissionRequestId, CompletableFuture<PermissionResponseMessage>> waitingPermissionChecks = new HashMap<>();

    protected ProxyMessageController(Common common) {
        this.common = common;
    }

    @Override
    protected void handleMessage(ServerConnection origin, Message<?> message, PlayerConnection player) {
        if (message instanceof ExecuteCommandMessage) {
            ExecuteCommandMessage executeCommandMessage = (ExecuteCommandMessage) message;
            var source = new PluginMessageCommandSourceAccessor<>(
                    this,
                    origin,
                    executeCommandMessage.getPlayerName().orElse(null),
                    message.getCommandExecutionId()
            );
            executeCommand(source, executeCommandMessage.getArgs());
        } else if (message instanceof PermissionResponseMessage) {
            PermissionResponseMessage response = (PermissionResponseMessage) message;
            CompletableFuture<PermissionResponseMessage> future = waitingPermissionChecks.remove(response.getRequestId());
            if (future != null) {
                future.complete(response);
            } else {
                Constants.LOG.error("Received permission response for unknown request: {}", response.getCommandExecutionId());
            }
        } else if (message instanceof TransferPlayerS2PMessage) {
            TransferPlayerS2PMessage transferPlayerS2PMessage = (TransferPlayerS2PMessage) message;
            try {
                common.findServer(transferPlayerS2PMessage.getServerId()).thenAccept(server -> {
                    if (server.isEmpty()) {
                        Constants.LOG.error("Failed to transfer player to server: Server not found: {}", transferPlayerS2PMessage.getServerId());
                        return;
                    }

                    common.getProxyServerManager().transferPlayer(server.get(), getPlayerName(player));
                }).exceptionally(t -> {
                    Constants.LOG.error("Failed to transfer player to server: {}", transferPlayerS2PMessage.getServerId(), t);
                    return null;
                });
            } catch (IOException e) {
                Constants.LOG.error("Failed to transfer player to server: {}", transferPlayerS2PMessage.getServerId(), e);
            }
        } else {
            Constants.LOG.error("Unknown message type: {}", message.getType());
        }
    }

    public CompletableFuture<Boolean> hasPermission(ServerConnection source, CommandExecutionId id, String permission) {
        PermissionRequestMessage message = new PermissionRequestMessage(id, permission);
        CompletableFuture<PermissionResponseMessage> future = new CompletableFuture<>();
        waitingPermissionChecks.put(message.getRequestId(), future);
        send(source, message);
        return future
                .thenApply(PermissionResponseMessage::getResult)
                .orTimeout(3, TimeUnit.SECONDS)
                .whenComplete((result, error) -> waitingPermissionChecks.remove(message.getRequestId()));
    }

    public void sendTextComponent(ServerConnection serverConnection, CommandExecutionId id, @NotNull Component message) {
        TextComponentMessage textComponentMessage = new TextComponentMessage(id, message);
        send(serverConnection, textComponentMessage);
    }

    public void freeExecutionId(ServerConnection serverConnection, CommandExecutionId id) {
        send(serverConnection, new FreeExecutionIdMessage(id));
    }

    public void transferPlayers(ServerConnection serverConnection, CommandExecutionId id, String serverId, Set<String> playerNames) {
        send(serverConnection, new TransferPlayersP2SMessage(id, serverId, playerNames.toArray(String[]::new)));
    }

    protected abstract void executeCommand(CommandSourceAccessor source, String[] args);

    protected abstract String getPlayerName(PlayerConnection player);

}

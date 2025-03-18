package com.exaroton.proxy.network;

import com.exaroton.proxy.CommonProxyPlugin;
import com.exaroton.proxy.Constants;
import com.exaroton.proxy.commands.CommandSourceAccessor;
import com.exaroton.proxy.commands.PluginMessageCommandSourceAccessor;
import com.exaroton.proxy.network.id.CommandExecutionId;
import com.exaroton.proxy.network.id.FilterPlayersRequestId;
import com.exaroton.proxy.network.id.PermissionRequestId;
import com.exaroton.proxy.network.messages.*;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * A controller for handling plugin messages on a proxy
 *
 * @param <ServerConnection> server connection type to send/receive messages from/to
 * @param <PlayerConnection> player connection type used to get player names
 * @param <Common> Type of the common plugin
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
    private final Map<PermissionRequestId, WaitingRequest<PermissionResponseMessage>> waitingPermissionChecks = new HashMap<>();

    /**
     * A map of permission checks that are waiting for a response
     */
    private final Map<FilterPlayersRequestId, WaitingRequest<FilterPlayersResponse>> waitingPlayerFilters = new HashMap<>();

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
            var request = waitingPermissionChecks.remove(response.getRequestId());
            if (request != null) {
                request.getFuture().complete(response);
            } else {
                Constants.LOG.error("Received permission response for unknown request: {}", response.getCommandExecutionId());
            }
        } else if (message instanceof FilterPlayersResponse) {
            FilterPlayersResponse response = (FilterPlayersResponse) message;
            var request = waitingPlayerFilters.remove(response.getRequestId());
            if (request != null) {
                request.getFuture().complete(response);
            } else {
                Constants.LOG.error("Received player filter response for unknown request: {}", response.getCommandExecutionId());
            }
        } else if (message instanceof TransferPlayerS2PMessage) {
            TransferPlayerS2PMessage transferPlayerS2PMessage = (TransferPlayerS2PMessage) message;
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
        } else {
            Constants.LOG.error("Unknown message type: {}", message.getType().getSlug());
        }
    }

    public CompletableFuture<Boolean> hasPermission(ServerConnection source, CommandExecutionId id, String permission) {
        PermissionRequestMessage message = new PermissionRequestMessage(id, permission);
        var response = new WaitingRequest<PermissionResponseMessage>(id);
        waitingPermissionChecks.put(message.getRequestId(), response);
        send(source, message);
        return response.getFuture()
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
        clearExecutionId(waitingPermissionChecks, id);
        clearExecutionId(waitingPlayerFilters, id);
    }

    public void transferPlayers(ServerConnection serverConnection, CommandExecutionId id, String serverId, Set<String> playerNames) {
        send(serverConnection, new TransferPlayersP2SMessage(id, serverId, playerNames));
    }

    public CompletableFuture<Set<String>> filterPlayers(ServerConnection serverConnection, CommandExecutionId id, Set<String> playerNames) {
        var message = new FilterPlayersRequest(id, playerNames);
        var request = new WaitingRequest<FilterPlayersResponse>(id);
        waitingPlayerFilters.put(message.getRequestId(), request);
        send(serverConnection, message);
        return request.getFuture()
                .thenApply(FilterPlayersResponse::getPlayerNames)
                .orTimeout(3, TimeUnit.SECONDS)
                .whenComplete((result, error) -> waitingPlayerFilters.remove(message.getRequestId()));
    }

    /**
     * Execute a command on the proxy
     * @param source the source of the command
     * @param args the command arguments
     */
    protected abstract void executeCommand(CommandSourceAccessor source, String[] args);

    /**
     * Get the name of a player
     * @param player the player connection
     * @return the player name
     */
    protected abstract String getPlayerName(PlayerConnection player);

    private <K, V> void clearExecutionId(Map<K, WaitingRequest<V>> map, CommandExecutionId id) {
        for (Map.Entry<K, WaitingRequest<V>> entry : new ArrayList<>(map.entrySet())) {
            if (entry.getValue().getCommandExecutionId() == id) {
                entry.getValue().getFuture().completeExceptionally(new IllegalStateException("Execution ID was freed"));
                map.remove(entry.getKey());
            }
        }
    }

    protected static class WaitingRequest<T> {
        private final CommandExecutionId id;
        private final CompletableFuture<T> future;

        protected WaitingRequest(CommandExecutionId id) {
            this.id = id;
            this.future = new CompletableFuture<>();
        }

        public CommandExecutionId getCommandExecutionId() {
            return id;
        }

        public CompletableFuture<T> getFuture() {
            return future;
        }
    }
}

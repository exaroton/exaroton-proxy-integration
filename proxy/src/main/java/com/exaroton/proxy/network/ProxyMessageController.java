package com.exaroton.proxy.network;

import com.exaroton.proxy.Constants;
import com.exaroton.proxy.commands.CommandSourceAccessor;
import com.exaroton.proxy.commands.PluginMessageCommandSourceAccessor;
import com.exaroton.proxy.network.id.CommandExecutionId;
import com.exaroton.proxy.network.id.PermissionRequestId;
import com.exaroton.proxy.network.messages.*;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * A controller for handling plugin messages on a proxy
 * @param <Server> server connection type to send/receive messages from/to
 */
public abstract class ProxyMessageController<Server> extends MessageController<Server> {
    /**
     * A map of permission checks that are waiting for a response
     */
    private final Map<PermissionRequestId, CompletableFuture<PermissionResponseMessage>> waitingPermissionChecks = new HashMap<>();

    @Override
    protected void handleMessage(Server origin, Message<?> message) {
        if (message instanceof ExecuteCommandMessage) {
            ExecuteCommandMessage executeCommandMessage = (ExecuteCommandMessage) message;
            var source = new PluginMessageCommandSourceAccessor<>(this, origin, message.getCommandExecutionId());
            executeCommand(source, executeCommandMessage.getArgs());
        } else if (message instanceof PermissionResponseMessage) {
            PermissionResponseMessage response = (PermissionResponseMessage) message;
            CompletableFuture<PermissionResponseMessage> future = waitingPermissionChecks.remove(response.getRequestId());
            if (future != null) {
                future.complete(response);
            } else {
                Constants.LOG.error("Received permission response for unknown request: {}", response.getCommandExecutionId());
            }
        } else {
            Constants.LOG.error("Unknown message type: {}", message.getType());
        }
    }

    public CompletableFuture<Boolean> hasPermission(Server source, CommandExecutionId id, String permission) {
        PermissionRequestMessage message = new PermissionRequestMessage(id, permission);
        CompletableFuture<PermissionResponseMessage> future = new CompletableFuture<>();
        waitingPermissionChecks.put(message.getRequestId(), future);
        send(source, message);
        return future
                .thenApply(PermissionResponseMessage::getResult)
                .orTimeout(3, TimeUnit.SECONDS)
                .whenComplete((result, error) -> waitingPermissionChecks.remove(message.getRequestId()));
    }

    public void sendTextComponent(Server server, CommandExecutionId id, @NotNull Component message) {
        TextComponentMessage textComponentMessage = new TextComponentMessage(id, message);
        send(server, textComponentMessage);
    }

    public void freeExecutionId(Server server, CommandExecutionId id) {
        send(server, new FreeExecutionIdMessage(id));
    }

    protected abstract void executeCommand(CommandSourceAccessor source, String[] args);
}

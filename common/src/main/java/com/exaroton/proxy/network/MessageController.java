package com.exaroton.proxy.network;

import com.exaroton.proxy.Constants;
import com.google.common.io.ByteStreams;

/**
 * A controller for sending and receiving plugin messages
 * @param <Connection> backend server connection type
 * @param <Player> player connection type
 */
public abstract class MessageController<Connection, Player> {
    /**
     * Send a message to the target
     * @param target message receiver
     * @param message message to send
     */
    protected final void send(Connection target, Message<?> message) {
        send(target, message.serialize());
    }

    /**
     * Send a message to the target
     * @param target message receiver
     * @param data message data
     */
    protected abstract void send(Connection target, byte[] data);

    /**
     * Handle a message
     * @param origin message sender
     * @param message message to handle
     * @param player player the message was sent alongside
     */
    protected abstract void handleMessage(Connection origin, Message<?> message, Player player);

    /**
     * Handle a message
     * @param origin message sender
     * @param channel channel id
     * @param data message data
     * @param player player the message was sent alongside
     */
    protected final void handleMessage(Connection origin, String channel, byte[] data, Player player) {
        if (!channel.equalsIgnoreCase(Constants.CHANNEL_ID)) {
            return;
        }

        handleMessage(origin, MessageType.deserialize(ByteStreams.newDataInput(data)), player);
    }

    /**
     * Register the channel
     * @see Constants#CHANNEL_ID
     */
    protected abstract void registerChannel();
}

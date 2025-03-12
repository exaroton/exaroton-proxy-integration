package com.exaroton.proxy.network;

import com.exaroton.proxy.Constants;
import com.google.common.io.ByteStreams;

/**
 * A controller for sending and receiving plugin messages
 * @param <Connection> connection type
 */
public abstract class MessageController<Connection> {
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
     */
    protected abstract void handleMessage(Connection origin, Message<?> message);

    /**
     * Handle a message
     * @param origin message sender
     * @param channel channel id
     * @param data message data
     */
    protected final void handleMessage(Connection origin, String channel, byte[] data) {
        if (!channel.equalsIgnoreCase(Constants.CHANNEL_ID)) {
            return;
        }

        handleMessage(origin, MessageType.deserialize(ByteStreams.newDataInput(data)));
    }

    /**
     * Register the channel
     * @see Constants#CHANNEL_ID
     */
    protected abstract void registerChannel();
}

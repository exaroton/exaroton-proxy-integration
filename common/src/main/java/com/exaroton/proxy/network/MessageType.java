package com.exaroton.proxy.network;

import com.exaroton.proxy.network.messages.*;
import com.google.common.io.ByteArrayDataInput;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class MessageType<T extends Message<T>> {
    private static final Map<String, MessageType<?>> BY_SLUG = new HashMap<>();
    public static final MessageType<ExecuteCommandMessage> EXECUTE_COMMAND = new MessageType<>("execute_command", ExecuteCommandMessage::new);
    public static final MessageType<PermissionRequestMessage> PERMISSION_REQUEST = new MessageType<>("permission_request", PermissionRequestMessage::new);
    public static final MessageType<PermissionResponseMessage> PERMISSION_RESPONSE = new MessageType<>("permission_response", PermissionResponseMessage::new);
    public static final MessageType<TextComponentMessage> TEXT_COMPONENT = new MessageType<>("text_component", TextComponentMessage::new);
    public static final MessageType<TransferPlayersMessage> TRANSFER_PLAYERS = new MessageType<>("transfer_players", TransferPlayersMessage::new);

    private final String slug;
    private final Function<ByteArrayDataInput, T> deserializer;

    public static MessageType<?> bySlug(String slug) {
        return BY_SLUG.get(slug);
    }

    public static Message<?> deserialize(ByteArrayDataInput input) {
        String slug = input.readUTF();
        MessageType<?> type = bySlug(slug);
        if (type == null) {
            throw new IllegalArgumentException("Unknown message type: " + slug);
        }
        return type.deserializer.apply(input);
    }

    private MessageType(String slug, Function<ByteArrayDataInput, T> deserializer) {
        this.slug = slug;
        this.deserializer = deserializer;
        BY_SLUG.put(slug, this);
    }

    /**
     * Unique slug for this type, must consist only of [a-z0-9/._-] and should not change between versions
     * @return slug
     */
    public String getSlug() {
        return slug;
    }
}

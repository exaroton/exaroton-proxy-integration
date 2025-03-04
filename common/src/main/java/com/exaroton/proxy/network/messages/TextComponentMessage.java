package com.exaroton.proxy.network.messages;

import com.exaroton.proxy.network.Message;
import com.exaroton.proxy.network.MessageType;
import com.exaroton.proxy.network.id.CommandExecutionId;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;

public class TextComponentMessage extends Message<TextComponentMessage> {
    private static final JSONComponentSerializer COMPONENT_SERIALIZER = JSONComponentSerializer.json();
    private final Component component;

    public TextComponentMessage(CommandExecutionId id, Component component) {
        super(id);
        this.component = component;
    }

    public TextComponentMessage(ByteArrayDataInput input) {
        super(input);
        this.component = COMPONENT_SERIALIZER.deserialize(input.readUTF());
    }

    @Override
    public MessageType<TextComponentMessage> getType() {
        return MessageType.TEXT_COMPONENT;
    }

    @Override
    public void serialize(ByteArrayDataOutput output) {
        output.writeUTF(COMPONENT_SERIALIZER.serialize(component));
    }

    public Component getComponent() {
        return component;
    }
}

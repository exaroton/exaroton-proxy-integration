package com.exaroton.proxy.network;

import com.exaroton.proxy.network.id.CommandExecutionId;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public abstract class Message<This extends Message<This>> {
    private final CommandExecutionId commandExecutionId;

    public Message(CommandExecutionId id) {
        this.commandExecutionId = id;
    }

    public Message(ByteArrayDataInput input) {
        this.commandExecutionId = new CommandExecutionId(input);
    }

    public abstract MessageType<This> getType();

    public byte[] serialize() {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF(getType().getSlug());
        commandExecutionId.serialize(output);
        serialize(output);
        return output.toByteArray();
    }

    protected abstract void serialize(ByteArrayDataOutput output);

    public CommandExecutionId getCommandExecutionId() {
        return commandExecutionId;
    }
}

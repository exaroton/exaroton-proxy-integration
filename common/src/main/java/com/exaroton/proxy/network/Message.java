package com.exaroton.proxy.network;

import com.exaroton.proxy.network.id.CommandExecutionId;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.util.HashSet;
import java.util.Set;

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

    protected void serializeStringSet(ByteArrayDataOutput output, Set<String> set) {
        output.writeInt(set.size());
        for (String string : set) {
            output.writeUTF(string);
        }
    }

    protected Set<String> deserializeStringSet(ByteArrayDataInput input) {
        int size = input.readInt();
        Set<String> set = new HashSet<>();
        for (int i = 0; i < size; i++) {
            set.add(input.readUTF());
        }
        return set;
    }

    public CommandExecutionId getCommandExecutionId() {
        return commandExecutionId;
    }
}

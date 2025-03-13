package com.exaroton.proxy.network.messages;

import com.exaroton.proxy.network.Message;
import com.exaroton.proxy.network.MessageType;
import com.exaroton.proxy.network.id.CommandExecutionId;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ExecuteCommandMessage extends Message<ExecuteCommandMessage> {
    private final @NotNull String playerName;
    private final String[] args;

    public ExecuteCommandMessage(@Nullable String playerName, String[] args) {
        super(new CommandExecutionId());
        this.playerName = playerName == null ? "" : playerName;
        this.args = args;
    }

    public ExecuteCommandMessage(ByteArrayDataInput input) {
        super(input);
        this.playerName = input.readUTF();

        int length = input.readInt();
        this.args = new String[length];
        for (int i = 0; i < length; i++) {
            this.args[i] = input.readUTF();
        }
    }

    @Override
    public MessageType<ExecuteCommandMessage> getType() {
        return MessageType.EXECUTE_COMMAND;
    }

    @Override
    public void serialize(ByteArrayDataOutput output) {
        output.writeInt(args.length);
        for (String arg : args) {
            output.writeUTF(arg);
        }
    }

    public Optional<String> getPlayerName() {
        if (playerName.isBlank()) {
            return Optional.empty();
        }

        return Optional.of(playerName);
    }

    public String[] getArgs() {
        return args;
    }
}

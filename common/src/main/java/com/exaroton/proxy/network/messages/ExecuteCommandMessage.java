package com.exaroton.proxy.network.messages;

import com.exaroton.proxy.network.Message;
import com.exaroton.proxy.network.MessageType;
import com.exaroton.proxy.network.id.CommandExecutionId;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public class ExecuteCommandMessage extends Message<ExecuteCommandMessage> {
    private final String[] args;

    public ExecuteCommandMessage(String[] args) {
        super(new CommandExecutionId());
        this.args = args;
    }

    public ExecuteCommandMessage(ByteArrayDataInput input) {
        super(input);
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

    public String[] getArgs() {
        return args;
    }
}

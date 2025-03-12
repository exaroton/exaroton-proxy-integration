package com.exaroton.proxy.network.messages;

import com.exaroton.proxy.network.Message;
import com.exaroton.proxy.network.MessageType;
import com.exaroton.proxy.network.id.CommandExecutionId;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public class FreeExecutionIdMessage extends Message<FreeExecutionIdMessage> {
    public FreeExecutionIdMessage(CommandExecutionId id) {
        super(id);
    }

    public FreeExecutionIdMessage(ByteArrayDataInput input) {
        super(input);
    }

    @Override
    public MessageType<FreeExecutionIdMessage> getType() {
        return MessageType.FREE_EXECUTION_ID;
    }

    @Override
    protected void serialize(ByteArrayDataOutput output) {

    }
}

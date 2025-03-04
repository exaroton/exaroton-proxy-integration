package com.exaroton.proxy.network.messages;

import com.exaroton.proxy.network.Message;
import com.exaroton.proxy.network.MessageType;
import com.exaroton.proxy.network.id.CommandExecutionId;
import com.exaroton.proxy.network.id.NetworkId;
import com.exaroton.proxy.network.id.PermissionRequestId;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public class PermissionResponseMessage extends Message<PermissionResponseMessage> {
    private final PermissionRequestId requestId;
    private final boolean result;

    public PermissionResponseMessage(CommandExecutionId commandExecutionId, PermissionRequestId requestId, boolean result) {
        super(commandExecutionId);
        this.requestId = requestId;
        this.result = result;
    }

    public PermissionResponseMessage(ByteArrayDataInput input) {
        super(input);
        this.requestId = new PermissionRequestId(input);
        this.result = input.readBoolean();
    }

    @Override
    public MessageType<PermissionResponseMessage> getType() {
        return MessageType.PERMISSION_RESPONSE;
    }

    @Override
    public void serialize(ByteArrayDataOutput output) {
        requestId.serialize(output);
        output.writeBoolean(result);
    }

    public PermissionRequestId getRequestId() {
        return requestId;
    }

    public boolean getResult() {
        return result;
    }
}

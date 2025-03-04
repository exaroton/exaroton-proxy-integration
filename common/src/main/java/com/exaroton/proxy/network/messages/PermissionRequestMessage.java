package com.exaroton.proxy.network.messages;

import com.exaroton.proxy.network.Message;
import com.exaroton.proxy.network.MessageType;
import com.exaroton.proxy.network.id.CommandExecutionId;
import com.exaroton.proxy.network.id.NetworkId;
import com.exaroton.proxy.network.id.PermissionRequestId;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public class PermissionRequestMessage extends Message<PermissionRequestMessage> {
    private final PermissionRequestId requestId;
    private final String permission;

    public PermissionRequestMessage(CommandExecutionId commandExecutionId, String permission) {
        super(commandExecutionId);
        this.requestId = new PermissionRequestId();
        this.permission = permission;
    }

    public PermissionRequestMessage(ByteArrayDataInput input) {
        super(input);
        this.requestId = new PermissionRequestId(input);
        this.permission = input.readUTF();
    }

    @Override
    public MessageType<PermissionRequestMessage> getType() {
        return MessageType.PERMISSION_REQUEST;
    }

    @Override
    public void serialize(ByteArrayDataOutput output) {
        requestId.serialize(output);
        output.writeUTF(permission);
    }

    public PermissionRequestId getRequestId() {
        return requestId;
    }

    public String getPermission() {
        return permission;
    }
}

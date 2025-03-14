package com.exaroton.proxy.network.messages;

import com.exaroton.proxy.network.Message;
import com.exaroton.proxy.network.MessageType;
import com.exaroton.proxy.network.id.CommandExecutionId;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

/**
 * Transfer the player this message was sent with to another server
 */
public class TransferPlayerS2PMessage extends Message<TransferPlayerS2PMessage> {
    private final String serverId;

    public TransferPlayerS2PMessage(CommandExecutionId id, String serverId) {
        super(id);
        this.serverId = serverId;
    }

    public TransferPlayerS2PMessage(ByteArrayDataInput input) {
        super(input);
        this.serverId = input.readUTF();
    }

    @Override
    public MessageType<TransferPlayerS2PMessage> getType() {
        return MessageType.TRANSFER_PLAYER_S2P;
    }

    @Override
    protected void serialize(ByteArrayDataOutput output) {
        output.writeUTF(serverId);
    }

    public String getServerId() {
        return serverId;
    }
}

package com.exaroton.proxy.network.messages;

import com.exaroton.proxy.network.Message;
import com.exaroton.proxy.network.MessageType;
import com.exaroton.proxy.network.id.CommandExecutionId;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import java.util.Set;

public class TransferPlayersP2SMessage extends Message<TransferPlayersP2SMessage> {
    private final String serverId;
    private final Set<String> players;

    public TransferPlayersP2SMessage(CommandExecutionId id, String serverId, Set<String> players) {
        super(id);
        this.serverId = serverId;
        this.players = players;
    }

    public TransferPlayersP2SMessage(ByteArrayDataInput input) {
        super(input);
        this.serverId = input.readUTF();
        this.players = deserializeStringSet(input);
    }

    @Override
    public MessageType<TransferPlayersP2SMessage> getType() {
        return MessageType.TRANSFER_PLAYERS_P2S;
    }

    @Override
    protected void serialize(ByteArrayDataOutput output) {
        output.writeUTF(serverId);
        serializeStringSet(output, players);
    }

    public String getServerId() {
        return serverId;
    }

    public Set<String> getPlayers() {
        return players;
    }
}

package com.exaroton.proxy.network.messages;

import com.exaroton.proxy.network.Message;
import com.exaroton.proxy.network.MessageType;
import com.exaroton.proxy.network.id.CommandExecutionId;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public class TransferPlayersP2SMessage extends Message<TransferPlayersP2SMessage> {
    private final String serverId;
    private final String[] players;

    public TransferPlayersP2SMessage(CommandExecutionId id, String serverId, String[] players) {
        super(id);
        this.serverId = serverId;
        this.players = players;
    }

    public TransferPlayersP2SMessage(ByteArrayDataInput input) {
        super(input);
        this.serverId = input.readUTF();
        this.players = new String[input.readInt()];
        for (int i = 0; i < this.players.length; i++) {
            this.players[i] = input.readUTF();
        }
    }

    @Override
    public MessageType<TransferPlayersP2SMessage> getType() {
        return MessageType.TRANSFER_PLAYERS_P2S;
    }

    @Override
    protected void serialize(ByteArrayDataOutput output) {
        output.writeUTF(serverId);
        output.writeInt(players.length);
        for (String player : players) {
            output.writeUTF(player);
        }
    }

    public String getServerId() {
        return serverId;
    }

    public String[] getPlayers() {
        return players;
    }
}

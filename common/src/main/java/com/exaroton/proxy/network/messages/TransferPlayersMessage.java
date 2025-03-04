package com.exaroton.proxy.network.messages;

import com.exaroton.proxy.network.Message;
import com.exaroton.proxy.network.MessageType;
import com.exaroton.proxy.network.id.CommandExecutionId;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public class TransferPlayersMessage extends Message<TransferPlayersMessage> {
    private final String server;
    private final String[] players;

    public TransferPlayersMessage(CommandExecutionId id, String server, String[] players) {
        super(id);
        this.server = server;
        this.players = players;
    }

    public TransferPlayersMessage(ByteArrayDataInput input) {
        super(input);
        this.server = input.readUTF();
        this.players = new String[input.readInt()];
        for (int i = 0; i < this.players.length; i++) {
            this.players[i] = input.readUTF();
        }
    }

    @Override
    public MessageType<TransferPlayersMessage> getType() {
        return MessageType.TRANSFER_PLAYERS;
    }

    @Override
    protected void serialize(ByteArrayDataOutput output) {
        output.writeUTF(server);
        output.writeInt(players.length);
        for (String player : players) {
            output.writeUTF(player);
        }
    }

    public String getServer() {
        return server;
    }

    public String[] getPlayers() {
        return players;
    }
}

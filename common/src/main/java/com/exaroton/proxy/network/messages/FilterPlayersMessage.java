package com.exaroton.proxy.network.messages;

import com.exaroton.proxy.network.Message;
import com.exaroton.proxy.network.id.CommandExecutionId;
import com.exaroton.proxy.network.id.FilterPlayersRequestId;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import java.util.Set;

public abstract class FilterPlayersMessage<This extends FilterPlayersMessage<This>> extends Message<This> {
    private final FilterPlayersRequestId requestId;
    private final Set<String> playerNames;

    public FilterPlayersMessage(CommandExecutionId id, FilterPlayersRequestId requestId, Set<String> playerNames) {
        super(id);
        this.requestId = requestId;
        this.playerNames = playerNames;
    }

    public FilterPlayersMessage(ByteArrayDataInput input) {
        super(input);
        this.requestId = new FilterPlayersRequestId(input);
        this.playerNames = deserializeStringSet(input);
    }

    @Override
    protected void serialize(ByteArrayDataOutput output) {
        requestId.serialize(output);
        serializeStringSet(output, playerNames);
    }

    public FilterPlayersRequestId getRequestId() {
        return requestId;
    }

    public Set<String> getPlayerNames() {
        return playerNames;
    }
}

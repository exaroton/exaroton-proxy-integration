package com.exaroton.proxy.network.messages;

import com.exaroton.proxy.network.MessageType;
import com.exaroton.proxy.network.id.CommandExecutionId;
import com.exaroton.proxy.network.id.FilterPlayersRequestId;
import com.google.common.io.ByteArrayDataInput;

import java.util.Set;

public class FilterPlayersResponse extends FilterPlayersMessage<FilterPlayersResponse> {
    public FilterPlayersResponse(CommandExecutionId id, FilterPlayersRequestId requestId, Set<String> playerNames) {
        super(id, requestId, playerNames);
    }

    public FilterPlayersResponse(ByteArrayDataInput input) {
        super(input);
    }

    @Override
    public MessageType<FilterPlayersResponse> getType() {
        return MessageType.FILTER_PLAYERS_RESPONSE;
    }
}

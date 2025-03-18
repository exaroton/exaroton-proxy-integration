package com.exaroton.proxy.network.messages;

import com.exaroton.proxy.network.MessageType;
import com.exaroton.proxy.network.id.CommandExecutionId;
import com.exaroton.proxy.network.id.FilterPlayersRequestId;
import com.google.common.io.ByteArrayDataInput;

import java.util.Set;

public class FilterPlayersRequest extends FilterPlayersMessage<FilterPlayersRequest> {
    public FilterPlayersRequest(CommandExecutionId id, Set<String> playerNames) {
        super(id, new FilterPlayersRequestId(), playerNames);
    }

    public FilterPlayersRequest(ByteArrayDataInput input) {
        super(input);
    }

    @Override
    public MessageType<FilterPlayersRequest> getType() {
        return MessageType.FILTER_PLAYERS_REQUEST;
    }
}

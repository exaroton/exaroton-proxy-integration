package com.exaroton.proxy.network.id;

import com.google.common.io.ByteArrayDataInput;

public class FilterPlayersRequestId extends NetworkId {
    /**
     * Create a new random filter players ID
     */
    public FilterPlayersRequestId() {
    }

    /**
     * Read a filter players ID from input
     * @param input input
     */
    public FilterPlayersRequestId(ByteArrayDataInput input) {
        super(input);
    }
}

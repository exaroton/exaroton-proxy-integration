package com.exaroton.proxy.network.id;


import com.google.common.io.ByteArrayDataInput;

/**
 * ID of a command execution
 */
public class CommandExecutionId extends NetworkId {
    public CommandExecutionId() {
    }

    public CommandExecutionId(ByteArrayDataInput input) {
        super(input);
    }
}

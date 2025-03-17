package com.exaroton.proxy.network.id;


import com.google.common.io.ByteArrayDataInput;

/**
 * ID of a command execution
 */
public class CommandExecutionId extends NetworkId {
    /**
     * Create a new random command execution ID
     */
    public CommandExecutionId() {
    }

    /**
     * Read a command execution ID from input
     * @param input input
     */
    public CommandExecutionId(ByteArrayDataInput input) {
        super(input);
    }
}

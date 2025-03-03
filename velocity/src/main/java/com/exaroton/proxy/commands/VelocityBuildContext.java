package com.exaroton.proxy.commands;

import com.velocitypowered.api.command.CommandSource;

public class VelocityBuildContext extends BuildContext<CommandSource> {
    /**
     * Constructs a new velocity build context.
     */
    public VelocityBuildContext() {
        super();
    }

    @Override
    public VelocityCommandSourceAccessor mapSource(CommandSource source) {
        return new VelocityCommandSourceAccessor(source);
    }
}

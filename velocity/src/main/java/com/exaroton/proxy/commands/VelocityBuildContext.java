package com.exaroton.proxy.commands;

import com.velocitypowered.api.command.CommandSource;
import com.exaroton.proxy.components.AdventureComponent;

public class VelocityBuildContext extends BuildContext<CommandSource, AdventureComponent> {
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

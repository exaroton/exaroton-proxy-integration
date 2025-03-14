package com.exaroton.proxy.commands;

import com.exaroton.proxy.ServerConnectionCommandSource;
import com.exaroton.proxy.VelocityPlugin;
import com.velocitypowered.api.command.CommandSource;

public class VelocityBuildContext extends BuildContext<CommandSource> {
    private final VelocityPlugin velocityPlugin;

    /**
     * Constructs a new velocity build context.
     */
    public VelocityBuildContext(VelocityPlugin velocityPlugin) {
        super();
        this.velocityPlugin = velocityPlugin;
    }

    @Override
    public CommandSourceAccessor mapSource(CommandSource source) {
        if (source instanceof ServerConnectionCommandSource s) {
            return s.source();
        }

        return new VelocityCommandSourceAccessor(velocityPlugin, source);
    }
}

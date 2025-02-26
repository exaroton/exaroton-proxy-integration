package com.exaroton.proxy.commands;

import com.velocitypowered.api.command.CommandSource;
import com.exaroton.proxy.components.AdventureComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public class VelocityCommandSourceAccessor implements ICommandSourceAccessor<AdventureComponent> {
    /**
     * The original velocity command source
     */
    protected final CommandSource source;

    /**
     * Create a new velocity command source accessor.
     *
     * @param source The original velocity command source.
     */
    public VelocityCommandSourceAccessor(CommandSource source) {
        this.source = source;
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return source.hasPermission(permission.node());
    }

    @Override
    public Path getRootDirectory() {
        return Path.of(".");
    }

    @Override
    public Collection<LogDirectory> getLogDirectories() {
        return List.of(
                new LogDirectory(getRootDirectory().resolve("logs"), LogType.LOG)
        );
    }

    @Override
    public void sendFailure(AdventureComponent message) {
        source.sendMessage(message.getBoxed().color(NamedTextColor.RED));
    }

    @Override
    public void sendSuccess(AdventureComponent message, boolean allowLogging) {
        source.sendMessage(message.getBoxed());
    }
}

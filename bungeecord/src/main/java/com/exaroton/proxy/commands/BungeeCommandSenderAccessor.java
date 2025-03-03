package com.exaroton.proxy.commands;

import com.exaroton.proxy.BungeePlugin;
import net.kyori.adventure.audience.Audience;
import net.md_5.bungee.api.CommandSender;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

/**
 * A BungeeCord implementation of {@link CommandSourceAccessor}.
 */
public class BungeeCommandSenderAccessor extends CommandSourceAccessor {
    private final BungeePlugin plugin;
    private final CommandSender sender;

    /**
     * Create a new BungeeCommandSenderAccessor
     * @param plugin The plugin instance
     * @param sender The command sender
     */
    public BungeeCommandSenderAccessor(BungeePlugin plugin,
                                       CommandSender sender) {
        this.plugin = plugin;
        this.sender = sender;
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return sender.hasPermission(permission.node());
    }

    @Override
    public Path getRootDirectory() {
        return Path.of(".");
    }

    @Override
    public Collection<LogDirectory> getLogDirectories() {
        return List.of(
                new LogDirectory(getRootDirectory(), LogType.LOG),
                // Waterfall places logs in a sensible location
                new LogDirectory(getRootDirectory().resolve("logs"), LogType.LOG)
        );
    }

    @Override
    public String getCurrentLogFileName() {
        return "proxy.log.0";
    }

    @Override
    protected Audience getAudience() {
        return plugin.audience(sender);
    }
}

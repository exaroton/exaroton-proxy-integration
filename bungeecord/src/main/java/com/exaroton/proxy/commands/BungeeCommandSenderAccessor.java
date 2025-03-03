package com.exaroton.proxy.commands;

import com.exaroton.proxy.BungeePlugin;
import net.kyori.adventure.audience.Audience;
import net.md_5.bungee.api.CommandSender;

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
    protected Audience getAudience() {
        return plugin.audience(sender);
    }
}

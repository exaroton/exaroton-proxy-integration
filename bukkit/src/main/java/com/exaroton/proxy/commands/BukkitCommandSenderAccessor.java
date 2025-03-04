package com.exaroton.proxy.commands;

import com.exaroton.proxy.BukkitPlugin;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;

/**
 * Bukkit Command Sender Accessor
 */
public class BukkitCommandSenderAccessor extends CommandSourceAccessor {
    protected final BukkitPlugin plugin;
    protected final CommandSender commandSender;

    /**
     * Create a new Bukkit Command Sender Accessor
     * @param plugin the plugin
     * @param commandSender the command sender
     */
    public BukkitCommandSenderAccessor(BukkitPlugin plugin,
                                       CommandSender commandSender) {
        this.plugin = plugin;
        this.commandSender = commandSender;
    }

    @Override
    public boolean hasPermission(String permission) {
        return commandSender.hasPermission(permission);
    }

    @Override
    protected Audience getAudience() {
        return plugin.audience(commandSender);
    }
}

package com.exaroton.proxy.commands;

import com.exaroton.proxy.BukkitPlugin;
import org.bukkit.command.CommandSender;

/**
 * Bukkit Build Context implementation
 */
public class BukkitBuildContext extends BuildContext<CommandSender> {
    protected final BukkitPlugin plugin;

    /**
     * Create a new Bukkit Build Context
     * @param plugin The plugin
     */
    public BukkitBuildContext(BukkitPlugin plugin) {
        super();
        this.plugin = plugin;
    }

    @Override
    public CommandSourceAccessor mapSource(CommandSender source) {
        return new BukkitCommandSenderAccessor(plugin, source);
    }
}

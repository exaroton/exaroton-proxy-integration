package com.exaroton.proxy.commands;

import com.exaroton.proxy.BungeePlugin;
import net.md_5.bungee.api.CommandSender;

/**
 * A BuildContext implementation for BungeeCord
 */
public class BungeeBuildContext extends BuildContext<CommandSender> {
    /**
     * The plugin instance
     */
    private final BungeePlugin plugin;

    /**
     * Create a new BungeeBuildContext
     * @param plugin The plugin instance
     */
    public BungeeBuildContext(BungeePlugin plugin) {
        super();
        this.plugin = plugin;
    }

    @Override
    public CommandSourceAccessor mapSource(CommandSender source) {
        return new BungeeCommandSenderAccessor(plugin, source);
    }
}

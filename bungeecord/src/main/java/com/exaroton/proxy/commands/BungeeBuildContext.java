package com.exaroton.proxy.commands;

import com.exaroton.proxy.BungeePlugin;
import com.exaroton.proxy.CommandSourceCommandSender;
import com.exaroton.proxy.ProxyPluginImpl;
import net.md_5.bungee.api.CommandSender;

/**
 * A BuildContext implementation for BungeeCord
 */
public class BungeeBuildContext extends BuildContext<CommandSender> {
    /**
     * The plugin instance
     */
    private final BungeePlugin plugin;
    private final ProxyPluginImpl commonPlugin;

    /**
     * Create a new BungeeBuildContext
     *
     * @param plugin       The plugin instance
     * @param commonPlugin The common plugin instance
     */
    public BungeeBuildContext(BungeePlugin plugin, ProxyPluginImpl commonPlugin) {
        super();
        this.plugin = plugin;
        this.commonPlugin = commonPlugin;
    }

    @Override
    public CommandSourceAccessor mapSource(CommandSender sender) {
        if (sender instanceof CommandSourceCommandSender) {
            CommandSourceCommandSender source = (CommandSourceCommandSender) sender;
            return source.getCommandSource();
        }

        return new BungeeCommandSenderAccessor(plugin, commonPlugin, sender);
    }
}

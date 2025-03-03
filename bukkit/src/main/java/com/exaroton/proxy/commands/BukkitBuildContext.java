package com.exaroton.proxy.commands;

import com.exaroton.proxy.BukkitPlugin;
import org.bukkit.command.CommandSender;

public class BukkitBuildContext extends BuildContext<CommandSender> {
    protected final BukkitPlugin plugin;

    public BukkitBuildContext(BukkitPlugin plugin) {
        super();
        this.plugin = plugin;
    }

    @Override
    public CommandSourceAccessor mapSource(CommandSender source) {
        return new BukkitCommandSenderAccessor(plugin, source);
    }
}

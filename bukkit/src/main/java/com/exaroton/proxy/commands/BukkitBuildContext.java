package com.exaroton.proxy.commands;

import com.exaroton.proxy.BukkitPlugin;
import com.exaroton.proxy.components.AdventureComponent;
import org.bukkit.command.CommandSender;

public class BukkitBuildContext extends BuildContext<CommandSender, AdventureComponent> {
    protected final BukkitPlugin plugin;

    public BukkitBuildContext(BukkitPlugin plugin) {
        super();
        this.plugin = plugin;
    }

    @Override
    public ICommandSourceAccessor<AdventureComponent> mapSource(CommandSender source) {
        return new BukkitCommandSenderAccessor(plugin, source);
    }
}

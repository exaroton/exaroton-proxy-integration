package com.exaroton.proxy.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

/**
 * A BungeeCord command implementation that uses Brigadier for command execution and completion.
 */
public class BungeeBrigadierCommand extends Command implements TabExecutor {

    private final BrigadierExecutor<CommandSender> executor;

    public BungeeBrigadierCommand(
            CommandDispatcher<CommandSender> dispatcher,
            BuildContext<CommandSender> buildContext
    ) {
        super("exaroton", Permission.BASE.node());
        this.executor = new BrigadierExecutor<>(dispatcher, buildContext);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        executor.executeCommand(sender, this.getName(), args);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return executor.completeCommand(sender, this.getName(), args);
    }
}

package com.exaroton.proxy.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.exaroton.proxy.Constants;
import com.exaroton.proxy.BukkitPlugin;
import org.bukkit.command.*;
import org.bukkit.command.Command;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BukkitBrigadierCommand implements CommandExecutor, TabCompleter {
    private final BukkitPlugin plugin;
    private final BrigadierExecutor<CommandSender> executor;
    private final CommandDispatcher<CommandSender> dispatcher;

    public BukkitBrigadierCommand(
            BukkitPlugin plugin,
            CommandDispatcher<CommandSender> dispatcher,
            BuildContext<CommandSender> buildContext
    ) {
        this.plugin = plugin;
        this.dispatcher = dispatcher;
        this.executor = new BrigadierExecutor<>(dispatcher, buildContext);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String[] args) {
        executor.executeCommand(sender, label, args);
        return true;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command command,
                                                @NotNull String label,
                                                @NotNull String[] args) {
        return executor.completeCommand(sender, label, args);
    }

    public void register() {
        for (var commandNode : dispatcher.getRoot().getChildren()) {
            var command = plugin.getCommand(commandNode.getName());

            if (command == null) {
                Constants.LOG.warn("Command {} is missing from plugin.yml, skipping...", commandNode.getName());
                continue;
            }

            command.setExecutor(this);
            command.setTabCompleter(this);
        }
    }
}

package com.exaroton.proxy.commands;

import com.exaroton.proxy.Constants;
import com.exaroton.proxy.BukkitPlugin;
import com.exaroton.proxy.BukkitMessageController;
import org.bukkit.command.*;
import org.bukkit.command.Command;
import org.jetbrains.annotations.NotNull;

/**
 * Bukkit command that forwards all inputs to the proxy
 */
public class BukkitProxyCommand implements CommandExecutor {
    private final BukkitPlugin plugin;
    private final BukkitMessageController messageController;

    public BukkitProxyCommand(
            BukkitPlugin plugin,
            BukkitMessageController messageController
    ) {
        this.plugin = plugin;
        this.messageController = messageController;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String[] args) {
        messageController.executeCommand(sender, args);
        return true;
    }


    public void register() {
        var command = plugin.getCommand("exaroton");

        if (command == null) {
            Constants.LOG.error("Command is missing from plugin.yml");
            return;
        }

        command.setExecutor(this);
    }
}

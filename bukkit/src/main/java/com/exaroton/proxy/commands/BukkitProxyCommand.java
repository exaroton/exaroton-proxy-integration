package com.exaroton.proxy.commands;

import com.exaroton.proxy.Constants;
import com.exaroton.proxy.BukkitPlugin;
import com.exaroton.proxy.BukkitMessageController;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.command.Command;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        var processedArgs = new ArrayList<String>(args.length);

        for (var arg : args) {
            if (arg.startsWith("@")) {
                var entities = matchSelector(sender, arg);
                if (entities.isEmpty()) {
                    plugin.audience(sender).sendMessage(Component.text("No players found", NamedTextColor.RED));
                    return true;
                }
                processedArgs.addAll(entities);
            } else {
                processedArgs.add(arg);
            }
        }

        messageController.executeCommand(sender, processedArgs.toArray(String[]::new));
        return true;
    }

    /**
     * Match a selector (@a, @p, @r, @s) to a list of player names. This method removes other entities and
     * returns the selector itself if it was invalid.
     * @return List of player names or the selector itself
     */
    protected Collection<String> matchSelector(CommandSender sender, String arg) {
        var result = new ArrayList<String>();
        List<Entity> entities;
        try {
            entities = Bukkit.selectEntities(sender, arg);
        } catch (IllegalArgumentException e) {
            return List.of(arg);
        }

        for (var entity : entities) {
            if (entity instanceof Player) {
                result.add(entity.getName());
            }
        }

        return result;
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

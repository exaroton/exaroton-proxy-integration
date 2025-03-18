package com.exaroton.proxy.commands;

import com.exaroton.api.server.Server;
import com.exaroton.proxy.BungeePlugin;
import com.exaroton.proxy.ProxyPluginImpl;
import net.kyori.adventure.audience.Audience;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A BungeeCord implementation of {@link CommandSourceAccessor}.
 */
public class BungeeCommandSenderAccessor extends CommandSourceAccessor {
    private final BungeePlugin plugin;
    private final ProxyPluginImpl commonPlugin;
    private final CommandSender sender;

    /**
     * Create a new BungeeCommandSenderAccessor
     *
     * @param plugin       The plugin instance
     * @param commonPlugin The common plugin instance
     * @param sender       The command sender
     */
    public BungeeCommandSenderAccessor(BungeePlugin plugin,
                                       ProxyPluginImpl commonPlugin,
                                       CommandSender sender) {
        this.plugin = plugin;
        this.commonPlugin = commonPlugin;
        this.sender = sender;
    }

    @Override
    public boolean hasPermission(String permission) {
        return sender.hasPermission(permission);
    }

    @Override
    public Optional<String> getPlayerName() {
        if (sender instanceof ProxiedPlayer) {
            return Optional.of(sender.getName());
        }

        return Optional.empty();
    }

    @Override
    public Set<String> filterPlayers(Set<String> playerNames) {
        return playerNames.stream().filter(commonPlugin.getPlayers()::contains).collect(Collectors.toSet());
    }

    @Override
    public void transferPlayers(Server server, Set<String> playerNames) {
        commonPlugin.getProxyServerManager().transferPlayers(server, playerNames);
    }

    @Override
    protected Audience getAudience() {
        return plugin.audience(sender);
    }
}

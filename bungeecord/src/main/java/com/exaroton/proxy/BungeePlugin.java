package com.exaroton.proxy;

import com.exaroton.proxy.commands.BungeeBrigadierCommand;
import com.exaroton.proxy.commands.BungeeBuildContext;
import com.exaroton.proxy.platform.Services;
import com.mojang.brigadier.CommandDispatcher;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeePlugin extends Plugin {
    static {
        // Bukkit uses a different class loader for plugins than the default thread context class loader.
        Services.setClassLoader(BungeePlugin.class.getClassLoader());
    }

    protected ProxyPluginImpl commonPlugin = new ProxyPluginImpl(this);
    protected BungeeAudiences adventure;
    protected BungeeBrigadierCommand command;

    @Override
    public void onEnable() {
        commonPlugin.setUp().join();
        adventure = BungeeAudiences.create(this);
        registerCommands();
        getProxy().getPluginManager().registerListener(this, commonPlugin.getMessageController());
    }

    @Override
    public void onDisable() {
        if (adventure != null) {
            adventure.close();
            adventure = null;
        }
        commonPlugin.tearDown().join();
    }

    private BungeeAudiences adventure() {
        if (adventure == null) {
            throw new IllegalStateException("Adventure platform is not initialized");
        }

        return adventure;
    }

    public Audience audience(CommandSender sender) {
        //noinspection resource
        return adventure().sender(sender);
    }

    public BungeeBrigadierCommand getCommand() {
        return command;
    }

    protected void registerCommands() {
        CommandDispatcher<CommandSender> dispatcher = new CommandDispatcher<>();
        var context = new BungeeBuildContext(this, commonPlugin);

        commonPlugin.registerCommands(dispatcher, context);

        command = new BungeeBrigadierCommand(dispatcher, context);
        this.getProxy().getPluginManager().registerCommand(this, command);
    }
}

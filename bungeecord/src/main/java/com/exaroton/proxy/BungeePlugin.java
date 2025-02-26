package com.exaroton.proxy;

import com.mojang.brigadier.CommandDispatcher;
import com.exaroton.proxy.commands.BungeeBrigadierCommand;
import com.exaroton.proxy.commands.BungeeBuildContext;
import com.exaroton.proxy.components.AdventureComponentFactory;
import com.exaroton.proxy.platform.Services;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeePlugin extends Plugin {
    static {
        // Bukkit uses a different class loader for plugins than the default thread context class loader.
        Services.setClassLoader(BungeePlugin.class.getClassLoader());
    }

    protected CommonPlugin commonPlugin = new CommonPlugin();
    protected BungeeAudiences adventure;
    protected CommandDispatcher<CommandSender> dispatcher;

    @Override
    public void onEnable() {
        commonPlugin.init();
        adventure = BungeeAudiences.create(this);
        registerCommands();
    }

    @Override
    public void onDisable() {
        if (adventure != null) {
            adventure.close();
            adventure = null;
        }
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

    protected void registerCommands() {
        dispatcher = new CommandDispatcher<>();
        var context = new BungeeBuildContext(this);
        var componentFactory = new AdventureComponentFactory();

        commonPlugin.registerCommands(dispatcher, context, componentFactory);

        var command = new BungeeBrigadierCommand(dispatcher, context, componentFactory);
        this.getProxy().getPluginManager().registerCommand(this, command);
    }
}

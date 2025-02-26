package com.exaroton.proxy;

import com.mojang.brigadier.CommandDispatcher;
import com.exaroton.proxy.commands.BukkitBrigadierCommand;
import com.exaroton.proxy.components.AdventureComponentFactory;
import com.exaroton.proxy.commands.BukkitBuildContext;
import com.exaroton.proxy.platform.Services;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitPlugin extends JavaPlugin {
    static {
        // Bukkit uses a different class loader for plugins than the default thread context class loader.
        Services.setClassLoader(BukkitPlugin.class.getClassLoader());
    }

    protected CommonPlugin commonPlugin = new CommonPlugin();
    protected BukkitAudiences adventure;
    protected CommandDispatcher<CommandSender> dispatcher;

    @Override
    public void onEnable() {
        commonPlugin.init();
        adventure = BukkitAudiences.create(this);
        registerCommands();
    }

    @Override
    public void onDisable() {
        if (adventure != null) {
            adventure.close();
            adventure = null;
        }
    }

    private BukkitAudiences adventure() {
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
        var context = new BukkitBuildContext(this);
        var componentFactory = new AdventureComponentFactory();

        commonPlugin.registerCommands(dispatcher, context, componentFactory);
        var executor = new BukkitBrigadierCommand(this, dispatcher, context, componentFactory);
        executor.register();
    }
}

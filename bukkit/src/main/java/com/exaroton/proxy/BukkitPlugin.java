package com.exaroton.proxy;

import com.exaroton.proxy.commands.BukkitBrigadierCommand;
import com.exaroton.proxy.commands.BukkitBuildContext;
import com.exaroton.proxy.commands.Command;
import com.exaroton.proxy.platform.Services;
import com.mojang.brigadier.CommandDispatcher;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.List;

public class BukkitPlugin extends JavaPlugin {
    static {
        // Bukkit uses a different class loader for plugins than the default thread context class loader.
        Services.setClassLoader(BukkitPlugin.class.getClassLoader());
    }

    protected CommonPlugin commonPlugin = new CommonPlugin() {

        @Override
        protected Collection<Command<?>> getCommands() {
            return List.of(); // TODO: Implement
        }
    };
    protected BukkitAudiences adventure;
    protected CommandDispatcher<CommandSender> dispatcher;
    protected MessageControllerImpl messageController;

    @Override
    public void onEnable() {
        adventure = BukkitAudiences.create(this);
        registerCommands();

        checkIfBungee(); // TODO: consider if this works with modern velocity forwarding

        messageController = new MessageControllerImpl(this);
    }

    private void checkIfBungee() {
        YamlConfiguration config = getServer().spigot().getConfig();
        ConfigurationSection settings = config.getConfigurationSection("settings");

        if (settings == null || !settings.getBoolean("bungeecord")) {
            Constants.LOG.error("This server is not BungeeCord.");
            Constants.LOG.error("If the server is already hooked to BungeeCord, please enable it into your spigot.yml aswell.");
            Constants.LOG.error("Plugin disabled!");
            getServer().getPluginManager().disablePlugin(this);
        }
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

        commonPlugin.registerCommands(dispatcher, context);
        var executor = new BukkitBrigadierCommand(this, dispatcher, context);
        executor.register();
    }
}

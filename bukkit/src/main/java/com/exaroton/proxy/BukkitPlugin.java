package com.exaroton.proxy;

import com.exaroton.proxy.commands.BukkitProxyCommand;
import com.exaroton.proxy.platform.Services;
import com.mojang.brigadier.CommandDispatcher;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitPlugin extends JavaPlugin {
    static {
        // Bukkit uses a different class loader for plugins than the default thread context class loader.
        Services.setClassLoader(BukkitPlugin.class.getClassLoader());
    }

    protected BukkitAudiences adventure;
    protected CommandDispatcher<CommandSender> dispatcher;
    protected BukkitMessageController messageController;

    @Override
    public void onEnable() {
        adventure = BukkitAudiences.create(this);
        if (!checkIfBungee()) {
            return;
        }; // TODO: consider if this works with modern velocity forwarding

        messageController = new BukkitMessageController(this);
        var command = new BukkitProxyCommand(this, messageController);
        command.register();
    }

    private boolean checkIfBungee() {
        YamlConfiguration config = getServer().spigot().getConfig();
        ConfigurationSection settings = config.getConfigurationSection("settings");

        if (settings == null || !settings.getBoolean("bungeecord")) {
            Constants.LOG.error("This server is not BungeeCord.");
            Constants.LOG.error("If the server is already hooked to BungeeCord, please enable it into your spigot.yml aswell.");
            Constants.LOG.error("Plugin disabled!");
            getServer().getPluginManager().disablePlugin(this);
            return false;
        }
        return true;
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
}

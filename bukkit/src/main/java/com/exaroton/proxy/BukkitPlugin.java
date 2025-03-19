package com.exaroton.proxy;

import com.exaroton.proxy.commands.BukkitProxyCommand;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitPlugin extends JavaPlugin {
    protected BukkitAudiences adventure;
    protected BukkitMessageController messageController;

    @Override
    public void onEnable() {
        adventure = BukkitAudiences.create(this);

        messageController = new BukkitMessageController(this);
        var command = new BukkitProxyCommand(this, messageController);
        command.register();
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

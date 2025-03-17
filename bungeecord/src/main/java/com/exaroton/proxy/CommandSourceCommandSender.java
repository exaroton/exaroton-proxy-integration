package com.exaroton.proxy;

import com.exaroton.proxy.commands.CommandSourceAccessor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

import java.util.Collection;
import java.util.List;

/**
 * Implementation of {@link CommandSender} based on a {@link CommandSourceAccessor}.
 */
public class CommandSourceCommandSender implements CommandSender {
    private final CommandSourceAccessor source;

    /**
     * Create a new CommandSourceCommandSender
     * @param source The command source
     */
    public CommandSourceCommandSender(CommandSourceAccessor source) {
        this.source = source;
    }

    /**
     * Get the underlying command source
     * @return The command source
     */
    public CommandSourceAccessor getCommandSource() {
        return source;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void sendMessage(String message) {
        source.sendSuccess(Component.text(message));
    }

    @Override
    public void sendMessages(String... messages) {
        for (String msg : messages) {
            sendMessage(msg);
        }
    }

    @Override
    public void sendMessage(BaseComponent... message) {
        for (BaseComponent msg : message) {
            sendMessage(msg);
        }
    }

    @Override
    public void sendMessage(BaseComponent message) {
        String json = ComponentSerializer.toString(message);
        source.sendSuccess(JSONComponentSerializer.json().deserialize(json));
    }

    @Override
    public Collection<String> getGroups() {
        return List.of();
    }

    @Override
    public void addGroups(String... groups) {

    }

    @Override
    public void removeGroups(String... groups) {

    }

    @Override
    public boolean hasPermission(String permission) {
        return source.hasPermission(permission);
    }

    @Override
    public void setPermission(String permission, boolean value) {

    }

    @Override
    public Collection<String> getPermissions() {
        return List.of();
    }
}

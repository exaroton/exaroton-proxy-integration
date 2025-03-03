package com.exaroton.proxy;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.Server;

import java.util.Collection;
import java.util.List;

public class ServerCommandSender implements CommandSender {
    private final Server source;

    public ServerCommandSender(Server source) {
        this.source = source;
    }

    @Override
    public String getName() {
        return source.getInfo().getName();
    }

    @Override
    public void sendMessage(String message) {

    }

    @Override
    public void sendMessages(String... messages) {

    }

    @Override
    public void sendMessage(BaseComponent... message) {

    }

    @Override
    public void sendMessage(BaseComponent message) {

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
        return true;
    }

    @Override
    public void setPermission(String permission, boolean value) {

    }

    @Override
    public Collection<String> getPermissions() {
        return List.of();
    }
}

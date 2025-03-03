package com.exaroton.proxy;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.permission.Tristate;
import com.velocitypowered.api.proxy.ServerConnection;

public class ServerConnectionCommandSource implements CommandSource {
    private final ServerConnection source;

    public ServerConnectionCommandSource(ServerConnection source) {
        this.source = source;
    }


    @Override
    public Tristate getPermissionValue(String permission) {
        return Tristate.TRUE;
    }
}

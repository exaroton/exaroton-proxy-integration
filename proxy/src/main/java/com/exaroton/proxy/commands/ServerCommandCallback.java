package com.exaroton.proxy.commands;

import com.exaroton.api.server.Server;

import java.io.IOException;

@FunctionalInterface
public interface ServerCommandCallback {
    /**
     * Execute this command
     * @param source command source
     * @param server server
     * @throws IOException if an error occurs
     */
    void execute(CommandSourceAccessor source, Server server) throws IOException;
}

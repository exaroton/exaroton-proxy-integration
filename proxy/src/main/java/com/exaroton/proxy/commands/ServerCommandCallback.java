package com.exaroton.proxy.commands;

import com.exaroton.api.server.Server;

@FunctionalInterface
public interface ServerCommandCallback {
    /**
     * Execute this command
     * @param source command source
     * @param server server
     * @throws Exception if an error occurs
     */
    void execute(CommandSourceAccessor source, Server server) throws Exception;
}

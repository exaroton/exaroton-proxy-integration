package com.exaroton.proxy;

import com.exaroton.proxy.commands.*;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import java.util.Collection;

public abstract class CommonPlugin {
    protected abstract Collection<Command<?>> getCommands();

    protected <T> void registerCommands(CommandDispatcher<T> dispatcher, BuildContext<T> context) {
        Constants.LOG.info("Registering command exaroton");
        var builder = LiteralArgumentBuilder.<T>literal("exaroton");

        for (var command : getCommands()) {
            dispatcher.register(command.build(context, builder));
        }
    }
}

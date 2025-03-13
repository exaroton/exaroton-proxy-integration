package com.exaroton.proxy.commands;

import com.exaroton.proxy.CommonPlugin;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

/**
 * A platform-agnostic command
 * @param <CommonType> The plugin type
 */
public abstract class Command<CommonType extends CommonPlugin> {
    /**
     * The plugin
     */
    protected final CommonType plugin;

    /**
     * Create a new command
     * @param plugin The plugin
     */
    public Command(CommonType plugin) {
        this.plugin = plugin;
    }

    /**
     * Build a brigadier command
     *
     * @param buildContext Contains the environment and various methods to build the command.
     * @param builder      the builder to add the command to
     * @param <T>          the command source type
     * @return the built command
     */
    public abstract <T> LiteralArgumentBuilder<T> build(
            BuildContext<T> buildContext,
            LiteralArgumentBuilder<T> builder
    );
}

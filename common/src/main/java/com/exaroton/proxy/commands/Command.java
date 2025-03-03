package com.exaroton.proxy.commands;

import com.exaroton.api.ExarotonClient;
import com.exaroton.proxy.CommonPlugin;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

/**
 * A platform-agnostic command
 */
public abstract class Command {
    /**
     * The plugin
     */
    protected final CommonPlugin plugin;
    /**
     * The exaroton API client
     */
    protected final ExarotonClient apiClient;

    /**
     * Create a new command
     * @param plugin The plugin
     * @param apiClient The exaroton API client
     */
    public Command(CommonPlugin plugin, ExarotonClient apiClient) {
        this.plugin = plugin;
        this.apiClient = apiClient;
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

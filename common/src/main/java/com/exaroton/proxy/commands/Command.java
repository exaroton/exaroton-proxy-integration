package com.exaroton.proxy.commands;

import com.exaroton.api.ExarotonClient;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.exaroton.proxy.components.IComponent;
import com.exaroton.proxy.components.IComponentFactory;
import com.exaroton.proxy.components.IStyle;

/**
 * A platform-agnostic command
 * @param <ComponentType> The type of components used on the platform
 * @param <StyleType> The type of styles used on the platform
 * @param <ClickEventType> The type of click events used on the platform
 */
public abstract class Command<
        ComponentType extends IComponent<ComponentType, StyleType, ClickEventType>,
        StyleType extends IStyle<StyleType, ClickEventType>,
        ClickEventType
        > {
    /**
     * The exaroton API client
     */
    protected final ExarotonClient apiClient;
    /**
     * A component factory
     */
    protected final IComponentFactory<ComponentType, StyleType, ClickEventType> componentFactory;

    /**
     * Create a new command
     * @param apiClient The exaroton API client
     * @param componentFactory A component factory
     */
    public Command(
            ExarotonClient apiClient,
            IComponentFactory<ComponentType, StyleType, ClickEventType> componentFactory
    ) {
        this.apiClient = apiClient;
        this.componentFactory = componentFactory;
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
            BuildContext<T, ComponentType> buildContext,
            LiteralArgumentBuilder<T> builder
    );

    /**
     * Execute the command
     *
     * @param context      the command context
     * @param buildContext Contains the environment and various methods to execute the command.
     * @param <T>          the command source type
     * @return the result of the command
     */
    public abstract <T> int execute(CommandContext<T> context, BuildContext<T, ComponentType> buildContext);
}

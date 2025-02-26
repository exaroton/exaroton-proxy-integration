package com.exaroton.proxy.commands;

import com.exaroton.proxy.components.IComponent;

/**
 * A context for building commands.
 *
 * @param <T> Type of the "command source".
 * @param <ComponentType> Type of the components used in this context.
 */
public abstract class BuildContext<T, ComponentType extends IComponent<ComponentType, ?, ?>> {

    /**
     * Constructs a new build context.
     */
    public BuildContext() {
    }

    /**
     * Maps a command source to a command source accessor.
     * @param source The command source to map.
     * @return A command source accessor.
     */
    public abstract ICommandSourceAccessor<ComponentType> mapSource(T source);
}

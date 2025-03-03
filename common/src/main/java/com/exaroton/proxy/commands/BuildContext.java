package com.exaroton.proxy.commands;

/**
 * A context for building commands.
 *
 * @param <T> Type of the "command source".
 */
public abstract class BuildContext<T> {

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
    public abstract CommandSourceAccessor mapSource(T source);
}

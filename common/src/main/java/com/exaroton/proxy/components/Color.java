package com.exaroton.proxy.components;

import org.jetbrains.annotations.Nullable;

/**
 * An enum for colors used in chat components. This is a subset of the named Minecraft colors
 */
public enum Color {
    RED,
    EXAROTON_GREEN(0x19ba19)
    ;

    private final @Nullable Integer colorCode;

    Color(@Nullable Integer colorCode) {
        this.colorCode = colorCode;
    }

    Color() {
        this(null);
    }

    public @Nullable Integer getColorCode() {
        return colorCode;
    }
}

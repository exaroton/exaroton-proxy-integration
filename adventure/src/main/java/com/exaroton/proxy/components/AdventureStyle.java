package com.exaroton.proxy.components;

import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;

/**
 * Adventure Style implementation
 */
public class AdventureStyle implements IStyle<AdventureStyle, ClickEvent> {
    /**
     * The internal style
     */
    protected Style boxed;

    /**
     * Create a new AdventureStyle
     * @param boxed The style to wrap
     */
    public AdventureStyle(Style boxed) {
        this.boxed = boxed;
    }

    /**
     * Get the internal style
     * @return The internal style
     */
    public Style getBoxed() {
        return boxed;
    }

    @Override
    public AdventureStyle color(Color color) {
        switch (color) {
            case RED:
                boxed = boxed.color(NamedTextColor.RED);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported color: " + color);
        }
        return this;
    }

    @Override
    public AdventureStyle underlined() {
        boxed = boxed.decorate(TextDecoration.UNDERLINED);
        return this;
    }

    @Override
    public AdventureStyle italic() {
        boxed = boxed.decorate(TextDecoration.ITALIC);
        return this;
    }

    @Override
    public AdventureStyle clickEvent(ClickEvent clickEvent) {
        boxed = boxed.clickEvent(clickEvent);
        return this;
    }
}

package com.exaroton.proxy;

import com.exaroton.api.BrandColor;
import com.exaroton.api.server.Server;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public interface Components {
    static Component addressText(Server server) {
        return addressText(server.getAddress());
    }

    static Component addressText(String address) {
        return text(address, BrandColor.MAIN);
    }

    static Component text(String text, BrandColor color) {
        return Component.text(text, color(color));
    }

    static TextColor color(BrandColor color) {
        return TextColor.color(color.getRGB());
    }
}

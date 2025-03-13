package com.exaroton.proxy;

import com.exaroton.api.BrandColor;
import com.exaroton.api.server.Server;
import com.exaroton.api.server.ServerStatus;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

public interface Components {
    static Component incorrectStatus(Server server, Set<ServerStatus> allowedStatuses, String message) {
        Component component = Component.text("Server has to be");

        boolean first = true;
        Iterator<ServerStatus> iterator = allowedStatuses.iterator();
        while (iterator.hasNext()) {
            ServerStatus status = iterator.next();
            if (!first) {
                if (iterator.hasNext()) {
                    component = component.append(Component.text(","));
                } else {
                    component = component.appendSpace().append(Component.text("or"));
                }
            } else {
                first = false;
            }
            component = component.appendSpace().append(statusText(status));
        }

        return component.appendSpace()
                .append(Component.text("to be " + message + "."))
                .appendNewline()
                .append(addressText(server))
                .appendSpace()
                .append(Component.text("is currently"))
                .appendSpace()
                .append(statusText(server.getStatus()))
                .append(Component.text("."));
    }

    static Component statusText(ServerStatus status) {
        return text(status.getName().toLowerCase(Locale.ROOT), status.getColor());
    }

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

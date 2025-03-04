package com.exaroton.proxy.commands;

import com.exaroton.proxy.network.id.CommandExecutionId;
import com.exaroton.proxy.network.id.NetworkId;
import com.exaroton.proxy.network.ProxyMessageController;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.chat.ChatType;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;

public class PluginMessageCommandSourceAccessor<Server> extends CommandSourceAccessor {
    private final ProxyMessageController<Server> controller;
    private final Server server;
    private final CommandExecutionId id;

    public PluginMessageCommandSourceAccessor(ProxyMessageController<Server> controller,
                                              Server server,
                                              CommandExecutionId id) {
        this.controller = controller;
        this.server = server;
        this.id = id;
    }

    @Override
    public boolean hasPermission(String permission) {
        try {
            return controller.hasPermission(server, id, permission).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Audience getAudience() {
        return new Audience() {
            @SuppressWarnings({"UnstableApiUsage", "deprecation"})
            public void sendMessage(final @NotNull Identity source,
                                    final @NotNull Component message,
                                    final @NotNull MessageType type) {
                controller.sendTextComponent(server, id, message);
            }

            public void sendMessage(final @NotNull SignedMessage signedMessage, final @NotNull ChatType.Bound boundChatType) {
                final Component content = signedMessage.unsignedContent() != null ? signedMessage.unsignedContent() : Component.text(signedMessage.message());
                this.sendMessage(content);
            }
        };
    }
}

package com.exaroton.proxy.commands;

import com.exaroton.proxy.network.id.CommandExecutionId;
import com.exaroton.proxy.network.ProxyMessageController;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.chat.ChatType;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.Cleaner;

public class PluginMessageCommandSourceAccessor<Server> extends CommandSourceAccessor {
    private static final Cleaner cleaner = Cleaner.create();

    private static class State<Server> implements Runnable {
        private final ProxyMessageController<Server> controller;
        private final Server server;
        private final CommandExecutionId id;

        State(ProxyMessageController<Server> controller, Server server, CommandExecutionId id) {
            this.controller = controller;
            this.server = server;
            this.id = id;
        }

        public void run() {
            controller.freeExecutionId(server, id);
        }
    }

    private final ProxyMessageController<Server> controller;
    private final Server server;
    private final CommandExecutionId id;

    public PluginMessageCommandSourceAccessor(ProxyMessageController<Server> controller,
                                              Server server,
                                              CommandExecutionId id) {
        this.controller = controller;
        this.server = server;
        this.id = id;

        cleaner.register(this, new State<>(controller, server, id));
    }

    @Override
    public boolean hasPermission(String permission) {
        return controller.hasPermission(server, id, permission).join();
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

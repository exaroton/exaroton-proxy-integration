package com.exaroton.proxy.commands;

import com.exaroton.proxy.network.ProxyMessageController;
import com.exaroton.proxy.network.id.CommandExecutionId;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.chat.ChatType;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.Cleaner;
import java.util.Optional;
import java.util.Set;

public class PluginMessageCommandSourceAccessor<Server> extends CommandSourceAccessor {
    private static final Cleaner cleaner = Cleaner.create();

    private static class State<Server> implements Runnable {
        private final ProxyMessageController<Server, ?, ?> controller;
        private final Server server;
        private final CommandExecutionId id;

        State(ProxyMessageController<Server, ?, ?> controller, Server server, CommandExecutionId id) {
            this.controller = controller;
            this.server = server;
            this.id = id;
        }

        public void run() {
            controller.freeExecutionId(server, id);
        }
    }

    private final ProxyMessageController<Server, ?, ?> controller;
    private final Server server;
    @Nullable
    private final String playerName;
    private final CommandExecutionId id;

    public PluginMessageCommandSourceAccessor(ProxyMessageController<Server, ?, ?> controller,
                                              Server server,
                                              @Nullable String playerName,
                                              CommandExecutionId id) {
        this.controller = controller;
        this.server = server;
        this.playerName = playerName;
        this.id = id;

        cleaner.register(this, new State<>(controller, server, id));
    }

    @Override
    public boolean hasPermission(String permission) {
        return controller.hasPermission(server, id, permission).join();
    }

    @Override
    public Optional<String> getPlayerName() {
        return Optional.ofNullable(playerName);
    }

    @Override
    public Set<String> filterPlayers(Set<String> playerNames) {
        return controller.filterPlayers(server, id, playerNames).join();
    }

    @Override
    public void transferPlayers(com.exaroton.api.server.Server server, Set<String> playerNames) {
        controller.transferPlayers(this.server, id, server.getId(), playerNames);
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

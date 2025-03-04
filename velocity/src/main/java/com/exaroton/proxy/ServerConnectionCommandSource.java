package com.exaroton.proxy;

import com.exaroton.proxy.commands.CommandSourceAccessor;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.permission.Tristate;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.chat.ChatType;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class ServerConnectionCommandSource implements CommandSource {
    private final CommandSourceAccessor source;

    public ServerConnectionCommandSource(CommandSourceAccessor source) {
        this.source = source;
    }

    @Override
    public Tristate getPermissionValue(String permission) {
        return source.hasPermission(permission) ? Tristate.TRUE : Tristate.FALSE;
    }

    @SuppressWarnings({"UnstableApiUsage", "deprecation"})
    public void sendMessage(final @NotNull Identity i,
                            final @NotNull Component message,
                            final @NotNull MessageType type) {
        source.sendSuccess(message);
    }

    @Override
    public void sendMessage(final @NotNull SignedMessage signedMessage, final @NotNull ChatType.Bound boundChatType) {
        final Component content = signedMessage.unsignedContent() != null ? signedMessage.unsignedContent() : Component.text(signedMessage.message());
        this.sendMessage(content);
    }
}

package com.exaroton.proxy.commands;

import com.exaroton.api.server.Server;
import com.exaroton.proxy.CommonProxyPlugin;
import com.mojang.brigadier.context.CommandContext;
import net.kyori.adventure.text.Component;

import java.io.IOException;
import java.util.Set;

public class SwitchCommand extends AbstractTransferCommand {

    /**
     * Create a new command
     *
     * @param plugin The plugin
     */
    public SwitchCommand(CommonProxyPlugin plugin) {
        super(plugin, "switch");
    }

    @Override
    protected <T> void execute(CommandContext<T> context, BuildContext<T> buildContext, Server server) throws IOException {
        CommandSourceAccessor source = buildContext.mapSource(context.getSource());

        if (source.getPlayerName().isEmpty()) {
            source.sendFailure(Component.text("The switch command can only be used by players. Use the transfer command instead."));
            return;
        }

        execute(context, buildContext, server, Set.of(source.getPlayerName().get()), "You");
    }
}

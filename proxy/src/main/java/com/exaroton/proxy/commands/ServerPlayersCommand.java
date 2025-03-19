package com.exaroton.proxy.commands;

import com.exaroton.api.server.Server;
import com.exaroton.proxy.CommonProxyPlugin;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.kyori.adventure.text.Component;

import java.util.HashSet;
import java.util.Set;

public abstract class ServerPlayersCommand extends ServerCommand {
    private static final String ARGUMENT_PLAYERS = "players";

    /**
     * Create a new command
     *
     * @param plugin The plugin
     * @param name   The name of the subcommand
     */
    public ServerPlayersCommand(CommonProxyPlugin plugin, String name) {
        super(plugin, name);
    }

    @Override
    protected <T> ArgumentBuilder<T, ?> buildWithServer(BuildContext<T> buildContext, RequiredArgumentBuilder<T, ?> builder) {
        return super.buildWithServer(buildContext, builder)
                .then(RequiredArgumentBuilder.<T, String>argument(ARGUMENT_PLAYERS, StringArgumentType.greedyString())
                        .suggests((context, suggestions) -> {
                            PlayerParseResult existing = PlayerParseResult.tryParse(context);

                            for (String player : plugin.getPlayers()) {
                                if (!existing.contains(player) && existing.remainingCouldBeCompletedBy(player)) {
                                    suggestions.suggest(player);
                                }
                            }

                            return suggestions.buildFuture();
                        })
                        .executes(context -> this.executeWithServer(
                                context,
                                buildContext,
                                (source, server) -> execute(
                                        source,
                                        server,
                                        PlayerParseResult.parse(context).getPlayers(),
                                        "The players"
                                )
                        )));
    }

    @Override
    public void execute(CommandSourceAccessor source, Server server) throws Exception {
        if (source.getPlayerName().isEmpty()) {
            source.sendFailure(Component.text("A list of players has to be provided if the command is not executed by a player."));
            return;
        }

        execute(source, server, Set.of(source.getPlayerName().get()), "You");
    }

    /**
     * Execute the command
     *
     * @param source      The command source
     * @param server      The server to transfer players to
     * @param playerNames The names of the players to transfer
     * @param subject     A string describing who will be transferred (e.g. "You" or "The players")
     * @throws Exception If an error occurs while transferring players
     */
    protected abstract void execute(
            CommandSourceAccessor source,
            Server server,
            Set<String> playerNames,
            String subject
    ) throws Exception;

    private static class PlayerParseResult {
        private final Set<String> players = new HashSet<>();
        private String remaining = "";

        public static PlayerParseResult tryParse(CommandContext<?> context) throws CommandSyntaxException {
            try {
                return parse(context);
            } catch (Exception e) {
                return new PlayerParseResult("");
            }
        }

        public static PlayerParseResult parse(CommandContext<?> context) throws CommandSyntaxException {
            var argument = context.getArgument(ARGUMENT_PLAYERS, String.class);
            return new PlayerParseResult(argument);
        }

        private PlayerParseResult(String remaining) throws CommandSyntaxException {
            var stringArg = StringArgumentType.string();
            var reader = new StringReader(remaining);
            while (reader.canRead()) {
                var next = reader.peek();
                if (!StringReader.isQuotedStringStart(next) && !StringReader.isAllowedInUnquotedString(next)) {
                    reader.skip();
                } else {
                    var player = stringArg.parse(reader);
                    players.add(player);
                    if (!reader.canRead()) {
                        this.remaining = player;
                    }
                }
            }
        }

        public Set<String> getPlayers() {
            return players;
        }

        public boolean contains(String player) {
            return players.contains(player);
        }

        public boolean remainingCouldBeCompletedBy(String other) {
            return other.toLowerCase().startsWith(remaining.toLowerCase());
        }
    }
}

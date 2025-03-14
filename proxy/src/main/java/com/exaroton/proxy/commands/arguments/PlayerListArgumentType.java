package com.exaroton.proxy.commands.arguments;

import com.exaroton.proxy.CommonProxyPlugin;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PlayerListArgumentType implements ArgumentType<PlayerList> {
    protected final CommonProxyPlugin plugin;

    public PlayerListArgumentType(CommonProxyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public PlayerList parse(StringReader reader) throws CommandSyntaxException {
        PlayerList result = new PlayerList();

        while (reader.canRead()) {
            result.add(reader.readString());
        }

        return result;
    }

    @Override
    public Collection<String> getExamples() {
        return List.of(
                "one two three",
                "on \"t w o\" three",
                "one \"\" three"
        );
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (String player : plugin.getPlayers()) {
            builder.suggest(player);
        }

        return builder.buildFuture();
    }
}

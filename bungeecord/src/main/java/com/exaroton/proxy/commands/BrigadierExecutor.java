package com.exaroton.proxy.commands;

import com.exaroton.proxy.Constants;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * A Utility class to execute and tab complete brigadier commands on platforms that do not natively support brigadier
 * @param <T> The command source type
 */
public class BrigadierExecutor<T> {
    /**
     * The brigadier command dispatcher
     */
    protected final CommandDispatcher<T> dispatcher;

    /**
     * The build context for the command
     */
    protected final BuildContext<T> context;

    /**
     * Create a new brigadier executor
     * @param dispatcher The brigadier command dispatcher
     * @param context The build context for the command
     */
    public BrigadierExecutor(CommandDispatcher<T> dispatcher, BuildContext<T> context) {
        this.dispatcher = dispatcher;
        this.context = context;
    }

    /**
     * Execute the command
     * @param source The command source
     * @param command The command name
     * @param args The command arguments
     */
    public void executeCommand(T source, String command, String[] args) {
        try {
            var parse = parse(command, args, source);
            dispatcher.execute(parse);
        } catch (CommandSyntaxException e) {
            var contextMessage = Component.text(exceptionContext(e), Style.style(TextDecoration.UNDERLINED));
            var error = Component.text(e.getType().toString())
                    .appendNewline()
                    .append(contextMessage)
                    .append(Component.text("<--[HERE]", Style.style(TextDecoration.ITALIC)));

            context.mapSource(source).sendFailure(error);
        }
    }

    /**
     * Get tab completions for the command
     * @param source The command source
     * @param command The command name
     * @param args The command arguments
     * @return A list of tab completions
     */
    public List<String> completeCommand(T source, String command, String[] args) {
        var parse = parse(command, args, source);
        try {
            var suggestions = dispatcher.getCompletionSuggestions(parse)
                    .get(1, TimeUnit.SECONDS);

            var result = new ArrayList<String>();

            for (var suggestion : suggestions.getList()) {
                result.add(suggestion.getText());
            }

            return result;
        } catch (Exception e) {
            Constants.LOG.error("Failed to get tab completions", e);
            return List.of();
        }
    }

    /**
     * Parse the command
     * @param command The command
     * @param args The command arguments
     * @param sender The command sender
     * @return The parse results
     */
    protected ParseResults<T> parse(String command, String[] args, T sender) {
        String input = command;
        if (args.length > 0) {
            input += " " + String.join(" ", args);
        }

        return dispatcher.parse(input, sender);
    }

    /**
     * A utility method to get the context of a command syntax exception
     * @param exception The exception
     * @return a string representing of the exception context for the chat
     */
    protected String exceptionContext(CommandSyntaxException exception) {
        final String input = exception.getInput();
        final StringBuilder builder = new StringBuilder();
        final int cursor = Math.min(input.length(), exception.getCursor());

        if (cursor > CommandSyntaxException.CONTEXT_AMOUNT) {
            builder.append("...");
        }

        builder.append(input, Math.max(0, cursor - CommandSyntaxException.CONTEXT_AMOUNT), cursor);

        return builder.toString();
    }
}

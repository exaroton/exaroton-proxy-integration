package com.exaroton.proxy.commands.arguments;

import java.util.*;

public class PlayerList {
    private final Collection<String> names = new ArrayList<>();

    public void add(String name) {
        names.add(name);
    }

    public Set<String> getPlayers(Collection<String> validNames) {
        Set<String> result = new HashSet<>();

        for (String name : names) {
            if (validNames.contains(name)) {
                result.add(name);
            }
        }

        return result;
    }
}

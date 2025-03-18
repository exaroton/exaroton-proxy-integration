package com.exaroton.proxy.commands.arguments;

import java.util.*;

public class PlayerList {
    private final Set<String> names = new HashSet<>();

    public void add(String name) {
        names.add(name);
    }

    public Set<String> getPlayers() {
        return names;
    }
}

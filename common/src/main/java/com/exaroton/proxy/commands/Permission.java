package com.exaroton.proxy.commands;

public enum Permission {
    BASE("exaroton"),
    START("exaroton.start"),
    STOP("exaroton.stop"),
    RESTART("exaroton.restart"),
    ADD("exaroton.add"),
    REMOVE("exaroton.remove"),
    SWITCH("exaroton.switch"),
    TRANSFER("exaroton.transfer"),
    ;

    private final String node;

    Permission(String node) {
        this.node = node;
    }

    /**
     * Returns the permission node for this permission.
     *
     * @return permission node
     */
    public String node() {
        return node;
    }
}

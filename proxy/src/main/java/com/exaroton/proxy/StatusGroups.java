package com.exaroton.proxy;

import com.exaroton.api.server.ServerStatus;

import java.util.Set;

public final class StatusGroups {
    public static final Set<ServerStatus> STARTABLE = Set.of(
            ServerStatus.OFFLINE,
            ServerStatus.CRASHED
    );

    public static final Set<ServerStatus> STARTING = Set.of(
            ServerStatus.STARTING,
            ServerStatus.LOADING,
            ServerStatus.RESTARTING,
            ServerStatus.PREPARING
    );

    public static final Set<ServerStatus> SWITCHABLE = Sets.union(
            STARTABLE,
            STARTING,
            Set.of(ServerStatus.ONLINE)
    );

    public static final Set<ServerStatus> RESTARTABLE = Set.of(
            ServerStatus.ONLINE
    );

    public static final Set<ServerStatus> STOPPABLE = Set.of(
            ServerStatus.STARTING,
            ServerStatus.ONLINE
    );

    private StatusGroups() {

    }
}

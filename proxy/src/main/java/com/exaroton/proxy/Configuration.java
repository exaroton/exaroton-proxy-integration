package com.exaroton.proxy;

import com.electronwill.nightconfig.core.serde.annotations.SerdeComment;
import com.electronwill.nightconfig.core.serde.annotations.SerdeSkipDeserializingIf;

public class Configuration {
    @SerdeComment(" exaroton API token. You can generate this on https://exaroton.com/account/")
    @SerdeSkipDeserializingIf({
            SerdeSkipDeserializingIf.SkipDeIf.IS_MISSING,
            SerdeSkipDeserializingIf.SkipDeIf.IS_NULL,
            SerdeSkipDeserializingIf.SkipDeIf.IS_EMPTY
    })
    String apiToken = "example-token";

    @SerdeComment(" Watch servers in the proxy config and automatically remove them when they go offline")
    @SerdeComment(" Note that this only works if you use .exaroton.me addresses in your velocity config.")
    @SerdeSkipDeserializingIf({
            SerdeSkipDeserializingIf.SkipDeIf.IS_MISSING,
            SerdeSkipDeserializingIf.SkipDeIf.IS_NULL,
            SerdeSkipDeserializingIf.SkipDeIf.IS_EMPTY
    })
    boolean watchServers = true;

    @SerdeComment(" Automatically start servers when the proxy starts")
    @SerdeSkipDeserializingIf({
            SerdeSkipDeserializingIf.SkipDeIf.IS_MISSING,
            SerdeSkipDeserializingIf.SkipDeIf.IS_NULL,
            SerdeSkipDeserializingIf.SkipDeIf.IS_EMPTY
    })
    AutoList autoStartServers = new AutoList();

    @SerdeComment(" Automatically stop servers when the proxy stops")
    @SerdeSkipDeserializingIf({
            SerdeSkipDeserializingIf.SkipDeIf.IS_MISSING,
            SerdeSkipDeserializingIf.SkipDeIf.IS_NULL,
            SerdeSkipDeserializingIf.SkipDeIf.IS_EMPTY
    })
    AutoList autoStopServers = new AutoList();

    public static class AutoList {
        @SerdeComment(" Enable or disable this feature")
        @SerdeSkipDeserializingIf({
                SerdeSkipDeserializingIf.SkipDeIf.IS_MISSING,
                SerdeSkipDeserializingIf.SkipDeIf.IS_NULL,
                SerdeSkipDeserializingIf.SkipDeIf.IS_EMPTY
        })
        boolean enabled = false;

        @SerdeComment(" List of server addresses, names or ids")
        @SerdeSkipDeserializingIf({
                SerdeSkipDeserializingIf.SkipDeIf.IS_MISSING,
                SerdeSkipDeserializingIf.SkipDeIf.IS_NULL,
                SerdeSkipDeserializingIf.SkipDeIf.IS_EMPTY
        })
        String[] servers = new String[] { "example.exaroton.me" };
    }
}

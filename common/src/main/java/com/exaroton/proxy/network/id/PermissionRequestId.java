package com.exaroton.proxy.network.id;

import com.google.common.io.ByteArrayDataInput;

/**
 * ID of a permission request
 */
public class PermissionRequestId extends NetworkId {
    public PermissionRequestId() {
    }

    public PermissionRequestId(ByteArrayDataInput input) {
        super(input);
    }
}

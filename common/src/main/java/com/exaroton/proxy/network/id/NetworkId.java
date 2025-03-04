package com.exaroton.proxy.network.id;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

/**
 * A unique identifier that can be used in plugin messages. This class uses a UUID internally.
 */
public abstract class NetworkId implements Comparable<NetworkId> {
    private final UUID id;

    /**
     * Create a new NetworkId with a random UUID
     */
    public NetworkId() {
        this(UUID.randomUUID());
    }

    /**
     * Deserialize a NetworkId from a ByteArrayDataInput
     * @param input input data
     */
    protected NetworkId(ByteArrayDataInput input) {
        this(new UUID(input.readLong(), input.readLong()));
    }

    private NetworkId(UUID id) {
        this.id = id;
    }

    /**
     * Serialize this NetworkId and write it to a ByteArrayDataOutput
     * @param output output data
     */
    public void serialize(ByteArrayDataOutput output) {
        output.writeLong(id.getMostSignificantBits());
        output.writeLong(id.getLeastSignificantBits());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        NetworkId that = (NetworkId) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int compareTo(@NotNull NetworkId o) {
        return id.compareTo(o.id);
    }
}

package io.korti.mrc.circuit;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum PortSignalMode {
    SINGLE(0),
    ANALOG(1),
    ;

    // Never reuse an id; always add new values at the end with the next available number.
    private static final Map<Integer, PortSignalMode> BY_ID =
            Arrays.stream(values()).collect(Collectors.toMap(m -> m.id, m -> m));

    public static final Codec<PortSignalMode> CODEC = Codec.STRING.comapFlatMap(
            s -> {
                try {
                    return DataResult.success(PortSignalMode.valueOf(s));
                } catch (IllegalArgumentException e) {
                    return DataResult.error(() -> "Unknown PortSignalMode: " + s);
                }
            },
            PortSignalMode::name
    );

    public final int id;

    PortSignalMode(int id) {
        this.id = id;
    }

    public static PortSignalMode byId(int id) {
        return BY_ID.getOrDefault(id, SINGLE);
    }
}

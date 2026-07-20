package io.korti.mrc.circuit;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum FaceMode {
    INPUT(0),
    OUTPUT(1),
    INACTIVE(2),
    ;

    // Never reuse an id; always add new values at the end with the next available number.
    private static final Map<Integer, FaceMode> BY_ID =
            Arrays.stream(values()).collect(Collectors.toMap(m -> m.id, m -> m));

    public final int id;

    FaceMode(int id) {
        this.id = id;
    }

    public static FaceMode byId(int id) {
        return BY_ID.getOrDefault(id, INACTIVE);
    }
}

package io.korti.mrc.circuit;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum ComponentType {
    EMPTY(0),
    DUST(1),
    TORCH(2),
    REPEATER(3),
    COMPARATOR(4),
    CIRCUIT_PLATE(5),
    BUNDLED_CABLE(6),
    SPLITTER(7),
    MERGER(8),
    NESTED_CIRCUIT(9),
    ;

    // Never reuse an id; always add new values at the end with the next available number.
    private static final Map<Integer, ComponentType> BY_ID =
            Arrays.stream(values()).collect(Collectors.toMap(t -> t.id, t -> t));

    public final int id;

    ComponentType(int id) { this.id = id; }

    public static ComponentType byId(int id) {
        return BY_ID.getOrDefault(id, EMPTY);
    }
}

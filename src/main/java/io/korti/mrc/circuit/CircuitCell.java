package io.korti.mrc.circuit;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

public record CircuitCell(
        ComponentType type,
        Direction facing,
        CompoundTag nestedCircuitNbt
) {
    private static final CompoundTag EMPTY_NBT = new CompoundTag();

    public static CircuitCell empty() {
        return new CircuitCell(ComponentType.EMPTY, Direction.NORTH, EMPTY_NBT);
    }

    public boolean hasNestedCircuit() {
        return !nestedCircuitNbt.isEmpty();
    }
}

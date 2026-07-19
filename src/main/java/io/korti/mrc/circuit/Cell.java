package io.korti.mrc.circuit;

import net.minecraft.core.Direction;

/**
 * One slot in the Circuit Plate's 5×5×2 grid.
 *
 * <p>Each cell holds the type of redstone component placed there and the horizontal
 * direction it faces. {@link ComponentType#EMPTY} cells always face {@link Direction#NORTH}
 * by convention; the facing has no gameplay meaning until a component is placed.
 *
 * @param type   the component occupying this slot
 * @param facing the horizontal direction the component faces (NORTH/SOUTH/EAST/WEST)
 */
public record Cell(ComponentType type, Direction facing) {
    public static Cell empty() {
        return new Cell(ComponentType.EMPTY, Direction.NORTH);
    }
}

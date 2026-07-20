package io.korti.mrc.circuit;

public enum CircuitTier {
    MK1(1, 3),
    MK2(2, 5),
    MK3(3, 7),
    ;

    private final int level;
    private final int gridSize;

    CircuitTier(int level, int gridSize) {
        this.level = level;
        this.gridSize = gridSize;
    }

    public int level() {
        return level;
    }

    public int gridSize() {
        return gridSize;
    }
}

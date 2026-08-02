package io.korti.mrc.client;

public final class ClientLayerState {

    private ClientLayerState() {}

    public static int currentLayer = 0;
    // indexed by CircuitTier.level() - 1: [0]=MK1, [1]=MK2, [2]=MK3
    public static final int[] blockLayerByTier = new int[3];
}

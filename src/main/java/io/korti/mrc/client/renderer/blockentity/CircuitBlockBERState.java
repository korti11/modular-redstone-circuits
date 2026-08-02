package io.korti.mrc.client.renderer.blockentity;

import io.korti.mrc.circuit.FaceMode;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.jetbrains.annotations.Nullable;

public class CircuitBlockBERState extends BlockEntityRenderState {
    static final int MAX_GRID = 7;
    // [face][i][j] — null means empty; face indexed by Direction.ordinal()
    public final @Nullable ItemStackRenderState[][][] faceItems =
            new ItemStackRenderState[6][MAX_GRID][MAX_GRID];
    public final FaceMode[] faceModes = new FaceMode[6];
    // non-null when face is INPUT or OUTPUT
    public final @Nullable ItemStackRenderState[] faceModeIndicators =
            new ItemStackRenderState[6];
    public int gridSize = 0;
}

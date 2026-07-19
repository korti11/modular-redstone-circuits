package io.korti.mrc.client.renderer.blockentity;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.jetbrains.annotations.Nullable;

public class CircuitPlateBERState extends BlockEntityRenderState {
    // [x][z][layer] — null means the cell is empty; populated in extractRenderState
    public final @Nullable ItemStackRenderState[][][] cells = new ItemStackRenderState[5][5][2];
}

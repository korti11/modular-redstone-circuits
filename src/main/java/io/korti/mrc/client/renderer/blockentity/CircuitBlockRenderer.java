package io.korti.mrc.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.korti.mrc.blockentity.CircuitBlockEntity;
import io.korti.mrc.circuit.CircuitCell;
import io.korti.mrc.circuit.CircuitData;
import io.korti.mrc.circuit.ComponentType;
import io.korti.mrc.circuit.FaceMode;
import io.korti.mrc.item.ModItems;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class CircuitBlockRenderer implements BlockEntityRenderer<CircuitBlockEntity, CircuitBlockBERState> {

    private final ItemModelResolver itemModelResolver;

    public CircuitBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public CircuitBlockBERState createRenderState() {
        return new CircuitBlockBERState();
    }

    @Override
    public void extractRenderState(CircuitBlockEntity be, CircuitBlockBERState state,
            float partialTicks, Vec3 cameraPos,
            ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(be, state, breakProgress);
        int size = be.getGridSize();
        state.gridSize = size;

        for (int f = 0; f < 6; f++) {
            for (int i = 0; i < CircuitBlockBERState.MAX_GRID; i++) {
                for (int j = 0; j < CircuitBlockBERState.MAX_GRID; j++) {
                    state.faceItems[f][i][j] = null;
                }
            }
            state.faceModeIndicators[f] = null;
        }

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                // UP (ordinal 1): outermost Y layer, [x][z] -> faceItems[1][i][j]
                resolveCell(be.getCell(i, size - 1, j), state.faceItems[1], i, j, 49 + i * 7 + j);
                // DOWN (ordinal 0): bottom Y layer, [x][z] -> faceItems[0][i][j]
                resolveCell(be.getCell(i, 0, j), state.faceItems[0], i, j, i * 7 + j);
                // NORTH (ordinal 2): outermost -Z face, [x][y] -> faceItems[2][i][j]
                resolveCell(be.getCell(i, j, 0), state.faceItems[2], i, j, 98 + i * 7 + j);
                // SOUTH (ordinal 3): outermost +Z face, [x][y] -> faceItems[3][i][j]
                resolveCell(be.getCell(i, j, size - 1), state.faceItems[3], i, j, 147 + i * 7 + j);
                // WEST (ordinal 4): outermost -X face, [z][y] -> faceItems[4][i][j]
                resolveCell(be.getCell(0, j, i), state.faceItems[4], i, j, 196 + i * 7 + j);
                // EAST (ordinal 5): outermost +X face, [z][y] -> faceItems[5][i][j]
                resolveCell(be.getCell(size - 1, j, i), state.faceItems[5], i, j, 245 + i * 7 + j);
            }
        }

        for (Direction dir : Direction.values()) {
            FaceMode mode = be.getFaceMode(dir);
            state.faceModes[dir.ordinal()] = mode;
            if (mode == FaceMode.INACTIVE) {
                continue;
            }
            Item item = mode == FaceMode.INPUT
                    ? ModItems.REDSTONE_WIRE_CELL.get()
                    : ModItems.REDSTONE_TORCH_CELL.get();
            ItemStackRenderState irs = new ItemStackRenderState();
            itemModelResolver.updateForTopItem(irs, new ItemStack(item),
                    ItemDisplayContext.FIXED, null, null, dir.ordinal());
            state.faceModeIndicators[dir.ordinal()] = irs;
        }
    }

    private void resolveCell(CircuitCell cell, @Nullable ItemStackRenderState[][] target,
            int i, int j, int seed) {
        if (cell.type() == ComponentType.EMPTY) {
            target[i][j] = null;
            return;
        }
        Item item;
        if (cell.type() == ComponentType.NESTED_CIRCUIT) {
            int tierLevel = CircuitData.fromNbt(cell.nestedCircuitNbt()).tier();
            item = itemForCircuitTier(tierLevel);
        } else {
            item = ModItems.getItemForType(cell.type());
        }
        if (item == Items.AIR) {
            target[i][j] = null;
            return;
        }
        ItemStackRenderState irs = new ItemStackRenderState();
        itemModelResolver.updateForTopItem(irs, new ItemStack(item),
                ItemDisplayContext.FIXED, null, null, seed);
        target[i][j] = irs;
    }

    private static Item itemForCircuitTier(int tierLevel) {
        return switch (tierLevel) {
            case 1 -> ModItems.CIRCUIT_BLOCK_MK1_ITEM.get();
            case 2 -> ModItems.CIRCUIT_BLOCK_MK2_ITEM.get();
            case 3 -> ModItems.CIRCUIT_BLOCK_MK3_ITEM.get();
            default -> Items.AIR;
        };
    }

    @Override
    public void submit(CircuitBlockBERState state, PoseStack poseStack,
            SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.gridSize == 0) {
            return;
        }
        float cellSize = 1.0f / state.gridSize;
        float itemScale = cellSize * 0.7f;

        for (Direction dir : Direction.values()) {
            int f = dir.ordinal();
            for (int i = 0; i < state.gridSize; i++) {
                for (int j = 0; j < state.gridSize; j++) {
                    ItemStackRenderState irs = state.faceItems[f][i][j];
                    if (irs == null || irs.isEmpty()) {
                        continue;
                    }
                    float ci = (i + 0.5f) * cellSize;
                    float cj = (j + 0.5f) * cellSize;
                    poseStack.pushPose();
                    applyFaceTransform(dir, poseStack, ci, cj, 0.001f);
                    poseStack.scale(itemScale, itemScale, itemScale);
                    irs.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
                    poseStack.popPose();
                }
            }
            ItemStackRenderState indicator = state.faceModeIndicators[f];
            if (indicator != null && !indicator.isEmpty()) {
                poseStack.pushPose();
                applyFaceTransform(dir, poseStack, 0.5f, 0.5f, 0.002f);
                poseStack.scale(0.15f, 0.15f, 0.15f);
                indicator.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
                poseStack.popPose();
            }
        }
    }

    // ci and cj are the two variable-axis face coordinates (matching the projection in extractRenderState).
    // offset is the small distance to push items beyond the block surface to avoid z-fighting.
    // Mapping: UP/DOWN → ci=x, cj=z; NORTH/SOUTH → ci=x, cj=y; WEST/EAST → ci=z, cj=y.
    private static void applyFaceTransform(Direction dir, PoseStack poseStack,
            float ci, float cj, float offset) {
        switch (dir) {
            case UP -> {
                poseStack.translate(ci, 1.0f + offset, cj);
                poseStack.mulPose(Axis.XP.rotationDegrees(-90.0f));
            }
            case DOWN -> {
                poseStack.translate(ci, -offset, cj);
                poseStack.mulPose(Axis.XP.rotationDegrees(90.0f));
            }
            case NORTH -> {
                poseStack.translate(ci, cj, -offset);
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0f));
            }
            case SOUTH -> poseStack.translate(ci, cj, 1.0f + offset);
            case WEST -> {
                poseStack.translate(-offset, cj, ci);
                poseStack.mulPose(Axis.YP.rotationDegrees(-90.0f));
            }
            case EAST -> {
                poseStack.translate(1.0f + offset, cj, ci);
                poseStack.mulPose(Axis.YP.rotationDegrees(90.0f));
            }
        }
    }
}

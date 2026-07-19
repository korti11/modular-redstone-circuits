package io.korti.mrc.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.korti.mrc.blockentity.CircuitPlateBlockEntity;
import io.korti.mrc.circuit.ComponentType;
import io.korti.mrc.item.ModItems;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class CircuitPlateRenderer implements BlockEntityRenderer<CircuitPlateBlockEntity, CircuitPlateBERState> {

    private static final float CELL_SIZE = 1.0f / 5.0f;
    private static final float ITEM_SCALE = 0.3f;

    private final ItemModelResolver itemModelResolver;

    public CircuitPlateRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public CircuitPlateBERState createRenderState() {
        return new CircuitPlateBERState();
    }

    @Override
    public void extractRenderState(CircuitPlateBlockEntity be, CircuitPlateBERState state,
            float partialTicks, Vec3 cameraPos,
            ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(be, state, breakProgress);
        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                for (int layer = 0; layer < 2; layer++) {
                    var cell = be.getCell(x, z, layer);
                    if (cell.type() == ComponentType.EMPTY) {
                        state.cells[x][z][layer] = null;
                        continue;
                    }
                    Item item = ModItems.getItemForType(cell.type());
                    if (item == Items.AIR) {
                        state.cells[x][z][layer] = null;
                        continue;
                    }
                    // TODO: when signal state exists, check powered flag and tint cells red here
                    ItemStackRenderState irs = new ItemStackRenderState();
                    itemModelResolver.updateForTopItem(irs, new ItemStack(item),
                            ItemDisplayContext.FIXED, null, null, x * 25 + z * 5 + layer);
                    state.cells[x][z][layer] = irs;
                }
            }
        }
    }

    @Override
    public void submit(CircuitPlateBERState state, PoseStack poseStack,
            SubmitNodeCollector collector, CameraRenderState camera) {
        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                for (int layer = 0; layer < 2; layer++) {
                    ItemStackRenderState irs = state.cells[x][z][layer];
                    if (irs == null || irs.isEmpty()) continue;
                    poseStack.pushPose();
                    // Center of cell on the block's top face; layer 1 floats 0.08 blocks above layer 0
                    poseStack.translate(
                            (x + 0.5f) * CELL_SIZE,
                            0.502f + layer * 0.08f,
                            (z + 0.5f) * CELL_SIZE);
                    // Rotate item to lie flat on the top face (item plane XY → XZ)
                    poseStack.mulPose(Axis.XP.rotationDegrees(-90.0f));
                    poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
                    irs.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
                    poseStack.popPose();
                }
            }
        }
    }
}

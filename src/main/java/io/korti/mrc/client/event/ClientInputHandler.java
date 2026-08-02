package io.korti.mrc.client.event;

import io.korti.mrc.ModularRedstoneCircuits;
import io.korti.mrc.block.CircuitBlock;
import io.korti.mrc.block.CircuitPlateBlock;
import io.korti.mrc.client.ClientLayerState;
import io.korti.mrc.network.C2SSetBlockLayerPacket;
import io.korti.mrc.network.C2SSetLayerPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

@EventBusSubscriber(modid = ModularRedstoneCircuits.MODID, value = Dist.CLIENT)
public final class ClientInputHandler {

    private ClientInputHandler() {}

    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        if (!mc.player.isShiftKeyDown()) return;
        if (!(mc.hitResult instanceof BlockHitResult bhr)) return;

        Block block = mc.level.getBlockState(bhr.getBlockPos()).getBlock();
        if (block instanceof CircuitPlateBlock) {
            ClientLayerState.currentLayer = 1 - ClientLayerState.currentLayer;
            event.setCanceled(true);
            ClientPacketDistributor.sendToServer(new C2SSetLayerPacket(ClientLayerState.currentLayer));
        } else if (block instanceof CircuitBlock cb) {
            int tierIdx = cb.getTier().level() - 1;
            int gridSize = cb.getTier().gridSize();
            int delta = event.getScrollDeltaY() > 0 ? 1 : -1;
            ClientLayerState.blockLayerByTier[tierIdx] =
                    Math.floorMod(ClientLayerState.blockLayerByTier[tierIdx] + delta, gridSize);
            event.setCanceled(true);
            ClientPacketDistributor.sendToServer(
                    new C2SSetBlockLayerPacket(cb.getTier().level(), ClientLayerState.blockLayerByTier[tierIdx]));
        }
    }

    @SubscribeEvent
    public static void onRenderGuiPost(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        if (!mc.player.isShiftKeyDown()) return;
        if (!(mc.hitResult instanceof BlockHitResult bhr)) return;

        Block block = mc.level.getBlockState(bhr.getBlockPos()).getBlock();
        String label;
        if (block instanceof CircuitPlateBlock) {
            label = ClientLayerState.currentLayer == 0 ? "Layer: 0 (Bottom)" : "Layer: 1 (Top)";
        } else if (block instanceof CircuitBlock cb) {
            int max = cb.getTier().gridSize() - 1;
            label = "Layer: " + ClientLayerState.blockLayerByTier[cb.getTier().level() - 1] + " / " + max;
        } else {
            return;
        }

        int x = mc.getWindow().getGuiScaledWidth() / 2 + 12;
        int y = mc.getWindow().getGuiScaledHeight() / 2 - 4;
        event.getGuiGraphics().text(mc.font, Component.literal(label), x, y, 0xFFFFFFFF);
    }
}

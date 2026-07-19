package io.korti.mrc.client.event;

import io.korti.mrc.ModularRedstoneCircuits;
import io.korti.mrc.block.CircuitPlateBlock;
import io.korti.mrc.client.ClientLayerState;
import io.korti.mrc.network.C2SSetLayerPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
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
        if (!(mc.level.getBlockState(bhr.getBlockPos()).getBlock() instanceof CircuitPlateBlock)) return;

        ClientLayerState.currentLayer = 1 - ClientLayerState.currentLayer;
        event.setCanceled(true);
        ClientPacketDistributor.sendToServer(new C2SSetLayerPacket(ClientLayerState.currentLayer));
    }

    @SubscribeEvent
    public static void onRenderGuiPost(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        if (!mc.player.isShiftKeyDown()) return;

        String label = ClientLayerState.currentLayer == 0 ? "Layer: 0 (Bottom)" : "Layer: 1 (Top)";
        int x = mc.getWindow().getGuiScaledWidth() / 2 + 12;
        int y = mc.getWindow().getGuiScaledHeight() / 2 - 4;
        event.getGuiGraphics().text(mc.font, Component.literal(label), x, y, 0xFFFFFFFF);
    }
}

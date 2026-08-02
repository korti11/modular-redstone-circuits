package io.korti.mrc.network;

import io.korti.mrc.attachment.ModAttachments;
import net.minecraft.util.Mth;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;


public class ModPayloads {

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ModPayloads::onRegisterPayloads);
    }

    private static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                C2SSetLayerPacket.TYPE,
                C2SSetLayerPacket.STREAM_CODEC,
                ModPayloads::handleSetLayer);
        registrar.playToServer(
                C2SSetBlockLayerPacket.TYPE,
                C2SSetBlockLayerPacket.STREAM_CODEC,
                ModPayloads::handleSetBlockLayer);
    }

    private static void handleSetLayer(C2SSetLayerPacket packet, IPayloadContext context) {
        context.player().setData(ModAttachments.SELECTED_LAYER.get(), Mth.clamp(packet.layer(), 0, 1));
    }

    private static void handleSetBlockLayer(C2SSetBlockLayerPacket packet, IPayloadContext context) {
        int layer = packet.layer();
        switch (packet.tierLevel()) {
            case 1 -> context.player().setData(ModAttachments.SELECTED_BLOCK_LAYER_MK1.get(), Mth.clamp(layer, 0, 2));
            case 2 -> context.player().setData(ModAttachments.SELECTED_BLOCK_LAYER_MK2.get(), Mth.clamp(layer, 0, 4));
            case 3 -> context.player().setData(ModAttachments.SELECTED_BLOCK_LAYER_MK3.get(), Mth.clamp(layer, 0, 6));
        }
    }
}

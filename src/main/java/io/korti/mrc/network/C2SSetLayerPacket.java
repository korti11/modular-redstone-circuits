package io.korti.mrc.network;

import io.korti.mrc.ModularRedstoneCircuits;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record C2SSetLayerPacket(int layer) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<C2SSetLayerPacket> TYPE =
            new CustomPacketPayload.Type<>(
                    Identifier.fromNamespaceAndPath(ModularRedstoneCircuits.MODID, "set_layer"));

    public static final StreamCodec<ByteBuf, C2SSetLayerPacket> STREAM_CODEC =
            ByteBufCodecs.VAR_INT.map(C2SSetLayerPacket::new, C2SSetLayerPacket::layer);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

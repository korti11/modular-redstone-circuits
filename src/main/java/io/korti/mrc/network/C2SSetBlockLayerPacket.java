package io.korti.mrc.network;

import io.korti.mrc.ModularRedstoneCircuits;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record C2SSetBlockLayerPacket(int tierLevel, int layer) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<C2SSetBlockLayerPacket> TYPE =
            new CustomPacketPayload.Type<>(
                    Identifier.fromNamespaceAndPath(ModularRedstoneCircuits.MODID, "set_block_layer"));

    public static final StreamCodec<ByteBuf, C2SSetBlockLayerPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, C2SSetBlockLayerPacket::tierLevel,
                    ByteBufCodecs.VAR_INT, C2SSetBlockLayerPacket::layer,
                    C2SSetBlockLayerPacket::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

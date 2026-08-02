package io.korti.mrc.circuit;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

import java.util.List;

public record CircuitData(
        int tier,
        String name,
        List<CellData> grid,
        List<PortData> ports
) {
    public static final CircuitData EMPTY = new CircuitData(0, "", List.of(), List.of());

    // Codec is built recursively so CellData can embed a full CircuitData for nested circuits.
    public static final Codec<CircuitData> CODEC = Codec.recursive("CircuitData", selfCodec ->
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.fieldOf("tier").forGetter(CircuitData::tier),
                    Codec.STRING.fieldOf("name").forGetter(CircuitData::name),
                    CellData.codec(selfCodec).listOf().fieldOf("grid").forGetter(CircuitData::grid),
                    PortData.CODEC.listOf().fieldOf("ports").forGetter(CircuitData::ports)
            ).apply(instance, CircuitData::new))
    );

    public boolean isEmpty() {
        return this == EMPTY || (grid.isEmpty() && ports.isEmpty() && name.isEmpty());
    }

    public CompoundTag toNbt() {
        Tag tag = CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow();
        return tag instanceof CompoundTag ct ? ct : new CompoundTag();
    }

    public static CircuitData fromNbt(CompoundTag tag) {
        if (tag.isEmpty()) {
            return EMPTY;
        }
        return CODEC.parse(NbtOps.INSTANCE, tag).result().orElse(EMPTY);
    }
}

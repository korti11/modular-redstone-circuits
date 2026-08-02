package io.korti.mrc.circuit;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;

import java.util.Optional;

public record CellData(
        int x,
        int y,
        int z,
        ComponentType type,
        Direction facing,
        int channels,
        CircuitData nested
) {
    private static final Codec<ComponentType> COMPONENT_TYPE_CODEC = Codec.STRING.comapFlatMap(
            s -> {
                try {
                    return DataResult.success(ComponentType.valueOf(s));
                } catch (IllegalArgumentException e) {
                    return DataResult.error(() -> "Unknown ComponentType: " + s);
                }
            },
            ComponentType::name
    );

    public static CellData empty(int x, int y, int z) {
        return new CellData(x, y, z, ComponentType.EMPTY, Direction.NORTH, 0, CircuitData.EMPTY);
    }

    public static Codec<CellData> codec(Codec<CircuitData> circuitCodec) {
        return RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("x").forGetter(CellData::x),
                Codec.INT.fieldOf("y").forGetter(CellData::y),
                Codec.INT.fieldOf("z").forGetter(CellData::z),
                COMPONENT_TYPE_CODEC.fieldOf("type").forGetter(CellData::type),
                Direction.CODEC.fieldOf("facing").forGetter(CellData::facing),
                Codec.INT.optionalFieldOf("channels", 0).forGetter(CellData::channels),
                circuitCodec.optionalFieldOf("nested").forGetter(
                        cell -> cell.nested().isEmpty() ? Optional.empty() : Optional.of(cell.nested())
                )
        ).apply(instance, (x, y, z, type, facing, channels, nestedOpt) ->
                new CellData(x, y, z, type, facing, channels, nestedOpt.orElse(CircuitData.EMPTY))
        ));
    }
}

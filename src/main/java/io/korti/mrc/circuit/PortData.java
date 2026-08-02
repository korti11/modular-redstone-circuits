package io.korti.mrc.circuit;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;

public record PortData(
        Direction face,
        FaceMode direction,
        PortSignalMode mode,
        String name
) {
    private static final Codec<FaceMode> FACE_MODE_CODEC = Codec.STRING.comapFlatMap(
            s -> {
                try {
                    return DataResult.success(FaceMode.valueOf(s));
                } catch (IllegalArgumentException e) {
                    return DataResult.error(() -> "Unknown FaceMode: " + s);
                }
            },
            FaceMode::name
    );

    public static final PortData INACTIVE = new PortData(Direction.NORTH, FaceMode.INACTIVE, PortSignalMode.SINGLE, "");

    public static final Codec<PortData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Direction.CODEC.fieldOf("face").forGetter(PortData::face),
            FACE_MODE_CODEC.fieldOf("direction").forGetter(PortData::direction),
            PortSignalMode.CODEC.fieldOf("mode").forGetter(PortData::mode),
            Codec.STRING.fieldOf("name").forGetter(PortData::name)
    ).apply(instance, PortData::new));
}

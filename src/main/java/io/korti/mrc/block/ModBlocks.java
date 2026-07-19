package io.korti.mrc.block;

import io.korti.mrc.ModularRedstoneCircuits;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {

    private static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(ModularRedstoneCircuits.MODID);

    public static final DeferredBlock<CircuitPlateBlock> CIRCUIT_PLATE =
            BLOCKS.register("circuit_plate", id -> new CircuitPlateBlock(
                    BlockBehaviour.Properties.of()
                            .setId(ResourceKey.create(Registries.BLOCK, id))
                            .mapColor(MapColor.STONE)
                            .sound(SoundType.STONE)
                            .strength(1.5f, 6.0f)
                            .requiresCorrectToolForDrops()));

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}

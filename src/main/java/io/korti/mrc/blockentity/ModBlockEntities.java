package io.korti.mrc.blockentity;

import io.korti.mrc.ModularRedstoneCircuits;
import io.korti.mrc.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {

    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ModularRedstoneCircuits.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CircuitPlateBlockEntity>> CIRCUIT_PLATE =
            BLOCK_ENTITY_TYPES.register("circuit_plate", () ->
                    new BlockEntityType<>(CircuitPlateBlockEntity::new, ModBlocks.CIRCUIT_PLATE.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CircuitBlockEntity>> CIRCUIT_BLOCK =
            BLOCK_ENTITY_TYPES.register("circuit_block", () ->
                    new BlockEntityType<>(CircuitBlockEntity::new,
                            ModBlocks.CIRCUIT_BLOCK_MK1.get(),
                            ModBlocks.CIRCUIT_BLOCK_MK2.get(),
                            ModBlocks.CIRCUIT_BLOCK_MK3.get()));

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITY_TYPES.register(modEventBus);
    }
}

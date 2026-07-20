package io.korti.mrc.item;

import io.korti.mrc.ModularRedstoneCircuits;
import io.korti.mrc.block.ModBlocks;
import io.korti.mrc.circuit.ComponentType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    private static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(ModularRedstoneCircuits.MODID);

    public static final DeferredItem<BlockItem> CIRCUIT_PLATE_ITEM =
            registerBlockItem(ModBlocks.CIRCUIT_PLATE);

    public static final DeferredItem<BlockItem> CIRCUIT_BLOCK_MK1_ITEM =
            registerBlockItem(ModBlocks.CIRCUIT_BLOCK_MK1);

    public static final DeferredItem<BlockItem> CIRCUIT_BLOCK_MK2_ITEM =
            registerBlockItem(ModBlocks.CIRCUIT_BLOCK_MK2);

    public static final DeferredItem<BlockItem> CIRCUIT_BLOCK_MK3_ITEM =
            registerBlockItem(ModBlocks.CIRCUIT_BLOCK_MK3);

    public static final DeferredItem<ComponentCellItem> REDSTONE_WIRE_CELL =
            ITEMS.registerItem("redstone_wire_cell", props -> new ComponentCellItem(ComponentType.DUST, props));

    public static final DeferredItem<ComponentCellItem> REDSTONE_TORCH_CELL =
            ITEMS.registerItem("redstone_torch_cell", props -> new ComponentCellItem(ComponentType.TORCH, props));

    public static final DeferredItem<ComponentCellItem> REPEATER_CELL =
            ITEMS.registerItem("repeater_cell", props -> new ComponentCellItem(ComponentType.REPEATER, props));

    public static final DeferredItem<ComponentCellItem> COMPARATOR_CELL =
            ITEMS.registerItem("comparator_cell", props -> new ComponentCellItem(ComponentType.COMPARATOR, props));

    public static <T extends Block> DeferredItem<BlockItem> registerBlockItem(DeferredBlock<T> block) {
        return ITEMS.registerItem(block.getId().getPath(), props -> new BlockItem(block.get(), props));
    }

    public static void addToCreativeTab(BuildCreativeModeTabContentsEvent event) {
        ITEMS.getEntries().forEach(item -> event.accept(item.get()));
    }

    public static Item getItemForType(ComponentType type) {
        return switch (type) {
            case DUST -> REDSTONE_WIRE_CELL.get();
            case TORCH -> REDSTONE_TORCH_CELL.get();
            case REPEATER -> REPEATER_CELL.get();
            case COMPARATOR -> COMPARATOR_CELL.get();
            default -> Items.AIR;
        };
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}

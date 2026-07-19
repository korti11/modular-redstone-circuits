package io.korti.mrc.item;

import io.korti.mrc.ModularRedstoneCircuits;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    private static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(ModularRedstoneCircuits.MODID);

    public static <T extends Block> DeferredItem<BlockItem> registerBlockItem(DeferredBlock<T> block) {
        return ITEMS.registerItem(block.getId().getPath(), props -> new BlockItem(block.get(), props));
    }

    public static void addToCreativeTab(BuildCreativeModeTabContentsEvent event) {
        ITEMS.getEntries().forEach(item -> event.accept(item.get()));
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}

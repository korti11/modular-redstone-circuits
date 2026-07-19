package io.korti.mrc.creativetab;

import io.korti.mrc.ModularRedstoneCircuits;
import io.korti.mrc.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {

    private static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ModularRedstoneCircuits.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MRC_TAB =
            CREATIVE_TABS.register("mrc_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.modularredstonecircuits"))
                    .icon(() -> new ItemStack(Items.REDSTONE))
                    .build());

    static void buildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == MRC_TAB.getKey()) {
            ModItems.addToCreativeTab(event);
        }
    }

    public static void register(IEventBus modEventBus) {
        CREATIVE_TABS.register(modEventBus);
        modEventBus.addListener(ModCreativeTabs::buildContents);
    }
}

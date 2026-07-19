package io.korti.mrc;

import com.mojang.logging.LogUtils;
import io.korti.mrc.block.ModBlocks;
import io.korti.mrc.blockentity.ModBlockEntities;
import io.korti.mrc.item.ModItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(ModularRedstoneCircuits.MODID)
public class ModularRedstoneCircuits {
    public static final String MODID = "modularredstonecircuits";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ModularRedstoneCircuits(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlockEntities.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}

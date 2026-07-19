package io.korti.mrc;

import com.mojang.logging.LogUtils;
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
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}

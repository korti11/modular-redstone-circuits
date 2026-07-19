package io.korti.mrc;

import io.korti.mrc.blockentity.ModBlockEntities;
import io.korti.mrc.client.renderer.blockentity.CircuitPlateRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = ModularRedstoneCircuits.MODID, dist = Dist.CLIENT)
public class ModularRedstoneCircuitsClient {
    public ModularRedstoneCircuitsClient(IEventBus modEventBus, ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        modEventBus.addListener(ModularRedstoneCircuitsClient::onRegisterRenderers);
    }

    private static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.CIRCUIT_PLATE.get(), CircuitPlateRenderer::new);
    }
}

package io.korti.mrc.datagen;

import io.korti.mrc.ModularRedstoneCircuits;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Set;

@EventBusSubscriber(modid = ModularRedstoneCircuits.MODID)
public class DataGenerators {

    // In NeoForge 26.2, the data run uses clientData() which only fires GatherDataEvent.Client.
    // GatherDataEvent.Client exposes getLookupProvider(), so all providers (models, tags,
    // loot tables) are registered here and generated in a single runData invocation.
    @SubscribeEvent
    public static void onClientData(GatherDataEvent.Client event) {
        PackOutput output = event.getGenerator().getPackOutput();
        var lookup = event.getLookupProvider();

        event.addProvider(new ModModelProvider(output));
        event.addProvider(new ModBlockTagsProvider(output, lookup));
        event.addProvider(new LootTableProvider(output, Set.of(),
                List.of(new LootTableProvider.SubProviderEntry(
                        ModBlockLootTableProvider::new, LootContextParamSets.BLOCK)),
                lookup));
    }
}

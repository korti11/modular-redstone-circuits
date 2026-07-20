package io.korti.mrc.datagen;

import io.korti.mrc.ModularRedstoneCircuits;
import io.korti.mrc.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {

    public ModBlockTagsProvider(PackOutput output,
                                CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, ModularRedstoneCircuits.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.CIRCUIT_PLATE.getKey())
                .add(ModBlocks.CIRCUIT_BLOCK_MK1.getKey())
                .add(ModBlocks.CIRCUIT_BLOCK_MK2.getKey())
                .add(ModBlocks.CIRCUIT_BLOCK_MK3.getKey());
    }
}

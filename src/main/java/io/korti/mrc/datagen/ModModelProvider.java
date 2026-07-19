package io.korti.mrc.datagen;

import io.korti.mrc.ModularRedstoneCircuits;
import io.korti.mrc.block.ModBlocks;
import io.korti.mrc.item.ModItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Blocks;

public class ModModelProvider extends ModelProvider {

    public ModModelProvider(PackOutput output) {
        super(output, ModularRedstoneCircuits.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        TextureMapping stoneTextures = new TextureMapping()
                .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(Blocks.STONE))
                .put(TextureSlot.TOP,    TextureMapping.getBlockTexture(Blocks.STONE))
                .put(TextureSlot.SIDE,   TextureMapping.getBlockTexture(Blocks.STONE));

        var modelId = ModelTemplates.SLAB_BOTTOM.create(
                ModBlocks.CIRCUIT_PLATE.get(), stoneTextures, blockModels.modelOutput);

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(ModBlocks.CIRCUIT_PLATE.get(),
                        BlockModelGenerators.plainVariant(modelId)));

        itemModels.generateFlatItem(ModItems.REDSTONE_WIRE_CELL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.REDSTONE_TORCH_CELL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.REPEATER_CELL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.COMPARATOR_CELL.get(), ModelTemplates.FLAT_ITEM);
    }
}

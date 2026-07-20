package io.korti.mrc.block;

import io.korti.mrc.blockentity.CircuitBlockEntity;
import io.korti.mrc.circuit.CircuitTier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class CircuitBlock extends Block implements EntityBlock {

    private final CircuitTier tier;

    public CircuitBlock(CircuitTier tier, BlockBehaviour.Properties props) {
        super(props);
        this.tier = tier;
    }

    public CircuitTier getTier() {
        return tier;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CircuitBlockEntity(pos, state);
    }
}

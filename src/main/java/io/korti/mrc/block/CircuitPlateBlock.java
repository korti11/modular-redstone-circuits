package io.korti.mrc.block;

import io.korti.mrc.attachment.ModAttachments;
import io.korti.mrc.blockentity.CircuitPlateBlockEntity;
import io.korti.mrc.circuit.Cell;
import io.korti.mrc.circuit.ComponentType;
import io.korti.mrc.item.ComponentCellItem;
import io.korti.mrc.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;
import java.util.WeakHashMap;

public class CircuitPlateBlock extends Block implements EntityBlock {

    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 8, 16);

    private static final Map<Player, Long> attackCooldown = new WeakHashMap<>();

    public CircuitPlateBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CircuitPlateBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
            BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(stack.getItem() instanceof ComponentCellItem cci)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        int[] cell = hitToCell(hit, pos);

        if (!level.isClientSide()) {
            int layer = player.getData(ModAttachments.SELECTED_LAYER.get());
            if (level.getBlockEntity(pos) instanceof CircuitPlateBlockEntity be) {
                if (be.getCell(cell[0], cell[1], layer).type() != ComponentType.EMPTY) {
                    return InteractionResult.FAIL;
                }
                be.setCell(cell[0], cell[1], layer,
                        new Cell(cci.getComponentType(), player.getDirection()));
                stack.consume(1, player);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (level.isClientSide()) return;

        long now = level.getGameTime();
        Long last = attackCooldown.get(player);
        if (last != null && now - last < 5L) return;
        attackCooldown.put(player, now);

        Vec3 eye = player.getEyePosition();
        Vec3 end = eye.add(player.getLookAngle().scale(6.0));
        BlockHitResult bhr = level.clip(
                new ClipContext(eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));

        if (bhr.getType() != HitResult.Type.BLOCK || !bhr.getBlockPos().equals(pos)) return;

        int[] cell = hitToCell(bhr, pos);
        int layer = player.getData(ModAttachments.SELECTED_LAYER.get());

        if (!(level.getBlockEntity(pos) instanceof CircuitPlateBlockEntity be)) return;

        Cell current = be.getCell(cell[0], cell[1], layer);
        if (current.type() == ComponentType.EMPTY) return;

        if (!player.getAbilities().instabuild) {
            Item item = ModItems.getItemForType(current.type());
            if (item != Items.AIR) {
                ItemStack returnStack = new ItemStack(item);
                if (!player.getInventory().add(returnStack)) {
                    Block.popResource(level, pos, returnStack);
                }
            }
        }

        be.setCell(cell[0], cell[1], layer, Cell.empty());
    }

    private static int[] hitToCell(BlockHitResult hit, BlockPos pos) {
        Vec3 loc = hit.getLocation();
        double fx = ((loc.x - pos.getX()) % 1.0 + 1.0) % 1.0;
        double fz = ((loc.z - pos.getZ()) % 1.0 + 1.0) % 1.0;
        return new int[]{ Mth.clamp((int)(fx * 5.0), 0, 4),
                          Mth.clamp((int)(fz * 5.0), 0, 4) };
    }
}

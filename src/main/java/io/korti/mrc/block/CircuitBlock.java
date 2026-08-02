package io.korti.mrc.block;

import io.korti.mrc.attachment.ModAttachments;
import io.korti.mrc.blockentity.CircuitBlockEntity;
import io.korti.mrc.circuit.CircuitCell;
import io.korti.mrc.circuit.CircuitData;
import io.korti.mrc.circuit.CircuitTier;
import io.korti.mrc.circuit.ComponentType;
import io.korti.mrc.item.ComponentCellItem;
import io.korti.mrc.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class CircuitBlock extends Block implements EntityBlock {

    private final CircuitTier tier;

    private static final Map<Player, Long> attackCooldown = new WeakHashMap<>();

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

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
            BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        int size = tier.gridSize();
        int[] cell = hitToCell(hit, pos, size);

        if (stack.getItem() instanceof ComponentCellItem cci) {
            if (!level.isClientSide()) {
                int layer = getSelectedLayer(player, tier);
                if (level.getBlockEntity(pos) instanceof CircuitBlockEntity be) {
                    if (be.getCell(cell[0], layer, cell[1]).type() != ComponentType.EMPTY) {
                        return InteractionResult.FAIL;
                    }
                    be.setCell(cell[0], layer, cell[1],
                            new CircuitCell(cci.getComponentType(), player.getDirection(), new CompoundTag()));
                    stack.consume(1, player);
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (stack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof CircuitPlateBlock) {
            if (!level.isClientSide()) {
                int layer = getSelectedLayer(player, tier);
                if (level.getBlockEntity(pos) instanceof CircuitBlockEntity be) {
                    if (be.getCell(cell[0], layer, cell[1]).type() != ComponentType.EMPTY) {
                        return InteractionResult.FAIL;
                    }
                    be.setCell(cell[0], layer, cell[1],
                            new CircuitCell(ComponentType.CIRCUIT_PLATE, player.getDirection(), new CompoundTag()));
                    stack.consume(1, player);
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (stack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof CircuitBlock nestedBlock) {
            CircuitTier nestedTier = nestedBlock.getTier();
            if (nestedTier.level() >= tier.level()) {
                return InteractionResult.FAIL;
            }
            if (!level.isClientSide()) {
                int layer = getSelectedLayer(player, tier);
                if (level.getBlockEntity(pos) instanceof CircuitBlockEntity be) {
                    if (be.getCell(cell[0], layer, cell[1]).type() != ComponentType.EMPTY) {
                        return InteractionResult.FAIL;
                    }
                    CompoundTag nbt = new CircuitData(nestedTier.level(), "", List.of(), List.of()).toNbt();
                    be.setCell(cell[0], layer, cell[1],
                            new CircuitCell(ComponentType.NESTED_CIRCUIT, player.getDirection(), nbt));
                    stack.consume(1, player);
                }
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;
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

        int size = tier.gridSize();
        int[] cell = hitToCell(bhr, pos, size);
        int layer = getSelectedLayer(player, tier);

        if (!(level.getBlockEntity(pos) instanceof CircuitBlockEntity be)) return;

        CircuitCell current = be.getCell(cell[0], layer, cell[1]);
        if (current.type() == ComponentType.EMPTY) return;

        if (!player.getAbilities().instabuild) {
            Item item;
            if (current.type() == ComponentType.NESTED_CIRCUIT) {
                item = itemForCircuitTier(CircuitData.fromNbt(current.nestedCircuitNbt()).tier());
            } else {
                item = ModItems.getItemForType(current.type());
            }
            if (item != Items.AIR) {
                ItemStack returnStack = new ItemStack(item);
                if (!player.getInventory().add(returnStack)) {
                    Block.popResource(level, pos, returnStack);
                }
            }
        }

        be.setCell(cell[0], layer, cell[1], CircuitCell.empty());
    }

    private static Item itemForCircuitTier(int tierLevel) {
        return switch (tierLevel) {
            case 1 -> ModItems.CIRCUIT_BLOCK_MK1_ITEM.get();
            case 2 -> ModItems.CIRCUIT_BLOCK_MK2_ITEM.get();
            case 3 -> ModItems.CIRCUIT_BLOCK_MK3_ITEM.get();
            default -> Items.AIR;
        };
    }

    private static int getSelectedLayer(Player player, CircuitTier tier) {
        return switch (tier) {
            case MK1 -> Mth.clamp(player.getData(ModAttachments.SELECTED_BLOCK_LAYER_MK1.get()), 0, tier.gridSize() - 1);
            case MK2 -> Mth.clamp(player.getData(ModAttachments.SELECTED_BLOCK_LAYER_MK2.get()), 0, tier.gridSize() - 1);
            case MK3 -> Mth.clamp(player.getData(ModAttachments.SELECTED_BLOCK_LAYER_MK3.get()), 0, tier.gridSize() - 1);
        };
    }

    private static int[] hitToCell(BlockHitResult hit, BlockPos pos, int gridSize) {
        Vec3 loc = hit.getLocation();
        double fx = ((loc.x - pos.getX()) % 1.0 + 1.0) % 1.0;
        double fz = ((loc.z - pos.getZ()) % 1.0 + 1.0) % 1.0;
        // Hitting the +X or +Z face puts the location exactly at the block boundary,
        // which wraps to 0.0 after % 1.0 and maps to the opposite face. Clamp to max.
        if (hit.getDirection() == Direction.EAST) {
            fx = 1.0 - 1e-9;
        } else if (hit.getDirection() == Direction.SOUTH) {
            fz = 1.0 - 1e-9;
        }
        return new int[]{
            Mth.clamp((int)(fx * gridSize), 0, gridSize - 1),
            Mth.clamp((int)(fz * gridSize), 0, gridSize - 1)
        };
    }
}

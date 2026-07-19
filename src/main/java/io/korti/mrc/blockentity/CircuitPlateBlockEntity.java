package io.korti.mrc.blockentity;

import com.mojang.serialization.Codec;
import io.korti.mrc.circuit.Cell;
import io.korti.mrc.circuit.ComponentType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.ArrayList;
import java.util.List;

public class CircuitPlateBlockEntity extends BlockEntity {

    // indexed [x][z][layer], x/z ∈ 0..4, layer ∈ {0 = bottom, 1 = top}
    private final Cell[][][] grid = new Cell[5][5][2];

    public CircuitPlateBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CIRCUIT_PLATE.get(), pos, state);
        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                for (int layer = 0; layer < 2; layer++) {
                    grid[x][z][layer] = Cell.empty();
                }
            }
        }
    }

    public Cell getCell(int x, int z, int layer) {
        return grid[x][z][layer];
    }

    public void setCell(int x, int z, int layer, Cell cell) {
        grid[x][z][layer] = cell;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    /**
     * NBT layout written by this block entity:
     *
     * <pre>
     * "types"   : List&lt;Integer&gt; (50 elements) — ComponentType id per cell (see ComponentType.id)
     *                                            EMPTY=0, DUST=1, TORCH=2, REPEATER=3, COMPARATOR=4
     * "facings" : List&lt;Integer&gt; (50 elements) — horizontal direction id per cell (our own stable
     *                                            mapping, independent of Direction.get2DDataValue())
     *                                            NORTH=0, SOUTH=1, EAST=2, WEST=3
     * </pre>
     *
     * Flat index formula: {@code layer * 25 + x * 5 + z}
     * where x,z ∈ [0,4] and layer ∈ {0 = bottom, 1 = top}.
     */
    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("types", Codec.INT.listOf(), packTypes());
        output.store("facings", Codec.INT.listOf(), packFacings());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.read("types", Codec.INT.listOf()).ifPresent(this::unpackTypes);
        input.read("facings", Codec.INT.listOf()).ifPresent(this::unpackFacings);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void handleUpdateTag(ValueInput input) {
        loadAdditional(input);
    }

    private List<Integer> packTypes() {
        List<Integer> list = new ArrayList<>(50);
        for (int layer = 0; layer < 2; layer++) {
            for (int x = 0; x < 5; x++) {
                for (int z = 0; z < 5; z++) {
                    list.add(grid[x][z][layer].type().id);
                }
            }
        }
        return list;
    }

    private void unpackTypes(List<Integer> list) {
        if (list.size() < 50) {
            return;
        }
        int idx = 0;
        for (int layer = 0; layer < 2; layer++) {
            for (int x = 0; x < 5; x++) {
                for (int z = 0; z < 5; z++) {
                    grid[x][z][layer] = new Cell(
                            ComponentType.byId(list.get(idx++)),
                            grid[x][z][layer].facing()
                    );
                }
            }
        }
    }

    private List<Integer> packFacings() {
        List<Integer> list = new ArrayList<>(50);
        for (int layer = 0; layer < 2; layer++) {
            for (int x = 0; x < 5; x++) {
                for (int z = 0; z < 5; z++) {
                    list.add((int) directionToId(grid[x][z][layer].facing()));
                }
            }
        }
        return list;
    }

    private void unpackFacings(List<Integer> list) {
        if (list.size() < 50) {
            return;
        }
        int idx = 0;
        for (int layer = 0; layer < 2; layer++) {
            for (int x = 0; x < 5; x++) {
                for (int z = 0; z < 5; z++) {
                    grid[x][z][layer] = new Cell(
                            grid[x][z][layer].type(),
                            directionFromId(list.get(idx++).byteValue())
                    );
                }
            }
        }
    }

    private static byte directionToId(Direction dir) {
        return switch (dir) {
            case NORTH -> 0;
            case SOUTH -> 1;
            case EAST  -> 2;
            case WEST  -> 3;
            default    -> 0; // NORTH as fallback
        };
    }

    private static Direction directionFromId(byte id) {
        return switch (id) {
            case 1  -> Direction.SOUTH;
            case 2  -> Direction.EAST;
            case 3  -> Direction.WEST;
            default -> Direction.NORTH;
        };
    }
}

package io.korti.mrc.blockentity;

import com.mojang.serialization.Codec;
import io.korti.mrc.block.CircuitBlock;
import io.korti.mrc.circuit.CircuitCell;
import io.korti.mrc.circuit.CircuitTier;
import io.korti.mrc.circuit.ComponentType;
import io.korti.mrc.circuit.FaceMode;
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
import java.util.Arrays;
import java.util.List;

public class CircuitBlockEntity extends BlockEntity {

    private final CircuitTier tier;
    // [x][y][z], all indices in [0, gridSize-1]; y=0 is bottom layer
    private final CircuitCell[][][] grid;
    // indexed by Direction.ordinal() (DOWN=0, UP=1, NORTH=2, SOUTH=3, WEST=4, EAST=5)
    private final FaceMode[] faceAssignments;

    public CircuitBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CIRCUIT_BLOCK.get(), pos, state);
        this.tier = ((CircuitBlock) state.getBlock()).getTier();
        int size = tier.gridSize();
        this.grid = new CircuitCell[size][size][size];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int z = 0; z < size; z++) {
                    grid[x][y][z] = CircuitCell.empty();
                }
            }
        }
        this.faceAssignments = new FaceMode[6];
        Arrays.fill(faceAssignments, FaceMode.INACTIVE);
    }

    public CircuitTier getTier() {
        return tier;
    }

    public int getGridSize() {
        return tier.gridSize();
    }

    public CircuitCell getCell(int x, int y, int z) {
        return grid[x][y][z];
    }

    public void setCell(int x, int y, int z, CircuitCell cell) {
        grid[x][y][z] = cell;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public FaceMode getFaceMode(Direction dir) {
        return faceAssignments[dir.ordinal()];
    }

    public void setFaceMode(Direction dir, FaceMode mode) {
        faceAssignments[dir.ordinal()] = mode;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    /**
     * NBT layout:
     *
     * "types"   : List<Integer> (size³) — ComponentType.id per cell
     *                                     flat index: x*size²+y*size+z
     * "facings" : List<Integer> (size³) — direction id per cell (NORTH=0,SOUTH=1,EAST=2,WEST=3)
     *                                     flat index: x*size²+y*size+z
     * "faces"   : List<Integer> (6)     — FaceMode.id per face, indexed by Direction.ordinal()
     *
     * Tier is NOT persisted — always re-derived from the live BlockState on load.
     * nestedCircuitNbt serialization is deferred to the circuit-nesting task.
     */
    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        int size = tier.gridSize();
        output.store("types",   Codec.INT.listOf(), packTypes(size));
        output.store("facings", Codec.INT.listOf(), packFacings(size));
        output.store("faces",   Codec.INT.listOf(), packFaceAssignments());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        int size = tier.gridSize();
        input.read("types",   Codec.INT.listOf()).ifPresent(list -> unpackTypes(list, size));
        input.read("facings", Codec.INT.listOf()).ifPresent(list -> unpackFacings(list, size));
        input.read("faces",   Codec.INT.listOf()).ifPresent(this::unpackFaceAssignments);
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

    private List<Integer> packTypes(int size) {
        List<Integer> list = new ArrayList<>(size * size * size);
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int z = 0; z < size; z++) {
                    list.add(grid[x][y][z].type().id);
                }
            }
        }
        return list;
    }

    private void unpackTypes(List<Integer> list, int size) {
        int total = size * size * size;
        if (list.size() < total) {
            return;
        }
        int idx = 0;
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int z = 0; z < size; z++) {
                    grid[x][y][z] = new CircuitCell(
                            ComponentType.byId(list.get(idx++)),
                            grid[x][y][z].facing(),
                            grid[x][y][z].nestedCircuitNbt()
                    );
                }
            }
        }
    }

    private List<Integer> packFacings(int size) {
        List<Integer> list = new ArrayList<>(size * size * size);
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int z = 0; z < size; z++) {
                    list.add((int) directionToId(grid[x][y][z].facing()));
                }
            }
        }
        return list;
    }

    private void unpackFacings(List<Integer> list, int size) {
        int total = size * size * size;
        if (list.size() < total) {
            return;
        }
        int idx = 0;
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int z = 0; z < size; z++) {
                    grid[x][y][z] = new CircuitCell(
                            grid[x][y][z].type(),
                            directionFromId(list.get(idx++).byteValue()),
                            grid[x][y][z].nestedCircuitNbt()
                    );
                }
            }
        }
    }

    private List<Integer> packFaceAssignments() {
        List<Integer> list = new ArrayList<>(6);
        for (FaceMode mode : faceAssignments) {
            list.add(mode.id);
        }
        return list;
    }

    private void unpackFaceAssignments(List<Integer> list) {
        if (list.size() < 6) {
            return;
        }
        for (int i = 0; i < 6; i++) {
            faceAssignments[i] = FaceMode.byId(list.get(i));
        }
    }

    private static byte directionToId(Direction dir) {
        return switch (dir) {
            case NORTH -> 0;
            case SOUTH -> 1;
            case EAST  -> 2;
            case WEST  -> 3;
            default    -> 0;
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

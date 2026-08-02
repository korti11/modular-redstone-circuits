package io.korti.mrc.test;

import io.korti.mrc.ModularRedstoneCircuits;
import io.korti.mrc.circuit.CellData;
import io.korti.mrc.circuit.CircuitData;
import io.korti.mrc.circuit.ComponentType;
import io.korti.mrc.circuit.FaceMode;
import io.korti.mrc.circuit.PortData;
import io.korti.mrc.circuit.PortSignalMode;
import net.minecraft.core.Direction;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.gametest.GameTestHooks;

import java.util.List;

// Tests run during runGameTestServer (when GameTestHooks.isGametestEnabled() == true).
// Each test logs "[MRC Test] <name>: PASS" on success and throws on failure.
@EventBusSubscriber(modid = ModularRedstoneCircuits.MODID)
public class CircuitDataTests {

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        if (!GameTestHooks.isGametestEnabled()) return;
        event.enqueueWork(() -> {
            run("emptyRoundTrip",          CircuitDataTests::emptyRoundTrip);
            run("flatCircuitRoundTrip",    CircuitDataTests::flatCircuitRoundTrip);
            run("threeDeepNestingRoundTrip", CircuitDataTests::threeDeepNestingRoundTrip);
            run("bundledCableChannelsMask", CircuitDataTests::bundledCableChannelsMask);
        });
    }

    private static void run(String name, Runnable test) {
        try {
            test.run();
            ModularRedstoneCircuits.LOGGER.info("[MRC Test] {}: PASS", name);
        } catch (Exception e) {
            String msg = "[MRC Test] " + name + " FAIL: " + e.getMessage();
            ModularRedstoneCircuits.LOGGER.error(msg);
            throw new RuntimeException(msg, e);
        }
    }

    private static void emptyRoundTrip() {
        CircuitData result = CircuitData.fromNbt(CircuitData.EMPTY.toNbt());
        if (!result.isEmpty()) {
            throw new AssertionError("Expected isEmpty() after EMPTY round-trip");
        }
    }

    private static void flatCircuitRoundTrip() {
        CellData cell = new CellData(0, 0, 0, ComponentType.DUST, Direction.NORTH, 0, CircuitData.EMPTY);
        PortData port = new PortData(Direction.NORTH, FaceMode.INPUT, PortSignalMode.SINGLE, "in");
        CircuitData original = new CircuitData(1, "test", List.of(cell), List.of(port));

        CircuitData result = CircuitData.fromNbt(original.toNbt());

        if (result.tier() != 1) throw new AssertionError("tier mismatch: " + result.tier());
        if (!result.name().equals("test")) throw new AssertionError("name mismatch: " + result.name());
        if (result.grid().size() != 1) throw new AssertionError("grid size: " + result.grid().size());
        if (result.grid().get(0).type() != ComponentType.DUST) throw new AssertionError("cell type mismatch");
        if (result.grid().get(0).facing() != Direction.NORTH) throw new AssertionError("cell facing mismatch");
        if (result.ports().size() != 1) throw new AssertionError("ports size: " + result.ports().size());
        if (result.ports().get(0).direction() != FaceMode.INPUT) throw new AssertionError("port direction mismatch");
        if (result.ports().get(0).mode() != PortSignalMode.SINGLE) throw new AssertionError("port mode mismatch");
        if (!result.ports().get(0).name().equals("in")) throw new AssertionError("port name mismatch");
    }

    private static void threeDeepNestingRoundTrip() {
        CellData innerCell = new CellData(0, 0, 0, ComponentType.DUST, Direction.NORTH, 0, CircuitData.EMPTY);
        CircuitData inner = new CircuitData(1, "inner", List.of(innerCell), List.of());

        CellData midCell = new CellData(1, 0, 0, ComponentType.NESTED_CIRCUIT, Direction.NORTH, 0, inner);
        CircuitData mid = new CircuitData(2, "mid", List.of(midCell), List.of());

        CellData outerCell = new CellData(2, 0, 0, ComponentType.NESTED_CIRCUIT, Direction.NORTH, 0, mid);
        CircuitData outer = new CircuitData(3, "outer", List.of(outerCell), List.of());

        CircuitData result = CircuitData.fromNbt(outer.toNbt());

        if (result.grid().size() != 1) throw new AssertionError("outer grid lost");
        CircuitData resultMid = result.grid().get(0).nested();
        if (resultMid.isEmpty()) throw new AssertionError("mid level was lost");
        if (!resultMid.name().equals("mid")) throw new AssertionError("mid name: " + resultMid.name());

        if (resultMid.grid().size() != 1) throw new AssertionError("mid grid lost");
        CircuitData resultInner = resultMid.grid().get(0).nested();
        if (resultInner.isEmpty()) throw new AssertionError("inner level was lost");
        if (!resultInner.name().equals("inner")) throw new AssertionError("inner name: " + resultInner.name());
        if (resultInner.grid().get(0).type() != ComponentType.DUST) throw new AssertionError("inner DUST cell type lost");
    }

    private static void bundledCableChannelsMask() {
        int mask = 0b11001100;
        CellData cell = new CellData(0, 0, 0, ComponentType.BUNDLED_CABLE, Direction.NORTH, mask, CircuitData.EMPTY);
        CircuitData original = new CircuitData(1, "bitmask", List.of(cell), List.of());

        CircuitData result = CircuitData.fromNbt(original.toNbt());

        if (result.grid().size() != 1) throw new AssertionError("grid lost");
        int got = result.grid().get(0).channels();
        if (got != mask) throw new AssertionError("channels: expected " + mask + " got " + got);
    }
}

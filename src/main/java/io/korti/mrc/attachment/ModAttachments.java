package io.korti.mrc.attachment;

import io.korti.mrc.ModularRedstoneCircuits;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModAttachments {

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES,
                    ModularRedstoneCircuits.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> SELECTED_LAYER =
            ATTACHMENT_TYPES.register("selected_layer",
                    () -> AttachmentType.builder(() -> 0).build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> SELECTED_BLOCK_LAYER_MK1 =
            ATTACHMENT_TYPES.register("selected_block_layer_mk1",
                    () -> AttachmentType.builder(() -> 0).build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> SELECTED_BLOCK_LAYER_MK2 =
            ATTACHMENT_TYPES.register("selected_block_layer_mk2",
                    () -> AttachmentType.builder(() -> 0).build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> SELECTED_BLOCK_LAYER_MK3 =
            ATTACHMENT_TYPES.register("selected_block_layer_mk3",
                    () -> AttachmentType.builder(() -> 0).build());

    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }
}

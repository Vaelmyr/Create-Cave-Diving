package com.vaelmyr.create_cave_diving;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class AllDataComponents {
    private static final DeferredRegister.DataComponents REGISTER = DeferredRegister
        .createDataComponents(Registries.DATA_COMPONENT_TYPE, CreateCaveDiving.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>>
        INSTALLED_FILTER = REGISTER.registerComponentType(
            "installed_filter",
            builder -> builder
                .persistent(ResourceLocation.CODEC)
                .networkSynchronized(ResourceLocation.STREAM_CODEC)
        );

    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }

    private AllDataComponents() {
    }
}

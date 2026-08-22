package com.vaelmyr.create_cave_diving;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class AllCreativeModeTabs {
    private static final DeferredRegister<CreativeModeTab> REGISTER =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateCaveDiving.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BASE = REGISTER.register("base",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.create_cave_diving.base"))
            .withTabsBefore(com.simibubi.create.AllCreativeModeTabs.PALETTES_CREATIVE_TAB.getKey())
            .icon(AllItems.COPPER_RESPIRATOR::asStack)
            .displayItems((parameters, output) -> {
                output.accept(AllItems.COPPER_RESPIRATOR.get());
                output.accept(AllItems.NETHERITE_RESPIRATOR.get());

                output.accept(AllItems.CHARCOAL_FILTER.get());
                output.accept(AllItems.LAYERED_FILTER.get());
                output.accept(AllItems.COMPOSITE_FILTER.get());
            })
            .build());

    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }

    private AllCreativeModeTabs() {
    }
}

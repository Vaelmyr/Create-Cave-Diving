package com.vaelmyr.create_cave_diving.hazard;

import com.vaelmyr.create_cave_diving.CreateCaveDiving;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

@EventBusSubscriber(modid = CreateCaveDiving.MODID)
public final class HazardEvents {
    @SubscribeEvent
    public static void addReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new HazardRuleReloadListener());
    }

    private HazardEvents() {
    }
}

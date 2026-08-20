package com.vaelmyr.create_cave_diving.client;

import com.vaelmyr.create_cave_diving.AllModelLayers;
import com.vaelmyr.create_cave_diving.CreateCaveDiving;
import com.vaelmyr.create_cave_diving.client.model.RespiratorArmorModel;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = CreateCaveDiving.MODID, value = Dist.CLIENT)
public final class ClientEvents {
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(AllModelLayers.RESPIRATOR, RespiratorArmorModel::createLayer);
    }
}

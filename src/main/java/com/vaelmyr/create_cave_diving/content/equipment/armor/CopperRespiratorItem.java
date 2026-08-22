package com.vaelmyr.create_cave_diving.content.equipment.armor;

import com.vaelmyr.create_cave_diving.CreateCaveDiving;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterial;

public class CopperRespiratorItem extends BaseRespiratorItem {
    private static final ResourceLocation TEXTURE = CreateCaveDiving.asResource("textures/item/copper_respirator.png");

    public CopperRespiratorItem(
        Holder<ArmorMaterial> material,
        Properties properties
    ) {
        super(
            material,
            properties,
            TEXTURE
        );
    }
}

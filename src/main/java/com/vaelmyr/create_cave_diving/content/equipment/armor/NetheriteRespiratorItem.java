package com.vaelmyr.create_cave_diving.content.equipment.armor;

import com.vaelmyr.create_cave_diving.CreateCaveDiving;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterial;

public class NetheriteRespiratorItem extends BaseRespiratorItem {
    private static final ResourceLocation TEXTURE = CreateCaveDiving
            .asResource("textures/item/netherite_respirator.png");

    public NetheriteRespiratorItem(Holder<ArmorMaterial> material, Properties properties) {
        super(material, properties, 2, TEXTURE);
    }
}

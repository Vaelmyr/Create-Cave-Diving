package com.vaelmyr.create_cave_diving;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ArmorItem.Type;

import com.simibubi.create.content.equipment.armor.AllArmorMaterials;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.vaelmyr.create_cave_diving.content.equipment.armor.CopperRespiratorItem;
import com.vaelmyr.create_cave_diving.content.equipment.armor.NetheriteRespiratorItem;

public final class AllItems {
    public static final Registrate REGISTRATE = CreateCaveDiving.registrate();

    public static final ItemEntry<CopperRespiratorItem> COPPER_RESPIRATOR = REGISTRATE
            .item("copper_respirator", p -> new CopperRespiratorItem(AllArmorMaterials.COPPER, p))
            .properties(p -> p.durability(Type.HELMET.getDurability(7))).tag(ItemTags.HEAD_ARMOR).register();

    public static final ItemEntry<NetheriteRespiratorItem> NETHERITE_RESPIRATOR = REGISTRATE
            .item("netherite_respirator", p -> new NetheriteRespiratorItem(ArmorMaterials.NETHERITE, p))
            .properties(p -> p.durability(Type.HELMET.getDurability(37))).tag(ItemTags.HEAD_ARMOR).register();

    public static void register() {
    }

    private AllItems() {
    }
}

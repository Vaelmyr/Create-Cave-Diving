package com.vaelmyr.create_cave_diving.content.equipment.armor;

import java.util.function.Consumer;

import com.vaelmyr.create_cave_diving.AllModelLayers;
import com.vaelmyr.create_cave_diving.client.model.RespiratorArmorModel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public abstract class BaseRespiratorItem extends ArmorItem {
    public static final EquipmentSlot SLOT = EquipmentSlot.HEAD;
    public static final ArmorItem.Type TYPE = ArmorItem.Type.HELMET;

    private final int tier;
    private final ResourceLocation texture;

    private HumanoidModel<?> model;

    protected BaseRespiratorItem(Holder<ArmorMaterial> material, Properties properties, int tier,
            ResourceLocation texture) {
        super(material, TYPE, properties);

        this.tier = tier;
        this.texture = texture;
    }

    public int getTier() {
        return tier;
    }

    public static ItemStack getWornItem(Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity))
            return ItemStack.EMPTY;

        ItemStack stack = livingEntity.getItemBySlot(SLOT);
        if (!(stack.getItem() instanceof BaseRespiratorItem))
            return ItemStack.EMPTY;

        return stack;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot,
                    HumanoidModel<?> original) {

                if (model == null) {
                    ModelPart root = Minecraft.getInstance().getEntityModels().bakeLayer(AllModelLayers.RESPIRATOR);
                    model = new RespiratorArmorModel(root);
                }

                return model;
            }
        });
    }

    @Override
    public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot,
            ArmorMaterial.Layer layer, boolean innerModel) {
        return texture;
    }
}

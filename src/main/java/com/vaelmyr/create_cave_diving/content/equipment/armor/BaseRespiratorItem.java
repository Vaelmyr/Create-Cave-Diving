package com.vaelmyr.create_cave_diving.content.equipment.armor;

import java.util.List;
import java.util.function.Consumer;

import com.vaelmyr.create_cave_diving.AllDataComponents;
import com.vaelmyr.create_cave_diving.AllEventListeners;
import com.vaelmyr.create_cave_diving.AllModelLayers;
import com.vaelmyr.create_cave_diving.client.model.RespiratorArmorModel;
import com.vaelmyr.create_cave_diving.content.filter.RespiratorFilterManager;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public abstract class BaseRespiratorItem extends ArmorItem {
    public static final EquipmentSlot SLOT = EquipmentSlot.HEAD;
    public static final ArmorItem.Type TYPE = ArmorItem.Type.HELMET;

    private final ResourceLocation texture;

    private HumanoidModel<?> model;

    protected BaseRespiratorItem(
        Holder<ArmorMaterial> material,
        Properties properties,
        ResourceLocation texture
    ) {
        super(material, TYPE, properties);

        this.texture = texture;
    }

    public static ItemStack getWornItem(Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity))
            return ItemStack.EMPTY;

        ItemStack stack = livingEntity.getItemBySlot(SLOT);
        if (!(stack.getItem() instanceof BaseRespiratorItem))
            return ItemStack.EMPTY;

        return stack;
    }

    public static ResourceLocation getInstalledFilter(ItemStack respirator) {
        return respirator.get(AllDataComponents.INSTALLED_FILTER.get());
    }

    public static void setInstalledFilter(ItemStack respirator, ResourceLocation filterId) {
        respirator.set(AllDataComponents.INSTALLED_FILTER.get(), filterId);
    }

    public static ResourceLocation removeInstalledFilter(ItemStack respirator) {
        ResourceLocation filter = getInstalledFilter(respirator);

        if (filter != null)
            respirator.remove(AllDataComponents.INSTALLED_FILTER.get());

        return filter;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(
        Level level,
        Player player,
        InteractionHand hand
    ) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            ResourceLocation filter = getInstalledFilter(stack);

            if (filter != null) {
                if (!level.isClientSide()) {
                    removeInstalledFilter(stack);
                    AllEventListeners.giveFilterBack(player, filter);
                }

                return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
            }
        }

        return super.use(level, player, hand);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public HumanoidModel<?> getHumanoidArmorModel(
                LivingEntity entity,
                ItemStack stack,
                EquipmentSlot slot,
                HumanoidModel<?> original
            ) {
                if (model == null) {
                    ModelPart root = Minecraft
                        .getInstance()
                        .getEntityModels()
                        .bakeLayer(AllModelLayers.RESPIRATOR);

                    model = new RespiratorArmorModel(root);
                }

                return model;
            }
        });
    }

    @Override
    public ResourceLocation getArmorTexture(
        ItemStack stack,
        Entity entity,
        EquipmentSlot slot,
        ArmorMaterial.Layer layer,
        boolean innerModel
    ) {
        return texture;
    }

    @Override
    public void appendHoverText(
        ItemStack stack,
        TooltipContext context,
        List<Component> tooltip,
        TooltipFlag flag
    ) {
        super.appendHoverText(
            stack,
            context,
            tooltip,
            flag
        );

        ResourceLocation filterId = getInstalledFilter(stack);
        if (filterId == null)
            return;

        RespiratorFilterManager
            .getFilter(filterId)
            .ifPresent(filter -> {
                Item filterItem = BuiltInRegistries.ITEM.get(filter.item());
                Component filterName = new ItemStack(filterItem).getHoverName();

                tooltip.add(Component
                    .translatable(
                        "tooltip.create_cave_diving.installed_filter",
                        filterName,
                        filter != null ? filter.tier() : "?"
                    )
                    .withStyle(ChatFormatting.GRAY)
                );
            });
    }
}

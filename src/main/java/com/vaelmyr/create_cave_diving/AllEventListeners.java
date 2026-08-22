package com.vaelmyr.create_cave_diving;

import com.vaelmyr.create_cave_diving.content.equipment.armor.BaseRespiratorItem;
import com.vaelmyr.create_cave_diving.content.filter.RespiratorFilter;
import com.vaelmyr.create_cave_diving.content.filter.RespiratorFilterManager;
import com.vaelmyr.create_cave_diving.content.filter.RespiratorFilterReloadListener;
import com.vaelmyr.create_cave_diving.hazard.HazardRuleReloadListener;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public final class AllEventListeners {
    public static void register() {
        NeoForge.EVENT_BUS.addListener(AllEventListeners::addReloadListeners);
        NeoForge.EVENT_BUS.addListener(AllEventListeners::installRespiratorFilter);
        NeoForge.EVENT_BUS.addListener(AllEventListeners::addItemTooltips);
    }

    private static void addReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new HazardRuleReloadListener());
        event.addListener(new RespiratorFilterReloadListener());
    }

    private static void installRespiratorFilter(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();

        if (!player.isShiftKeyDown())
            return;

        ItemStack filterStack = player.getItemInHand(event.getHand());

        RespiratorFilter filter = RespiratorFilterManager
            .getFilter(filterStack)
            .orElse(null);

        if (filter == null)
            return;

        ItemStack respirator = BaseRespiratorItem.getWornItem(player);

        if (respirator.isEmpty())
            return;

        if (player.level().isClientSide())
            return;

        ResourceLocation previousFilter = BaseRespiratorItem.getInstalledFilter(respirator);

        if (filter.item().equals(previousFilter)) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            return;
        }

        if (previousFilter != null)
            giveFilterBack(player, previousFilter);

        BaseRespiratorItem.setInstalledFilter(respirator, filter.item());

        if (!player.getAbilities().instabuild)
            filterStack.shrink(1);

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    public static void giveFilterBack(Player player, ResourceLocation filterId) {
        if (!BuiltInRegistries.ITEM.containsKey(filterId))
            return;

        ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.get(filterId));

        if (!player.getInventory().add(stack))
            player.drop(stack, false);
    }

    private static void addItemTooltips(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        RespiratorFilterManager.getFilter(stack)
            .ifPresent(filter ->
                event
                .getToolTip()
                .add(Component
                    .translatable("tooltip.create_cave_diving.filter_tier", filter.tier())
                    .withStyle(ChatFormatting.GRAY)
                )
            );
}

    private AllEventListeners() {
    }
}

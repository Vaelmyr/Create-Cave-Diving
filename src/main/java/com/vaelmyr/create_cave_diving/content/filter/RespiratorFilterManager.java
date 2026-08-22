package com.vaelmyr.create_cave_diving.content.filter;

import java.util.Map;
import java.util.Optional;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class RespiratorFilterManager {
    private static Map<ResourceLocation, RespiratorFilter> filters = Map.of();

    public static void setFilters(Map<ResourceLocation, RespiratorFilter> newFilters) {
        filters = Map.copyOf(newFilters);
    }

    public static Map<ResourceLocation, RespiratorFilter> getFilters() {
        return filters;
    }

    public static Optional<RespiratorFilter> getFilter(ResourceLocation itemId) {
        if (itemId == null)
            return Optional.empty();

        return Optional.ofNullable(filters.get(itemId));
    }

    public static Optional<RespiratorFilter> getFilter(ItemStack stack) {
        if (stack.isEmpty())
            return Optional.empty();

        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return getFilter(id);
    }

    public static int getTier(ResourceLocation itemId) {
        return getFilter(itemId)
            .map(RespiratorFilter::tier)
            .orElse(0);
    }

    private RespiratorFilterManager() {
    }
}

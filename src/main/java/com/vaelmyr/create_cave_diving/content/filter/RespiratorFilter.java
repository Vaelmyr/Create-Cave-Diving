package com.vaelmyr.create_cave_diving.content.filter;

import java.util.Set;

import net.minecraft.resources.ResourceLocation;

public record RespiratorFilter(
    ResourceLocation id,
    ResourceLocation item,
    int tier
) {
    public static final Set<String> FIELDS = Set.of(
        "item",
        "tier"
    );
}

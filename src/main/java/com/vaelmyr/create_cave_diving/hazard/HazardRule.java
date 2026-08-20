package com.vaelmyr.create_cave_diving.hazard;

import java.util.Set;

import net.minecraft.resources.ResourceLocation;

public record HazardRule(ResourceLocation id, int hazardLevel, int priority, boolean override,
        HazardConditions conditions) {
    public static final Set<String> FIELDS = Set.of("hazard_level", "priority", "override", "conditions");
}

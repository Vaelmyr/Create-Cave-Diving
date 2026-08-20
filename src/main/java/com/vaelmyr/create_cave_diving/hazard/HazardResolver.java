package com.vaelmyr.create_cave_diving.hazard;

import net.minecraft.world.entity.LivingEntity;

public final class HazardResolver {

    public static int resolve(LivingEntity entity) {
        HazardRule finalRule = null;

        for (HazardRule rule : HazardRuleManager.getRules()) {
            if (!rule.conditions().matches(entity))
                continue;

            if (isHigherPriority(rule, finalRule))
                finalRule = rule;
        }

        return finalRule != null ? finalRule.hazardLevel() : 0;
    }

    private static boolean isHigherPriority(HazardRule candidate, HazardRule current) {
        if (current == null)
            return true;

        if (candidate.override() != current.override())
            return candidate.override();

        if (candidate.priority() != current.priority())
            return candidate.priority() > current.priority();

        return candidate.hazardLevel() > current.hazardLevel();
    }

    private HazardResolver() {
    }
}

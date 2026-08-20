package com.vaelmyr.create_cave_diving.hazard;

import java.util.List;

public final class HazardRuleManager {
    private static List<HazardRule> rules = List.of();

    public static List<HazardRule> getRules() {
        return rules;
    }

    public static void setRules(List<HazardRule> newRules) {
        rules = List.copyOf(newRules);
    }

    private HazardRuleManager() {
    }
}

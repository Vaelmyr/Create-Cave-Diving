package com.vaelmyr.create_cave_diving.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ServerConfig {
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.BooleanValue ENABLED;

    public static final ModConfigSpec.IntValue BASE_AIR_INTERVAL;
    public static final ModConfigSpec.IntValue INTERVAL_REDUCTION_PER_HAZARD;
    public static final ModConfigSpec.IntValue MINIMUM_AIR_INTERVAL;

    public static final ModConfigSpec.DoubleValue MAX_FILTER_EFFICIENCY_BONUS;
    public static final ModConfigSpec.DoubleValue FILTER_EFFICIENCY_FALLOFF;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        ENABLED = builder.comment("Whether Create: Cave Diving's breathing hazard system is enabled.",
                "This disables only the runtime effects, not the datapack definitions that will still be loaded.")
                .define("enabled", true);

        builder.push("airConsumption");

        BASE_AIR_INTERVAL = builder
                .comment("Ticks between air consumption at hazard level 1. Higher values mean slower air consumption.")
                .defineInRange("baseAirInterval", 40, 1, Integer.MAX_VALUE);

        INTERVAL_REDUCTION_PER_HAZARD = builder
                .comment("How many ticks are removed from the air consumption interval for every hazard level above 1.")
                .defineInRange("intervalReductionPerHazard", 5, 0, Integer.MAX_VALUE);

        MINIMUM_AIR_INTERVAL = builder.comment(
                "Minimum number of ticks between air consumption. Prevents very high hazard levels from consuming air every tick.")
                .defineInRange("minimumAirInterval", 5, 1, Integer.MAX_VALUE);

        builder.pop();
        builder.push("filterEfficiency");

        MAX_FILTER_EFFICIENCY_BONUS = builder
                .comment("Maximum efficiency bonus for using a filter above the required hazard tier.",
                        "0.50 means a maximum theoretical bonus of +50%.")
                .defineInRange("maximumEfficiencyBonus", 0.50D, 0.0D, Double.POSITIVE_INFINITY);

        FILTER_EFFICIENCY_FALLOFF = builder
                .comment("Controls how quickly diminishing returns apply.",
                        "Higher values make extra filter tiers less effective.",
                        "1.0 gives +25% at one tier above hazard when max bonus is 50%.")
                .defineInRange("efficiencyFalloff", 1.0D, 0.01D, Double.POSITIVE_INFINITY);

        builder.pop();

        SPEC = builder.build();
    }
}

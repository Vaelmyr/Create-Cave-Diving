package com.vaelmyr.create_cave_diving;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;

public final class AirTags {
    public static final TagKey<DimensionType> BREATHABLE_DIMENSIONS = TagKey.create(Registries.DIMENSION_TYPE,
            CreateCaveDiving.asResource("breathable_dimensions"));

    public static final TagKey<Biome> BREATHABLE_BIOMES = TagKey.create(Registries.BIOME,
            CreateCaveDiving.asResource("breathable_biomes"));

    private AirTags() {
    }
}

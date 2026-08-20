package com.vaelmyr.create_cave_diving;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.biome.Biome;

public final class HazardResolver {
    public static int resolve(LivingEntity entity) {
        Holder<DimensionType> dimension = entity.level().dimensionTypeRegistration();
        if (dimension.is(AirTags.BREATHABLE_DIMENSIONS))
            return 0;

        Holder<Biome> biome = entity.level().getBiome(entity.blockPosition());
        if (biome.is(AirTags.BREATHABLE_BIOMES))
            return 0;

        double y = entity.getY();
        if (y <= 10)
            return 3;
        if (y <= 30)
            return 2;
        if (y <= 50)
            return 1;

        return 0;
    }
}

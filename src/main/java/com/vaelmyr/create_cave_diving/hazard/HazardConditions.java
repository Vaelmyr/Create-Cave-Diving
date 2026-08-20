package com.vaelmyr.create_cave_diving.hazard;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;

public record HazardConditions(Optional<String> dimension, Optional<String> biome, OptionalInt minY, OptionalInt maxY) {
    public static final Set<String> FIELDS = Set.of("dimension", "biome", "min_y", "max_y");

    public boolean matches(LivingEntity entity) {
        if (!matchesY(entity))
            return false;

        if (!matchesDimension(entity))
            return false;

        if (!matchesBiome(entity))
            return false;

        return true;
    }

    private boolean matchesY(LivingEntity entity) {
        int y = entity.blockPosition().getY();

        if (minY.isPresent() && y < minY.getAsInt())
            return false;

        if (maxY.isPresent() && y > maxY.getAsInt())
            return false;

        return true;
    }

    private boolean matchesDimension(LivingEntity entity) {
        if (dimension.isEmpty())
            return true;

        String value = dimension.get();
        if (value.startsWith("#"))
            return matchesDimensionTag(entity, value.substring(1));

        ResourceLocation expected = ResourceLocation.tryParse(value);
        if (expected == null)
            return false;

        return entity.level().dimension().location().equals(expected);
    }

    private boolean matchesDimensionTag(LivingEntity entity, String value) {
        ResourceLocation tagId = ResourceLocation.tryParse(value);

        if (tagId == null)
            return false;

        if (!(entity.level() instanceof ServerLevel serverLevel))
            return false;

        Registry<LevelStem> levelStemRegistry = serverLevel.registryAccess().registryOrThrow(Registries.LEVEL_STEM);
        ResourceLocation dimensionId = serverLevel.dimension().location();
        ResourceKey<LevelStem> dimensionKey = ResourceKey.create(Registries.LEVEL_STEM, dimensionId);
        Optional<Holder.Reference<LevelStem>> holder = levelStemRegistry.getHolder(dimensionKey);

        if (holder.isEmpty())
            return false;

        TagKey<LevelStem> tag = TagKey.create(Registries.LEVEL_STEM, tagId);
        return holder.get().is(tag);
    }

    private boolean matchesBiome(LivingEntity entity) {
        if (biome.isEmpty())
            return true;

        String value = biome.get();
        Holder<Biome> currentBiome = entity.level().getBiome(entity.blockPosition());

        if (value.startsWith("#")) {
            ResourceLocation id = ResourceLocation.tryParse(value.substring(1));
            if (id == null)
                return false;

            TagKey<Biome> tag = TagKey.create(Registries.BIOME, id);
            return currentBiome.is(tag);
        }

        ResourceLocation expected = ResourceLocation.tryParse(value);
        if (expected == null)
            return false;

        return currentBiome.unwrapKey().map(key -> key.location().equals(expected)).orElse(false);
    }
}

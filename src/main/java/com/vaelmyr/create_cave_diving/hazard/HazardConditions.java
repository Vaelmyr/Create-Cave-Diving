package com.vaelmyr.create_cave_diving.hazard;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;

public record HazardConditions(Optional<RegistrySelector<LevelStem>> dimension, Optional<RegistrySelector<Biome>> biome,
        OptionalInt minY, OptionalInt maxY) {
    public static final Set<String> FIELDS = Set.of("dimension", "biome", "min_y", "max_y");

    public boolean matches(LivingEntity entity) {
        return matchesY(entity) && matchesDimension(entity) && matchesBiome(entity);
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

        RegistrySelector<LevelStem> selector = dimension.get();
        if (!selector.isTag())
            return entity.level().dimension().location().equals(selector.id());

        if (!(entity.level() instanceof ServerLevel serverLevel))
            return false;

        Registry<LevelStem> registry = serverLevel.registryAccess().registryOrThrow(Registries.LEVEL_STEM);
        ResourceKey<LevelStem> dimensionKey = ResourceKey.create(Registries.LEVEL_STEM,
                serverLevel.dimension().location());

        Optional<Holder.Reference<LevelStem>> holder = registry.getHolder(dimensionKey);

        return holder.isPresent() && holder.get().is(selector.tag().orElseThrow());
    }

    private boolean matchesBiome(LivingEntity entity) {
        if (biome.isEmpty())
            return true;

        RegistrySelector<Biome> selector = biome.get();
        Holder<Biome> currentBiome = entity.level().getBiome(entity.blockPosition());

        if (selector.isTag())
            return currentBiome.is(selector.tag().orElseThrow());

        return currentBiome.unwrapKey().map(key -> key.location().equals(selector.id())).orElse(false);
    }
}

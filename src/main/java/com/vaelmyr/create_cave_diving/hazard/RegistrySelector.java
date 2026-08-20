package com.vaelmyr.create_cave_diving.hazard;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.Optional;

public record RegistrySelector<T>(ResourceLocation id, Optional<TagKey<T>> tag) {
    public static <T> RegistrySelector<T> id(ResourceLocation id) {
        return new RegistrySelector<>(id, Optional.empty());
    }

    public static <T> RegistrySelector<T> tag(TagKey<T> tag) {
        return new RegistrySelector<>(tag.location(), Optional.of(tag));
    }

    public boolean isTag() {
        return tag.isPresent();
    }
}

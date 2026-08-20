package com.vaelmyr.create_cave_diving.hazard;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vaelmyr.create_cave_diving.CreateCaveDiving;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;

public final class HazardRuleReloadListener extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public HazardRuleReloadListener() {
        super(GSON, "hazards");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> entries, ResourceManager resourceManager,
            ProfilerFiller profiler) {
        List<HazardRule> rules = new ArrayList<>();

        for (Map.Entry<ResourceLocation, JsonElement> entry : entries.entrySet()) {
            ResourceLocation id = entry.getKey();

            try {
                HazardRule rule = parseRule(id, entry.getValue());
                rules.add(rule);
            } catch (Exception e) {
                CreateCaveDiving.LOGGER.error("Failed to load hazard rule {}", id, e);
            }
        }

        HazardRuleManager.setRules(rules);
        CreateCaveDiving.LOGGER.info("Loaded {} hazard rules", rules.size());
    }

    private static HazardRule parseRule(ResourceLocation id, JsonElement element) {
        if (!element.isJsonObject())
            throw new IllegalArgumentException("Hazard rule must be a JSON object");

        JsonObject json = element.getAsJsonObject();
        validateJsonFields(json, HazardRule.FIELDS, "hazard rule '" + id + "'");

        if (!json.has("hazard_level"))
            throw new IllegalArgumentException("Missing required field 'hazard_level'");

        int hazardLevel = json.get("hazard_level").getAsInt();
        if (hazardLevel < 0)
            throw new IllegalArgumentException("'hazard_level' cannot be negative");

        int priority = json.has("priority") ? json.get("priority").getAsInt() : 0;
        boolean override = json.has("override") && json.get("override").getAsBoolean();

        HazardConditions conditions = emptyConditions();
        if (json.has("conditions")) {
            JsonElement conditionsElement = json.get("conditions");
            if (!conditionsElement.isJsonObject())
                throw new IllegalArgumentException("'conditions' must be a JSON object");

            conditions = parseConditions(id, conditionsElement.getAsJsonObject());
        }

        return new HazardRule(id, hazardLevel, priority, override, conditions);
    }

    private static HazardConditions parseConditions(ResourceLocation id, JsonObject json) {
        validateJsonFields(json, HazardConditions.FIELDS, "conditions for hazard rule '" + id + "'");

        Optional<String> dimension = getOptionalString(json, "dimension");
        Optional<String> biome = getOptionalString(json, "biome");
        OptionalInt minY = getOptionalInt(json, "min_y");
        OptionalInt maxY = getOptionalInt(json, "max_y");

        validateLocation("dimension", dimension);
        validateLocation("biome", biome);
        validateY(minY, maxY);

        return new HazardConditions(dimension, biome, minY, maxY);
    }

    private static void validateLocation(String field, Optional<String> value) {
        if (value.isEmpty())
            return;

        String raw = value.get();
        if (raw.startsWith("#"))
            raw = raw.substring(1);

        if (raw.isEmpty())
            throw new IllegalArgumentException("'" + field + "' tag cannot be empty");

        if (ResourceLocation.tryParse(raw) == null)
            throw new IllegalArgumentException("Invalid '" + field + "' ResourceLocation: " + value.get());
    }

    private static void validateY(OptionalInt minY, OptionalInt maxY) {
        if (minY.isPresent() && maxY.isPresent() && minY.getAsInt() > maxY.getAsInt())
            throw new IllegalArgumentException("'min_y' cannot be greater than 'max_y'");
    }

    private static void validateJsonFields(JsonObject json, Set<String> allowedField, String context) {
        for (String key : json.keySet())
            if (!allowedField.contains(key))
                throw new IllegalArgumentException("Unknown field '" + key + "' in " + context);
    }

    private static HazardConditions emptyConditions() {
        return new HazardConditions(Optional.empty(), Optional.empty(), OptionalInt.empty(), OptionalInt.empty());
    }

    private static Optional<String> getOptionalString(JsonObject json, String key) {
        if (!json.has(key))
            return Optional.empty();

        return Optional.of(json.get(key).getAsString());
    }

    private static OptionalInt getOptionalInt(JsonObject json, String key) {
        if (!json.has(key))
            return OptionalInt.empty();

        return OptionalInt.of(json.get(key).getAsInt());
    }
}

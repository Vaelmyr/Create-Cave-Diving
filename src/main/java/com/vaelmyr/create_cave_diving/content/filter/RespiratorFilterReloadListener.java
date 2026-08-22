package com.vaelmyr.create_cave_diving.content.filter;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vaelmyr.create_cave_diving.CreateCaveDiving;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

public final class RespiratorFilterReloadListener extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public RespiratorFilterReloadListener() {
        super(GSON, "respirator_filters");
    }

    @Override
    protected void apply(
        Map<ResourceLocation, JsonElement> entries,
        ResourceManager resourceManager,
        ProfilerFiller profiler
    ) {
        Map<ResourceLocation, RespiratorFilter> filters = new HashMap<>();

        for (Map.Entry<ResourceLocation, JsonElement> entry : entries.entrySet()) {
            ResourceLocation id = entry.getKey();

            try {
                RespiratorFilter filter = parseFilter(id, entry.getValue());

                if (filters.containsKey(filter.item()))
                    throw new IllegalArgumentException("Duplicate respirator filter item: " + filter.item());

                filters.put(filter.item(), filter);
            } catch (Exception e) {
                CreateCaveDiving.LOGGER.error("Failed to load respirator filter {}", id, e);
            }
        }

        RespiratorFilterManager.setFilters(filters);
        CreateCaveDiving.LOGGER.info("Loaded {} respirator filters", filters.size());
    }

    private static RespiratorFilter parseFilter(ResourceLocation id, JsonElement element) {
        if (!element.isJsonObject())
            throw new IllegalArgumentException("Respirator filter must be a JSON object");

        JsonObject json = element.getAsJsonObject();

        for (String key : json.keySet())
            if (!RespiratorFilter.FIELDS.contains(key))
                throw new IllegalArgumentException("Unknown field '" + key + "' in respirator filter '" + id + "'");

        for (String field : RespiratorFilter.FIELDS)
            if (!json.has(field))
                throw new IllegalArgumentException("Missing required field '" + field + "' in respirator filter '" + id + "'");

        String rawItemId = json.get("item").getAsString();
        ResourceLocation itemId = ResourceLocation.tryParse(rawItemId);

        if (itemId == null)
            throw new IllegalArgumentException("Invalid item ID '" + rawItemId + "' in respirator filter '" + id + "'");

        if (!BuiltInRegistries.ITEM.containsKey(itemId))
            throw new IllegalArgumentException("Unknown item '" + itemId + "' in respirator filter '" + id + "'");

        int tier = json.get("tier").getAsInt();
        if (tier < 1)
            throw new IllegalArgumentException("Tier must be at least 1 in respirator filter '" + id + "'");

        return new RespiratorFilter(id, itemId, tier);
    }
}

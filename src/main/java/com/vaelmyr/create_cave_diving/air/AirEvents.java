package com.vaelmyr.create_cave_diving.air;

import java.util.List;

import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.vaelmyr.create_cave_diving.CreateCaveDiving;
import com.vaelmyr.create_cave_diving.config.ServerConfig;
import com.vaelmyr.create_cave_diving.content.equipment.armor.BaseRespiratorItem;
import com.vaelmyr.create_cave_diving.content.filter.RespiratorFilterManager;
import com.vaelmyr.create_cave_diving.hazard.HazardResolver;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingBreatheEvent;

@EventBusSubscriber(modid = CreateCaveDiving.MODID)
public final class AirEvents {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void breatheUnderground(LivingBreatheEvent event) {
        if (!ServerConfig.ENABLED.get())
            return;

        LivingEntity entity = event.getEntity();
        if (!(entity instanceof ServerPlayer player))
            return;

        if (player.isCreative() || player.isSpectator())
            return;

        int hazardLevel = HazardResolver.resolve(entity);
        if (hazardLevel <= 0)
            return;

        ItemStack respirator = BaseRespiratorItem.getWornItem(player);
        if (respirator.isEmpty()) {
            event.setCanBreathe(false);
            return;
        }

        ResourceLocation filterId = BaseRespiratorItem.getInstalledFilter(respirator);
        int filterTier = RespiratorFilterManager.getTier(filterId);
        if (filterTier < hazardLevel) {
            event.setCanBreathe(false);
            return;
        }

        List<ItemStack> backtanks = BacktankUtil.getAllWithAir(player);
        if (backtanks.isEmpty()) {
            event.setCanBreathe(false);
            return;
        }

        int baseInterval = getBaseConsumptionInterval(hazardLevel);
        int interval = applyFilterEfficiency(baseInterval, filterTier, hazardLevel);

        if (player.level().getGameTime() % interval == 0) {
            BacktankUtil.consumeAir(player, backtanks.get(0), 1);
        }
    }

    private static int getBaseConsumptionInterval(int hazardLevel) {
        if (hazardLevel <= 0)
            return Integer.MAX_VALUE;

        long baseInterval = ServerConfig.BASE_AIR_INTERVAL.get();
        long reduction = ServerConfig.INTERVAL_REDUCTION_PER_HAZARD.get();
        long minimum = ServerConfig.MINIMUM_AIR_INTERVAL.get();
        long interval = baseInterval - ((long) (hazardLevel - 1) * reduction);

        return (int) Math.max(minimum, interval);
    }

    private static int applyFilterEfficiency(int baseInterval, int respiratorTier, int hazardLevel) {
        int tierDifference = Math.max(0, respiratorTier - hazardLevel);

        if (tierDifference == 0)
            return baseInterval;

        double maxBonus = ServerConfig.MAX_FILTER_EFFICIENCY_BONUS.get();
        double falloff = ServerConfig.FILTER_EFFICIENCY_FALLOFF.get();

        double efficiencyBonus = maxBonus * tierDifference / (tierDifference + falloff);
        double multiplier = 1.0D + efficiencyBonus;

        return (int) Math.min(Math.max(1L, Math.round(baseInterval * multiplier)), Integer.MAX_VALUE);
    }

    private AirEvents() {
    }
}

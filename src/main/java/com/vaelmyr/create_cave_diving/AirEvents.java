package com.vaelmyr.create_cave_diving;

import java.util.List;

import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.vaelmyr.create_cave_diving.content.equipment.armor.BaseRespiratorItem;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingBreatheEvent;

@EventBusSubscriber(modid = CreateCaveDiving.MODID)
public class AirEvents {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void breatheUnderground(LivingBreatheEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof ServerPlayer player))
            return;

        int hazardTier = HazardResolver.resolve(entity);
        if (hazardTier <= 0)
            return;

        int respiratorTier = 0;
        ItemStack respirator = BaseRespiratorItem.getWornItem(player);
        List<ItemStack> backtanks = BacktankUtil.getAllWithAir(player);

        if (respirator.getItem() instanceof BaseRespiratorItem respiratorItem)
            respiratorTier = respiratorItem.getTier();

        boolean canBreatheUnderground = respiratorTier >= hazardTier && !backtanks.isEmpty();

        if (!canBreatheUnderground) {
            event.setCanBreathe(false);
            return;
        }

        int interval = applyRespiratorEfficiency(respiratorTier, hazardTier);

        Level level = entity.level();
        if (level.getGameTime() % interval == 0) {
            BacktankUtil.consumeAir(player, backtanks.get(0), 1);
        }

        event.setCanBreathe(true);
    }

    private static int getBaseConsumptionInterval(int hazardTier) {
        return switch (hazardTier) {
        case 1 -> 40;
        case 2 -> 20;
        case 3 -> 10;
        default -> 40;
        };
    }

    private static int applyRespiratorEfficiency(int respiratorTier, int hazardTier) {
        int baseInterval = getBaseConsumptionInterval(hazardTier);
        int tierDifference = respiratorTier - hazardTier;

        double multiplier = switch (tierDifference) {
        case 0 -> 1.0;
        case 1 -> 1.25;
        case 2 -> 1.5;
        default -> 1.5;
        };

        return (int) Math.round(baseInterval * multiplier);
    }

    private AirEvents() {
    }
}

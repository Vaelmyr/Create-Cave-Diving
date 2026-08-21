package com.vaelmyr.create_cave_diving;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import com.tterrag.registrate.Registrate;
import com.vaelmyr.create_cave_diving.config.ServerConfig;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(CreateCaveDiving.MODID)
public class CreateCaveDiving {
    public static final String MODID = "create_cave_diving";
    public static final String NAME = "Create Cave Diving";

    public static final Logger LOGGER = LogUtils.getLogger();

    private static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    private static final Registrate REGISTRATE = Registrate.create(MODID)
        .defaultCreativeTab((ResourceKey<CreativeModeTab>) null);

    public CreateCaveDiving(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("{} initializing!", NAME);

        modContainer.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);

        AllItems.register();
        AllCreativeModeTabs.register(modEventBus);
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public static Registrate registrate() {
        if (!STACK_WALKER.getCallerClass().getPackageName().startsWith("com.vaelmyr.create_cave_diving"))
            throw new UnsupportedOperationException(
                    "Other mods are not permitted to use Create: Cave Diving registrate instance.");

        return REGISTRATE;
    }
}

package ru.ke4e.armorweight;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import ru.ke4e.armorweight.config.ArmorWeightConfigManager;

@Mod(ArmorWeightMod.MOD_ID)
public final class ArmorWeightMod {
    public static final String MOD_ID = "armorweight";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ArmorWeightMod(FMLJavaModLoadingContext context) {
        FMLCommonSetupEvent.getBus(context.getModBusGroup()).addListener(this::onCommonSetup);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(ArmorWeightConfigManager::load);
    }
}

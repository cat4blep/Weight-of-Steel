package ru.ke4e.armorweight;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import ru.ke4e.armorweight.config.ArmorWeightConfigManager;

@Mod(ArmorWeightMod.MOD_ID)
public final class ArmorWeightMod {
    public static final String MOD_ID = "armorweight";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ArmorWeightMod(IEventBus modEventBus) {
        modEventBus.addListener(this::onCommonSetup);
        NeoForge.EVENT_BUS.addListener(ArmorWeightEvents::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(ArmorWeightEvents::onTooltip);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(ArmorWeightConfigManager::load);
    }
}

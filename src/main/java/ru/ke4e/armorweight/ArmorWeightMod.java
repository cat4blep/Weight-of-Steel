package ru.ke4e.armorweight;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ke4e.armorweight.config.ArmorWeightConfigManager;

public final class ArmorWeightMod implements ModInitializer {
	public static final String MOD_ID = "armorweight";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ArmorWeightConfigManager.load();
		LOGGER.info("Armor Weight initialized");
	}
}

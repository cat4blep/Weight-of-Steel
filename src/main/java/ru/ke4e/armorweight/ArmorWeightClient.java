package ru.ke4e.armorweight;

import java.util.Locale;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import ru.ke4e.armorweight.config.ArmorWeightConfigManager;

public final class ArmorWeightClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player != null) {
				ArmorWeightPlayerHandler.applyWeight(client.player);
			}
		});

		ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
			if (!ArmorWeightConfigManager.getConfig().showTooltip || !ArmorWeightCalculator.isArmor(stack)) {
				return;
			}

			float weight = ArmorWeightCalculator.getArmorItemWeight(stack);
			lines.add(
				Text.translatable("tooltip.armorweight.weight", String.format(Locale.ROOT, "%.2f", weight))
					.formatted(Formatting.GRAY)
			);
		});
	}
}

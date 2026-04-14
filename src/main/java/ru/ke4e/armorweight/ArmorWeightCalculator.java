package ru.ke4e.armorweight;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import ru.ke4e.armorweight.config.ArmorWeightConfig;
import ru.ke4e.armorweight.config.ArmorWeightConfigManager;

public final class ArmorWeightCalculator {
	private ArmorWeightCalculator() {
	}

	public static float getSpeedMultiplier(PlayerEntity player) {
		if (player.isSpectator() || player.isCreative()) {
			return 1.0F;
		}

		float totalWeight = 0.0F;
		totalWeight += getArmorItemWeight(player.getEquippedStack(EquipmentSlot.HEAD));
		totalWeight += getArmorItemWeight(player.getEquippedStack(EquipmentSlot.CHEST));
		totalWeight += getArmorItemWeight(player.getEquippedStack(EquipmentSlot.LEGS));
		totalWeight += getArmorItemWeight(player.getEquippedStack(EquipmentSlot.FEET));

		ArmorWeightConfig config = ArmorWeightConfigManager.getConfig();
		float slowdown = MathHelper.clamp(totalWeight * config.weightToSlowdown, 0.0F, config.maxSlowdown);
		return 1.0F - slowdown;
	}

	public static boolean isArmor(ItemStack stack) {
		return stack.getItem() instanceof ArmorItem;
	}

	public static float getArmorItemWeight(ItemStack stack) {
		if (!isArmor(stack)) {
			return 0.0F;
		}

		float defaultWeight = getDefaultArmorItemWeight(stack);
		return ArmorWeightConfigManager.getConfiguredWeight(stack, defaultWeight);
	}

	public static float getDefaultArmorItemWeight(ItemStack stack) {
		if (!(stack.getItem() instanceof ArmorItem armorItem)) {
			return 0.0F;
		}

		ArmorWeightConfig config = ArmorWeightConfigManager.getConfig();
		float armorWeight = armorItem.getProtection();
		float toughnessWeight = armorItem.getMaterial().value().toughness() * config.toughnessWeight;
		float knockbackWeight = armorItem.getMaterial().value().knockbackResistance() * config.knockbackWeight;
		return armorWeight + toughnessWeight + knockbackWeight;
	}
}

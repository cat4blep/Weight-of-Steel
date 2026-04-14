package ru.ke4e.armorweight;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
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

		for (EquipmentSlot slot : EquipmentSlot.VALUES) {
			if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) {
				continue;
			}

			totalWeight += getArmorItemWeight(player.getEquippedStack(slot), slot);
		}

		ArmorWeightConfig config = ArmorWeightConfigManager.getConfig();
		float slowdown = MathHelper.clamp(totalWeight * config.weightToSlowdown, 0.0F, config.maxSlowdown);
		return 1.0F - slowdown;
	}

	public static boolean isArmor(ItemStack stack) {
		return getArmorSlot(stack) != null;
	}

	public static float getArmorItemWeight(ItemStack stack) {
		EquipmentSlot slot = getArmorSlot(stack);
		return slot == null ? 0.0F : getArmorItemWeight(stack, slot);
	}

	public static float getDefaultArmorItemWeight(ItemStack stack) {
		EquipmentSlot slot = getArmorSlot(stack);
		return slot == null ? 0.0F : getDefaultArmorItemWeight(stack, slot);
	}

	private static float getArmorItemWeight(ItemStack stack, EquipmentSlot slot) {
		float defaultWeight = getDefaultArmorItemWeight(stack, slot);
		return ArmorWeightConfigManager.getConfiguredWeight(stack, defaultWeight);
	}

	private static float getDefaultArmorItemWeight(ItemStack stack, EquipmentSlot slot) {
		if (stack.isEmpty() || slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) {
			return 0.0F;
		}

		ArmorWeightConfig config = ArmorWeightConfigManager.getConfig();
		float armorWeight = getAttributeValue(stack, slot, EntityAttributes.ARMOR);
		float toughnessWeight = getAttributeValue(stack, slot, EntityAttributes.ARMOR_TOUGHNESS) * config.toughnessWeight;
		float knockbackWeight = getAttributeValue(stack, slot, EntityAttributes.KNOCKBACK_RESISTANCE) * config.knockbackWeight;
		return armorWeight + toughnessWeight + knockbackWeight;
	}

	private static EquipmentSlot getArmorSlot(ItemStack stack) {
		if (stack.isEmpty()) {
			return null;
		}

		EquippableComponent equippableComponent = stack.get(DataComponentTypes.EQUIPPABLE);
		if (equippableComponent == null || equippableComponent.slot().getType() != EquipmentSlot.Type.HUMANOID_ARMOR) {
			return null;
		}

		return equippableComponent.slot();
	}

	private static float getAttributeValue(ItemStack stack, EquipmentSlot slot, RegistryEntry<EntityAttribute> attribute) {
		double[] total = new double[] {0.0};
		stack.applyAttributeModifiers(slot, (currentAttribute, modifier) -> total[0] += getModifierValue(currentAttribute, modifier, attribute));
		return (float) total[0];
	}

	private static double getModifierValue(
		RegistryEntry<EntityAttribute> currentAttribute, EntityAttributeModifier modifier, RegistryEntry<EntityAttribute> wantedAttribute
	) {
		if (!currentAttribute.getKeyOrValue().equals(wantedAttribute.getKeyOrValue())) {
			return 0.0;
		}

		return switch (modifier.operation()) {
			case ADD_VALUE -> modifier.value();
			case ADD_MULTIPLIED_BASE, ADD_MULTIPLIED_TOTAL -> 0.0;
		};
	}
}

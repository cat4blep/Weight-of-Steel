package ru.ke4e.armorweight;

import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public final class ArmorWeightPlayerHandler {
	private static final Identifier SPEED_MODIFIER_ID = Identifier.of(ArmorWeightMod.MOD_ID, "speed_penalty");

	private ArmorWeightPlayerHandler() {
	}

	public static void applyWeight(PlayerEntity player) {
		EntityAttributeInstance speedAttribute = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
		if (speedAttribute == null) {
			return;
		}

		float slowdown = 1.0F - ArmorWeightCalculator.getSpeedMultiplier(player);
		if (slowdown <= 0.0001F) {
			if (speedAttribute.getModifier(SPEED_MODIFIER_ID) != null) {
				speedAttribute.removeModifier(SPEED_MODIFIER_ID);
			}
			return;
		}

		if (speedAttribute.getModifier(SPEED_MODIFIER_ID) != null) {
			speedAttribute.removeModifier(SPEED_MODIFIER_ID);
		}

		speedAttribute.addTemporaryModifier(
			new EntityAttributeModifier(SPEED_MODIFIER_ID, -slowdown, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
		);
	}
}

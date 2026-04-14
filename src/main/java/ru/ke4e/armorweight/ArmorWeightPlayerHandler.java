package ru.ke4e.armorweight;

import java.util.UUID;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;

public final class ArmorWeightPlayerHandler {
	private static final UUID SPEED_MODIFIER_ID = UUID.fromString("fae0b9d3-4df6-49eb-9f4d-9c36c74d2a21");
	private static final String SPEED_MODIFIER_NAME = "armorweight.speed_penalty";

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
			new EntityAttributeModifier(SPEED_MODIFIER_ID, SPEED_MODIFIER_NAME, -slowdown, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
		);
	}
}

package ru.ke4e.armorweight;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public final class ArmorWeightPlayerHandler {
    private static final UUID SPEED_MODIFIER_ID = UUID.fromString("f9d19701-73f7-4f5a-b2c6-12040a05ac51");

    private ArmorWeightPlayerHandler() {
    }

    public static void applyWeight(Player player) {
        AttributeInstance speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
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

        speedAttribute.addTransientModifier(
            new AttributeModifier(SPEED_MODIFIER_ID, ArmorWeightMod.MOD_ID + ":speed_penalty", -slowdown, AttributeModifier.Operation.MULTIPLY_TOTAL)
        );
    }
}

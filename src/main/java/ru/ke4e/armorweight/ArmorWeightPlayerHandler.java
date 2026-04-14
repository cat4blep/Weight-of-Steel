package ru.ke4e.armorweight;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public final class ArmorWeightPlayerHandler {
    private static final ResourceLocation SPEED_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(ArmorWeightMod.MOD_ID, "speed_penalty");

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
            new AttributeModifier(SPEED_MODIFIER_ID, -slowdown, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
        );
    }
}

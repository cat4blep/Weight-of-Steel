package ru.ke4e.armorweight;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public final class ArmorWeightPlayerHandler {
    private static final Identifier SPEED_MODIFIER_ID = Identifier.fromNamespaceAndPath(ArmorWeightMod.MOD_ID, "speed_penalty");

    private ArmorWeightPlayerHandler() {
    }

    public static void applyWeight(Player player) {
        AttributeInstance speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute == null) {
            return;
        }

        float slowdown = 1.0F - ArmorWeightCalculator.getSpeedMultiplier(player);
        if (slowdown <= 0.0001F) {
            speedAttribute.removeModifier(SPEED_MODIFIER_ID);
            return;
        }

        speedAttribute.addOrUpdateTransientModifier(
            new AttributeModifier(SPEED_MODIFIER_ID, -slowdown, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
        );
    }
}

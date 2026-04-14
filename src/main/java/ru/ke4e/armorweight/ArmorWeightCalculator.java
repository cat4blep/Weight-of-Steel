package ru.ke4e.armorweight;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import ru.ke4e.armorweight.config.ArmorWeightConfig;
import ru.ke4e.armorweight.config.ArmorWeightConfigManager;

public final class ArmorWeightCalculator {
    private ArmorWeightCalculator() {
    }

    public static float getSpeedMultiplier(Player player) {
        if (player.isSpectator() || player.isCreative()) {
            return 1.0F;
        }

        float totalWeight = 0.0F;

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) {
                continue;
            }

            totalWeight += getArmorItemWeight(player.getItemBySlot(slot), slot);
        }

        ArmorWeightConfig config = ArmorWeightConfigManager.getConfig();
        float slowdown = Mth.clamp(totalWeight * config.weightToSlowdown, 0.0F, config.maxSlowdown);
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
        float armorWeight = getAttributeValue(stack, slot, Attributes.ARMOR);
        float toughnessWeight = getAttributeValue(stack, slot, Attributes.ARMOR_TOUGHNESS) * config.toughnessWeight;
        float knockbackWeight = getAttributeValue(stack, slot, Attributes.KNOCKBACK_RESISTANCE) * config.knockbackWeight;
        return armorWeight + toughnessWeight + knockbackWeight;
    }

    private static EquipmentSlot getArmorSlot(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }

        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        if (equippable == null || equippable.slot().getType() != EquipmentSlot.Type.HUMANOID_ARMOR) {
            return null;
        }

        return equippable.slot();
    }

    private static float getAttributeValue(ItemStack stack, EquipmentSlot slot, Holder<Attribute> attribute) {
        double[] total = new double[] {0.0};
        stack.forEachModifier(slot, (currentAttribute, modifier) -> total[0] += getModifierValue(currentAttribute, modifier, attribute));
        return (float) total[0];
    }

    private static double getModifierValue(Holder<Attribute> currentAttribute, AttributeModifier modifier, Holder<Attribute> wantedAttribute) {
        if (currentAttribute.value() != wantedAttribute.value()) {
            return 0.0;
        }

        return switch (modifier.operation()) {
            case ADD_VALUE -> modifier.amount();
            case ADD_MULTIPLIED_BASE, ADD_MULTIPLIED_TOTAL -> 0.0;
        };
    }
}

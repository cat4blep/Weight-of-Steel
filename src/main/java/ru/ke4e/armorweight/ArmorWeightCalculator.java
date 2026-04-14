package ru.ke4e.armorweight;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
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
        totalWeight += getArmorItemWeight(player.getItemBySlot(EquipmentSlot.HEAD));
        totalWeight += getArmorItemWeight(player.getItemBySlot(EquipmentSlot.CHEST));
        totalWeight += getArmorItemWeight(player.getItemBySlot(EquipmentSlot.LEGS));
        totalWeight += getArmorItemWeight(player.getItemBySlot(EquipmentSlot.FEET));

        ArmorWeightConfig config = ArmorWeightConfigManager.getConfig();
        float slowdown = Mth.clamp(totalWeight * config.weightToSlowdown, 0.0F, config.maxSlowdown);
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
        float armorWeight = armorItem.getDefense();
        float toughnessWeight = armorItem.getMaterial().getToughness() * config.toughnessWeight;
        float knockbackWeight = armorItem.getMaterial().getKnockbackResistance() * config.knockbackWeight;
        return armorWeight + toughnessWeight + knockbackWeight;
    }
}

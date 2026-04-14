package ru.ke4e.armorweight;

import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import ru.ke4e.armorweight.config.ArmorWeightConfigManager;

public final class ArmorWeightEvents {
    private ArmorWeightEvents() {
    }

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        ArmorWeightPlayerHandler.applyWeight(event.getEntity());
    }

    public static void onTooltip(ItemTooltipEvent event) {
        if (!ArmorWeightConfigManager.getConfig().showTooltip) {
            return;
        }

        if (!ArmorWeightCalculator.isArmor(event.getItemStack())) {
            return;
        }

        float weight = ArmorWeightCalculator.getArmorItemWeight(event.getItemStack());
        event.getToolTip().add(
            Component.translatable("tooltip.armorweight.weight", String.format(Locale.ROOT, "%.2f", weight))
                .withStyle(ChatFormatting.GRAY)
        );
    }
}

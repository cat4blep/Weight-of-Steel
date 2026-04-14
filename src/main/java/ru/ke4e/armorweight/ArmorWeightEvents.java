package ru.ke4e.armorweight;

import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.ke4e.armorweight.config.ArmorWeightConfigManager;

@Mod.EventBusSubscriber(modid = ArmorWeightMod.MOD_ID)
public final class ArmorWeightEvents {
    private ArmorWeightEvents() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent.Post event) {
        ArmorWeightPlayerHandler.applyWeight(event.player());
    }

    @SubscribeEvent
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


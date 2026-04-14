package ru.ke4e.armorweight.config;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ArmorWeightConfig {
    public static final float DEFAULT_WEIGHT_TO_SLOWDOWN = 0.013F;
    public static final float DEFAULT_MAX_SLOWDOWN = 0.35F;
    public static final float DEFAULT_TOUGHNESS_WEIGHT = 0.5F;
    public static final float DEFAULT_KNOCKBACK_WEIGHT = 8.0F;

    public boolean showTooltip = true;
    public float weightToSlowdown = DEFAULT_WEIGHT_TO_SLOWDOWN;
    public float maxSlowdown = DEFAULT_MAX_SLOWDOWN;
    public float toughnessWeight = DEFAULT_TOUGHNESS_WEIGHT;
    public float knockbackWeight = DEFAULT_KNOCKBACK_WEIGHT;
    public Map<String, ArmorItemOverride> itemOverrides = new LinkedHashMap<>();

    public static final class ArmorItemOverride {
        public boolean useCustomWeight = false;
        public float customWeight = 0.0F;

        public ArmorItemOverride() {
        }

        public ArmorItemOverride(boolean useCustomWeight, float customWeight) {
            this.useCustomWeight = useCustomWeight;
            this.customWeight = customWeight;
        }
    }
}

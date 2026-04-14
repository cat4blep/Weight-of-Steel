package ru.ke4e.armorweight.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.loading.FMLPaths;
import ru.ke4e.armorweight.ArmorWeightCalculator;
import ru.ke4e.armorweight.ArmorWeightMod;

public final class ArmorWeightConfigManager {
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve("armorweight.json");
    private static final Object SYNC_LOCK = new Object();

    private static ArmorWeightConfig config = new ArmorWeightConfig();
    private static boolean armorItemsSynced;
    private static boolean syncDelayLogged;

    private ArmorWeightConfigManager() {
    }

    public static void load() {
        boolean created = !Files.exists(CONFIG_PATH);
        config = loadFromDisk();

        boolean changed = sanitizeConfig();
        armorItemsSynced = false;
        syncDelayLogged = false;

        if (created || changed) {
            save();
        }

        ArmorWeightMod.LOGGER.info("Armor Weight initialized");
    }

    public static ArmorWeightConfig getConfig() {
        return config;
    }

    public static float getConfiguredWeight(ItemStack stack, float fallbackWeight) {
        if (stack.isEmpty()) {
            return 0.0F;
        }

        ensureArmorItemsSynced();

        Identifier itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (itemId == null) {
            return sanitizeWeight(fallbackWeight, 0.0F);
        }

        ArmorWeightConfig.ArmorItemOverride override = config.itemOverrides.get(itemId.toString());
        if (override != null && override.useCustomWeight) {
            return sanitizeWeight(override.customWeight, 0.0F);
        }

        return sanitizeWeight(fallbackWeight, 0.0F);
    }

    public static void ensureArmorItemsSynced() {
        if (armorItemsSynced) {
            return;
        }

        synchronized (SYNC_LOCK) {
            if (armorItemsSynced) {
                return;
            }

            SyncResult result = syncArmorItems();
            if (!result.completed()) {
                if (!syncDelayLogged) {
                    ArmorWeightMod.LOGGER.info("Delaying armor config item sync until default item components are available");
                    syncDelayLogged = true;
                }

                return;
            }

            armorItemsSynced = true;
            syncDelayLogged = false;

            if (result.changed()) {
                save();
            }
        }
    }

    private static ArmorWeightConfig loadFromDisk() {
        if (!Files.exists(CONFIG_PATH)) {
            return new ArmorWeightConfig();
        }

        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            ArmorWeightConfig loadedConfig = GSON.fromJson(reader, ArmorWeightConfig.class);
            return loadedConfig == null ? new ArmorWeightConfig() : loadedConfig;
        } catch (IOException | JsonParseException exception) {
            backupBrokenConfig();
            ArmorWeightMod.LOGGER.error("Failed to read armor weight config, using defaults", exception);
            return new ArmorWeightConfig();
        }
    }

    private static void backupBrokenConfig() {
        try {
            Files.copy(CONFIG_PATH, CONFIG_PATH.resolveSibling("armorweight.json.broken"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            ArmorWeightMod.LOGGER.warn("Failed to back up broken armor weight config", exception);
        }
    }

    private static boolean sanitizeConfig() {
        boolean changed = false;

        if (config.itemOverrides == null) {
            config.itemOverrides = new LinkedHashMap<>();
            changed = true;
        }

        float clampedWeightToSlowdown = sanitizeWeight(config.weightToSlowdown, ArmorWeightConfig.DEFAULT_WEIGHT_TO_SLOWDOWN);
        if (Float.compare(clampedWeightToSlowdown, config.weightToSlowdown) != 0) {
            config.weightToSlowdown = clampedWeightToSlowdown;
            changed = true;
        }

        float clampedMaxSlowdown = sanitizeRange(config.maxSlowdown, ArmorWeightConfig.DEFAULT_MAX_SLOWDOWN, 0.0F, 0.95F);
        if (Float.compare(clampedMaxSlowdown, config.maxSlowdown) != 0) {
            config.maxSlowdown = clampedMaxSlowdown;
            changed = true;
        }

        float clampedToughnessWeight = sanitizeWeight(config.toughnessWeight, ArmorWeightConfig.DEFAULT_TOUGHNESS_WEIGHT);
        if (Float.compare(clampedToughnessWeight, config.toughnessWeight) != 0) {
            config.toughnessWeight = clampedToughnessWeight;
            changed = true;
        }

        float clampedKnockbackWeight = sanitizeWeight(config.knockbackWeight, ArmorWeightConfig.DEFAULT_KNOCKBACK_WEIGHT);
        if (Float.compare(clampedKnockbackWeight, config.knockbackWeight) != 0) {
            config.knockbackWeight = clampedKnockbackWeight;
            changed = true;
        }

        return changed;
    }

    private static SyncResult syncArmorItems() {
        Map<String, ArmorWeightConfig.ArmorItemOverride> previousOverrides = config.itemOverrides;
        Map<String, ArmorWeightConfig.ArmorItemOverride> syncedOverrides = new LinkedHashMap<>();
        List<Identifier> itemIds = new ArrayList<>(BuiltInRegistries.ITEM.keySet());
        itemIds.sort(Comparator.comparing(Identifier::toString));
        boolean changed = false;

        for (Identifier itemId : itemIds) {
            Item item = BuiltInRegistries.ITEM.getValue(itemId);
            ItemStack stack;

            try {
                stack = item.getDefaultInstance();
            } catch (RuntimeException exception) {
                if (isComponentsUnavailable(exception)) {
                    return new SyncResult(false, false);
                }

                throw exception;
            }

            if (!ArmorWeightCalculator.isArmor(stack)) {
                continue;
            }

            String key = itemId.toString();
            float defaultWeight = ArmorWeightCalculator.getDefaultArmorItemWeight(stack);
            ArmorWeightConfig.ArmorItemOverride override = previousOverrides.get(key);

            if (override == null) {
                override = new ArmorWeightConfig.ArmorItemOverride(false, defaultWeight);
                changed = true;
            } else {
                float sanitizedCustomWeight = sanitizeWeight(override.customWeight, defaultWeight);
                if (Float.compare(sanitizedCustomWeight, override.customWeight) != 0) {
                    override.customWeight = sanitizedCustomWeight;
                    changed = true;
                }

                if (!override.useCustomWeight && Float.compare(override.customWeight, defaultWeight) != 0) {
                    override.customWeight = defaultWeight;
                    changed = true;
                }
            }

            syncedOverrides.put(key, override);
        }

        for (Map.Entry<String, ArmorWeightConfig.ArmorItemOverride> entry : previousOverrides.entrySet()) {
            if (!syncedOverrides.containsKey(entry.getKey())) {
                syncedOverrides.put(entry.getKey(), entry.getValue());
            }
        }

        if (previousOverrides.size() != syncedOverrides.size()) {
            changed = true;
        }

        config.itemOverrides = syncedOverrides;
        return new SyncResult(true, changed);
    }

    private static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(config, writer);
            }
        } catch (IOException exception) {
            ArmorWeightMod.LOGGER.error("Failed to save armor weight config", exception);
        }
    }

    private static float sanitizeWeight(float value, float fallback) {
        if (!Float.isFinite(value) || value < 0.0F) {
            return fallback;
        }

        return value;
    }

    private static float sanitizeRange(float value, float fallback, float min, float max) {
        if (!Float.isFinite(value)) {
            return fallback;
        }

        return Mth.clamp(value, min, max);
    }

    private static boolean isComponentsUnavailable(RuntimeException exception) {
        String message = exception.getMessage();
        return message != null && message.contains("Components not bound yet");
    }

    private record SyncResult(boolean completed, boolean changed) {
    }
}

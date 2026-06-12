package com.megatrex4.playerex;

import com.bibireden.playerex.api.event.LivingEntityEvents;
import com.megatrex4.api.v1.InventoryWeightApi;
import com.megatrex4.config.PlayerEXInventoryWeightConfig;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public final class PlayerEXInventoryWeightAttributeManager {

    private static final UUID MODIFIER_UUID =
            UUID.fromString("0f84866f-31d1-4d7f-9f2f-4f30c6a7b8e1");

    private static final String MODIFIER_NAME =
            "PlayerEX Inventory Weight Skill Capacity";

    private PlayerEXInventoryWeightAttributeManager() {
    }

    public static void register() {
        LivingEntityEvents.ON_EVERY_SECOND.register(entity -> {
            if (entity instanceof ServerPlayerEntity player) {
                updatePlayer(player);
            }
        });
    }

    public static void updatePlayer(ServerPlayerEntity player) {
        PlayerEXInventoryWeightConfig.Server config = PlayerEXInventoryWeightConfig.getServer();

        if (
                !config.enabled
                        || !config.skill.enabled
                        || config.skill.capacityMode != PlayerEXInventoryWeightConfig.SkillCapacityMode.INVENTORY_WEIGHT_ATTRIBUTE
        ) {
            removeModifier(player);
            return;
        }

        double skillLevel = Math.max(
                0.0D,
                PlayerEXAttributeAccess.getAttributeValue(player, config.skill.attributeId)
        );

        double value = skillLevel * config.skill.attributeValuePerLevel;

        EntityAttributeInstance instance = InventoryWeightApi.getMaxWeightAttributeInstance(player);

        if (instance == null) {
            return;
        }

        if (instance.getModifier(MODIFIER_UUID) != null) {
            instance.removeModifier(MODIFIER_UUID);
        }

        if (value <= 0.0D) {
            return;
        }

        instance.addTemporaryModifier(new EntityAttributeModifier(
                MODIFIER_UUID,
                MODIFIER_NAME,
                value,
                config.skill.attributeOperation
        ));
    }

    private static void removeModifier(ServerPlayerEntity player) {
        EntityAttributeInstance instance = InventoryWeightApi.getMaxWeightAttributeInstance(player);

        if (instance == null) {
            return;
        }

        if (instance.getModifier(MODIFIER_UUID) != null) {
            instance.removeModifier(MODIFIER_UUID);
        }
    }
}
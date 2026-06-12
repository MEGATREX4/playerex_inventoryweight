package com.megatrex4.playerex;

import com.bibireden.playerex.api.PlayerEXAPI;
import com.bibireden.playerex.api.attribute.PlayerEXAttributes;
import com.megatrex4.PlayerEXInventoryWeight;
import com.megatrex4.config.PlayerEXInventoryWeightConfig;
import net.minecraft.util.Identifier;

import java.util.Set;

public final class PlayerEXPrimaryAttributeIntegration {

    private static boolean refundConditionRegistered = false;

    private PlayerEXPrimaryAttributeIntegration() {
    }

    public static void register() {
        PlayerEXInventoryWeightConfig.Server config = PlayerEXInventoryWeightConfig.getServer();

        if (!config.enabled) {
            return;
        }

        if (!config.carryingAttribute.enabled) {
            return;
        }

        if (config.carryingAttribute.addToPrimaryAttributeList) {
            addCarryingToPrimaryAttributes();
        }

        registerRefundCondition();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void addCarryingToPrimaryAttributes() {
        /*
         * PlayerEXAttributes.PRIMARY_ATTRIBUTE_IDS is a Kotlin setOf(...).
         * In current PlayerEX 4.0.5+1.20.1 this is backed by a mutable LinkedHashSet,
         * even though the exposed type is Set.
         */
        Set<Identifier> primaryAttributes = (Set) PlayerEXAttributes.PRIMARY_ATTRIBUTE_IDS;

        Identifier carryingId = PlayerEXInventoryWeightAttributes.CARRYING_ID;

        if (primaryAttributes.contains(carryingId)) {
            PlayerEXInventoryWeight.LOGGER.info(
                    "PlayerEX primary attribute list already contains {}",
                    carryingId
            );
            return;
        }

        try {
            primaryAttributes.add(carryingId);

            PlayerEXInventoryWeight.LOGGER.info(
                    "Added {} to PlayerEX primary attribute list",
                    carryingId
            );
        } catch (UnsupportedOperationException exception) {
            PlayerEXInventoryWeight.LOGGER.error(
                    "Could not add {} to PlayerEX primary attribute list because the set is immutable. " +
                            "A mixin fallback will be needed for this PlayerEX version.",
                    carryingId,
                    exception
            );
        }
    }

    private static void registerRefundCondition() {
        if (refundConditionRegistered) {
            return;
        }

        refundConditionRegistered = true;

        /*
         * PlayerEX's default RefundFactory only counts:
         * constitution, strength, dexterity, intelligence, luckiness, focus.
         *
         * We add carrying here so players can refund carrying points too.
         */
        PlayerEXAPI.registerRefundCondition(
                (data, player) -> data.get(PlayerEXInventoryWeightAttributes.CARRYING)
        );

        PlayerEXInventoryWeight.LOGGER.info(
                "Registered PlayerEX refund condition for {}",
                PlayerEXInventoryWeightAttributes.CARRYING_ID
        );
    }
}
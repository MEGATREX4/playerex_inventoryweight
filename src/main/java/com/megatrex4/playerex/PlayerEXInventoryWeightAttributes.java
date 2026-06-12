package com.megatrex4.playerex;

import com.megatrex4.PlayerEXInventoryWeight;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class PlayerEXInventoryWeightAttributes {

    public static final Identifier CARRYING_ID =
            Identifier.of(PlayerEXInventoryWeight.MOD_ID, "carrying");

    public static final EntityAttribute CARRYING =
            Registry.register(
                    Registries.ATTRIBUTE,
                    CARRYING_ID,
                    new ClampedEntityAttribute(
                            "attribute.name.playerex_inventoryweight.carrying",
                            0.0D,
                            0.0D,
                            100.0D
                    ).setTracked(true)
            );

    private PlayerEXInventoryWeightAttributes() {
    }

    public static void register() {
        PlayerEXInventoryWeight.LOGGER.info(
                "Registered PlayerEX Inventory Weight attribute: {}",
                CARRYING_ID
        );
    }
}
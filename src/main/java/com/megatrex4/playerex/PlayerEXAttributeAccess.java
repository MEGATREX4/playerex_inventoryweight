package com.megatrex4.playerex;

import com.megatrex4.PlayerEXInventoryWeight;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public final class PlayerEXAttributeAccess {

    private PlayerEXAttributeAccess() {
    }

    public static double getAttributeValue(ServerPlayerEntity player, String attributeIdString) {
        Identifier id = Identifier.tryParse(attributeIdString);

        if (id == null) {
            PlayerEXInventoryWeight.LOGGER.warn(
                    "Invalid PlayerEX attribute id '{}'",
                    attributeIdString
            );
            return 0.0D;
        }

        EntityAttribute attribute = Registries.ATTRIBUTE.get(id);

        if (attribute == null) {
            return 0.0D;
        }

        EntityAttributeInstance instance = player.getAttributeInstance(attribute);

        if (instance == null) {
            return 0.0D;
        }

        return instance.getValue();
    }
}
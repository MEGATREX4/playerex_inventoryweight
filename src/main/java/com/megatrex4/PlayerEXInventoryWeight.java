package com.megatrex4;

import com.megatrex4.api.v1.InventoryWeightEvents;
import com.megatrex4.config.ModConfigs;
import com.megatrex4.config.PlayerEXInventoryWeightConfig;
import com.megatrex4.playerex.PlayerEXAttributeAccess;
import com.megatrex4.playerex.PlayerEXInventoryWeightAttributeManager;
import com.megatrex4.playerex.PlayerEXInventoryWeightAttributes;
import com.megatrex4.playerex.PlayerEXPrimaryAttributeIntegration;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerEXInventoryWeight implements ModInitializer {

	public static final String MOD_ID = "playerex_inventoryweight";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModConfigs.init();

		PlayerEXInventoryWeightAttributes.register();
		PlayerEXPrimaryAttributeIntegration.register();
		PlayerEXInventoryWeightAttributeManager.register();

		InventoryWeightEvents.MODIFY_MAX_WEIGHT.register(PlayerEXInventoryWeight::modifyMaxWeight);

		LOGGER.info("PlayerEX Inventory Weight initialized");
	}

	private static float modifyMaxWeight(ServerPlayerEntity player, float currentMaxWeight) {
		PlayerEXInventoryWeightConfig.Server config = PlayerEXInventoryWeightConfig.getServer();

		if (!config.enabled) {
			return currentMaxWeight;
		}

		float additive = 0.0f;
		float multiplier = 1.0f;

		if (config.level.enabled) {
			int level = Math.max(
					0,
					(int) Math.floor(PlayerEXAttributeAccess.getAttributeValue(player, config.level.levelAttributeId))
			);

			additive += level * config.level.additivePerLevel;

			multiplier *= Math.max(
					0.0f,
					1.0f + level * config.level.multiplierPerLevel
			);
		}

		if (
				config.skill.enabled
						&& config.skill.capacityMode == PlayerEXInventoryWeightConfig.SkillCapacityMode.EVENT_MODIFIER
		) {
			int skillLevel = Math.max(
					0,
					(int) Math.floor(PlayerEXAttributeAccess.getAttributeValue(player, config.skill.attributeId))
			);

			additive += skillLevel * config.skill.additivePerLevel;

			multiplier *= Math.max(
					0.0f,
					1.0f + skillLevel * config.skill.multiplierPerLevel
			);
		}

		float result = (currentMaxWeight + additive) * multiplier;

		return Math.max(config.minimumMaxWeight, result);
	}
}
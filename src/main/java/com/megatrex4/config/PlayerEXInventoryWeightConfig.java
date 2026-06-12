package com.megatrex4.config;

import com.megatrex4.PlayerEXInventoryWeight;
import me.fzzyhmstrs.fzzy_config.annotations.Comment;
import me.fzzyhmstrs.fzzy_config.annotations.Version;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.config.ConfigSection;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedFloat;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.util.Identifier;

public final class PlayerEXInventoryWeightConfig {

    public static final Identifier SERVER_CONFIG_ID =
            Identifier.of(PlayerEXInventoryWeight.MOD_ID, "server-config");

    private static Server SERVER_INSTANCE;

    private PlayerEXInventoryWeightConfig() {
    }

    public static Server getServer() {
        if (SERVER_INSTANCE == null) {
            SERVER_INSTANCE = ConfigApiJava.registerAndLoadConfig(Server::new, RegisterType.BOTH);
        }

        return SERVER_INSTANCE;
    }

    public enum SkillCapacityMode {
        /**
         * The add-on reads the PlayerEX skill/attribute value and modifies
         * MT Inventory Weight through InventoryWeightEvents.MODIFY_MAX_WEIGHT.
         *
         * Best option for multiplier-based logic.
         */
        EVENT_MODIFIER,

        /**
         * The add-on writes a modifier onto inventoryweight:generic.max_weight.
         *
         * Best option for flat attribute-style bonuses.
         */
        INVENTORY_WEIGHT_ATTRIBUTE
    }

    @Version(version = 1)
    public static class Server extends Config {

        public Server() {
            super(SERVER_CONFIG_ID);
        }

        @Comment("Master switch for the PlayerEX Inventory Weight integration.")
        public boolean enabled = true;

        @Comment("Minimum max weight after all PlayerEX formulas are applied.")
        @ValidatedFloat.Restrict(min = 1.0f, max = Float.MAX_VALUE)
        public float minimumMaxWeight = 1.0f;

        @Comment("Formula using the player's PlayerEX level attribute.")
        public LevelFormula level = new LevelFormula();

        @Comment("Formula using one configured PlayerEX skill/attribute.")
        public SkillFormula skill = new SkillFormula();

        @Comment("Settings for the optional custom Carrying PlayerEX attribute.")
        public CarryingAttribute carryingAttribute = new CarryingAttribute();





    }

    public static class LevelFormula extends ConfigSection {



        @Comment("If true, the PlayerEX level attribute affects max inventory weight.")
        public boolean enabled = true;

        @Comment("""
                PlayerEX level attribute id.

                Default:
                playerex:level
                """)
        public String levelAttributeId = "playerex:level";

        @Comment("""
                Flat max-weight bonus per PlayerEX level.

                Example:
                additivePerLevel = 5.0
                PlayerEX level = 10
                bonus = +50 max weight
                """)
        @ValidatedFloat.Restrict(min = 0.0f, max = Float.MAX_VALUE)
        public float additivePerLevel = 0.0f;

        @Comment("""
                Multiplier bonus per PlayerEX level.

                0.01 = +1% per level
                0.02 = +2% per level
                """)
        @ValidatedFloat.Restrict(min = 0.0f, max = Float.MAX_VALUE)
        public float multiplierPerLevel = 0.0f;
    }

    public static class SkillFormula extends ConfigSection {

        @Comment("If true, the configured PlayerEX skill/attribute affects max inventory weight.")
        public boolean enabled = true;

        @Comment("""
                How the configured PlayerEX skill/attribute should affect max inventory weight.

                EVENT_MODIFIER:
                Reads the configured PlayerEX attribute and applies additive/multiplier formula logic.

                INVENTORY_WEIGHT_ATTRIBUTE:
                Applies a modifier directly to inventoryweight:generic.max_weight.
                Best for flat ADD_VALUE bonuses.
                """)
        public SkillCapacityMode capacityMode = SkillCapacityMode.EVENT_MODIFIER;

        @Comment("""
                PlayerEX skill/attribute id.

                Custom add-on skill:
                playerex_inventoryweight:carrying

                Existing PlayerEX examples:
                playerex:constitution
                playerex:strength
                playerex:dexterity
                playerex:intelligence
                playerex:luckiness
                playerex:focus
                playerex:mining
                playerex:smithing
                playerex:farming
                """)
        public String attributeId = "playerex_inventoryweight:carrying";

        @Comment("""
                Flat max-weight bonus per configured skill level.

                Used when capacityMode = EVENT_MODIFIER.
                """)
        @ValidatedFloat.Restrict(min = 0.0f, max = Float.MAX_VALUE)
        public float additivePerLevel = 0.0f;

        @Comment("""
                Multiplier bonus per configured skill level.

                Used when capacityMode = EVENT_MODIFIER.

                0.05 = +5% per skill level.
                """)
        @ValidatedFloat.Restrict(min = 0.0f, max = Float.MAX_VALUE)
        public float multiplierPerLevel = 0.05f;

        @Comment("""
                Attribute value per configured skill level when capacityMode = INVENTORY_WEIGHT_ATTRIBUTE.

                With ADD_VALUE:
                10.0 means +10 max weight per skill level.
                """)
        @ValidatedFloat.Restrict(min = 0.0f, max = Float.MAX_VALUE)
        public float attributeValuePerLevel = 10.0f;

        @Comment("""
                Attribute operation used when capacityMode = INVENTORY_WEIGHT_ATTRIBUTE.

                Recommended:
                ADD_VALUE
                """)
        public EntityAttributeModifier.Operation attributeOperation =
                EntityAttributeModifier.Operation.ADDITION;
    }

    public static class CarryingAttribute extends ConfigSection {

        @Comment("""
            This controls whether the add-on should expect/use its custom Carrying attribute.

            The attribute is still registered by the mod, but this option controls whether
            the default config points at it.
            """)
        public boolean enabled = true;

        @Comment("""
            If true, the custom Carrying attribute is added directly to PlayerEX's main attribute list.

            This makes it appear together with:
            constitution, strength, dexterity, intelligence, luckiness, focus.

            This replaces the separate Inventory Weight PlayerEX screen/page.
            """)
        public boolean addToPrimaryAttributeList = true;

        @Comment("Maximum value for the custom Carrying attribute.")
        @ValidatedFloat.Restrict(min = 1.0f, max = Float.MAX_VALUE)
        public float maxValue = 100.0f;
    }
}
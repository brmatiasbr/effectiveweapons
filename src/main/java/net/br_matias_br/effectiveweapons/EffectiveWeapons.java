package net.br_matias_br.effectiveweapons;

import net.br_matias_br.effectiveweapons.block.EffectiveWeaponsBlocks;
import net.br_matias_br.effectiveweapons.client.particle.EffectiveWeaponsParticles;
import net.br_matias_br.effectiveweapons.effect.*;
import net.br_matias_br.effectiveweapons.entity.EffectiveWeaponsDamageSources;
import net.br_matias_br.effectiveweapons.entity.EffectiveWeaponsEntities;
import net.br_matias_br.effectiveweapons.item.EffectiveWeaponsItemGroup;
import net.br_matias_br.effectiveweapons.item.EffectiveWeaponsItems;
import net.br_matias_br.effectiveweapons.item.custom.AttunableItem;
import net.br_matias_br.effectiveweapons.item.custom.DoubleBowItem;
import net.br_matias_br.effectiveweapons.item.model.EffectiveWeaponsModelPredicateProviders;
import net.br_matias_br.effectiveweapons.networking.EffectiveWeaponsNetworking;
import net.br_matias_br.effectiveweapons.util.EffectiveWeaponsLootTableModifiers;
import net.fabricmc.api.ModInitializer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EffectiveWeapons implements ModInitializer {
    public static final String MOD_ID = "effectiveweapons";
    public static final Logger LOGGER = LoggerFactory.getLogger("effectiveweapons");

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing EffectiveWeapons");

        EffectiveWeaponsItems.registerItems();
        EffectiveWeaponsBlocks.registerModBlocks();
        EffectiveWeaponsItemGroup.registerItemGroups();
        EffectiveWeaponsEffects.registerEffects();

        EffectiveWeaponsNetworking.registerCustomPayloads();
        EffectiveWeaponsNetworking.registerServerReceivers();
        EffectiveWeaponsEntities.registerEntities();
        EffectiveWeaponsDamageSources.registerDamageSources();
        EffectiveWeaponsModelPredicateProviders.registerModelPredicateProviders();
        EffectiveWeaponsParticles.registerParticles();
        EffectiveWeaponsLootTableModifiers.modifyLootTables();

        EffectiveWeaponsPotions.registerPotions();
    }

    public static final String PASSIVE_ABILITY = "effectiveweapons:passive_ability";
    public static final String METER_ABILITY = "effectiveweapons:meter_ability";
    public static final String PASSIVE_NONE = "effectiveweapons:passive_none";
    public static final String METER_NONE = "effectiveweapons:meter_none";

    public static final String PASSIVE_FEATHERWEIGHT = "effectiveweapons:passive_featherweight";
    public static final String PASSIVE_STRIDE = "effectiveweapons:passive_stride";
    public static final String PASSIVE_LUCKY = "effectiveweapons:passive_lucky";
    public static final String PASSIVE_STURDY = "effectiveweapons:passive_sturdy";
    public static final String PASSIVE_TOUGH = "effectiveweapons:passive_tough";
    public static final String PASSIVE_FIRM = "effectiveweapons:passive_firm";
    public static final String PASSIVE_SWEEP = "effectiveweapons:passive_sweep";
    public static final String PASSIVE_ACROBATICS = "effectiveweapons:passive_acrobatics";
    public static final String PASSIVE_BUFFER = "effectiveweapons:passive_buffer";
    public static final String PASSIVE_MIGHTY = "effectiveweapons:passive_mighty";
    public static final String PASSIVE_LUMBERJACK = "effectiveweapons:passive_lumberjack";
    public static final String PASSIVE_STABBING = "effectiveweapons:passive_stabbing";
    public static final String PASSIVE_MOBILE = "effectiveweapons:passive_mobile";

    public static AttributeModifiersComponent getAttributeModifiersOf(String key, ItemStack stack, AttunableItem attunableItem){
        RegistryEntry<EntityAttribute> attribute = getAttributeOf(key);
        EntityAttributeModifier modifier = getAttributeValueOf(key);

        AttributeModifiersComponent attributeModifiersComponent = stack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS,
                attunableItem.getDefaultAttributeModifiers());

        if(attribute != null && modifier != null){
            attributeModifiersComponent = attributeModifiersComponent.with(attribute, modifier, AttributeModifierSlot.HAND);
            if (key.equals(PASSIVE_ACROBATICS)) {
                attributeModifiersComponent = attributeModifiersComponent.with(EntityAttributes.GENERIC_FALL_DAMAGE_MULTIPLIER,
                        new EntityAttributeModifier(Identifier.of(EffectiveWeapons.MOD_ID, "effectiveweapons_acrobatics_fall"), -0.5, EntityAttributeModifier.Operation.ADD_VALUE),
                        AttributeModifierSlot.HAND);
            }
            if (key.equals(DoubleBowItem.PASSIVE_FIRM_STANCE)) {
                attributeModifiersComponent = attributeModifiersComponent.with(EntityAttributes.GENERIC_MOVEMENT_SPEED,
                        new EntityAttributeModifier(Identifier.of(EffectiveWeapons.MOD_ID, "effectiveweapons_firm_stance_slowness"), -0.75, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
                        AttributeModifierSlot.HAND);
            }
            if (key.equals(PASSIVE_MOBILE)) {
                attributeModifiersComponent = attributeModifiersComponent.with(EntityAttributes.GENERIC_MOVEMENT_EFFICIENCY,
                        new EntityAttributeModifier(Identifier.of(EffectiveWeapons.MOD_ID, "effectiveweapons_mobile_efficiency"), 0.35, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
                        AttributeModifierSlot.HAND);
            }
        }

        return attributeModifiersComponent;
    }

    public static RegistryEntry<EntityAttribute> getAttributeOf(String key) {
        return switch (key) {
            case PASSIVE_FEATHERWEIGHT -> EntityAttributes.GENERIC_SAFE_FALL_DISTANCE;
            case PASSIVE_STRIDE -> EntityAttributes.GENERIC_STEP_HEIGHT;
            case PASSIVE_LUCKY -> EntityAttributes.GENERIC_LUCK;
            case PASSIVE_STURDY -> EntityAttributes.GENERIC_ARMOR;
            case PASSIVE_TOUGH -> EntityAttributes.GENERIC_ARMOR_TOUGHNESS;
            case PASSIVE_FIRM -> EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE;
            case PASSIVE_SWEEP -> EntityAttributes.PLAYER_SWEEPING_DAMAGE_RATIO;
            case PASSIVE_ACROBATICS -> EntityAttributes.GENERIC_JUMP_STRENGTH;
            case PASSIVE_BUFFER -> EntityAttributes.GENERIC_MAX_HEALTH;
            case PASSIVE_MIGHTY -> EntityAttributes.GENERIC_ATTACK_DAMAGE;
            case PASSIVE_LUMBERJACK -> EntityAttributes.PLAYER_BLOCK_BREAK_SPEED;
            case DoubleBowItem.PASSIVE_FIRM_STANCE -> EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE;
            case PASSIVE_STABBING -> EntityAttributes.GENERIC_ATTACK_SPEED;
            case PASSIVE_MOBILE -> EntityAttributes.GENERIC_MOVEMENT_SPEED;
            default -> null;
        };
    }
    public static EntityAttributeModifier getAttributeValueOf(String key) {
        return switch (key) {
            case PASSIVE_FEATHERWEIGHT -> new EntityAttributeModifier(Identifier.of(EffectiveWeapons.MOD_ID, "effectiveweapons_featherweight"),
                    3, EntityAttributeModifier.Operation.ADD_VALUE);
            case PASSIVE_STRIDE -> new EntityAttributeModifier(Identifier.of(EffectiveWeapons.MOD_ID, "effectiveweapons_stride"),
                    0.4, EntityAttributeModifier.Operation.ADD_VALUE);
            case PASSIVE_LUCKY -> new EntityAttributeModifier(Identifier.of(EffectiveWeapons.MOD_ID, "effectiveweapons_lucky"),
                    3, EntityAttributeModifier.Operation.ADD_VALUE);
            case PASSIVE_STURDY -> new EntityAttributeModifier(Identifier.of(EffectiveWeapons.MOD_ID, "effectiveweapons_sturdy"),
                    3, EntityAttributeModifier.Operation.ADD_VALUE);
            case PASSIVE_TOUGH -> new EntityAttributeModifier(Identifier.of(EffectiveWeapons.MOD_ID, "effectiveweapons_tough"),
                    5, EntityAttributeModifier.Operation.ADD_VALUE);
            case PASSIVE_FIRM -> new EntityAttributeModifier(Identifier.of(EffectiveWeapons.MOD_ID, "effectiveweapons_firm"),
                    0.3, EntityAttributeModifier.Operation.ADD_VALUE);
            case PASSIVE_SWEEP -> new EntityAttributeModifier(Identifier.of(EffectiveWeapons.MOD_ID, "effectiveweapons_sweep"),
                    0.64, EntityAttributeModifier.Operation.ADD_VALUE);
            case PASSIVE_ACROBATICS -> new EntityAttributeModifier(Identifier.of(EffectiveWeapons.MOD_ID, "effectiveweapons_acrobatics"),
                    0.5, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            case PASSIVE_BUFFER -> new EntityAttributeModifier(Identifier.of(EffectiveWeapons.MOD_ID, "effectiveweapons_buffer"),
                    4, EntityAttributeModifier.Operation.ADD_VALUE);
            case PASSIVE_MIGHTY -> new EntityAttributeModifier(Identifier.of(EffectiveWeapons.MOD_ID, "effectiveweapons_mighty"),
                    2, EntityAttributeModifier.Operation.ADD_VALUE);
            case PASSIVE_LUMBERJACK -> new EntityAttributeModifier(Identifier.of(EffectiveWeapons.MOD_ID, "effectiveweapons_lumberjack"),
                    0.75, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            case DoubleBowItem.PASSIVE_FIRM_STANCE -> new EntityAttributeModifier(Identifier.of(EffectiveWeapons.MOD_ID, "effectiveweapons_firm_stance"),
                    3, EntityAttributeModifier.Operation.ADD_VALUE);
            case PASSIVE_STABBING -> new EntityAttributeModifier(Identifier.of(EffectiveWeapons.MOD_ID, "effectiveweapons_stabbing"),
                    0.5, EntityAttributeModifier.Operation.ADD_VALUE);
            case PASSIVE_MOBILE -> new EntityAttributeModifier(Identifier.of(EffectiveWeapons.MOD_ID, "effectiveweapons_mobile"),
                    0.3, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            default -> null;
        };
    }

    public static int getColorFromGradient(int colorHex1, int colorHex2, float pos){
        int r1, g1, b1, r2, g2, b2, rF, gF, bF;
        r1 = colorHex1 >> 16;
        g1 = (colorHex1 >> 8) & 0xFF;
        b1 = colorHex1 & 0xFF;

        r2 = colorHex2 >> 16;
        g2 = (colorHex2 >> 8) & 0xFF;
        b2 = colorHex2 & 0xFF;

        rF = (int)((r1 * pos) + (r2 * (1 - pos)));
        gF = (int)((g1 * pos) + (g2 * (1 - pos)));
        bF = (int)((b1 * pos) + (b2 * (1 - pos)));

        return (rF << 16) | (gF << 8) | (bF);
    }
}

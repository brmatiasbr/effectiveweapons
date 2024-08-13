package net.br_matias_br.effectiveweapons;

import net.br_matias_br.effectiveweapons.block.EffectiveWeaponsBlocks;
import net.br_matias_br.effectiveweapons.client.particle.EffectiveWeaponsParticles;
import net.br_matias_br.effectiveweapons.effect.EffectiveWeaponsPotions;
import net.br_matias_br.effectiveweapons.effect.ElevatedEffect;
import net.br_matias_br.effectiveweapons.effect.FireGuardEffect;
import net.br_matias_br.effectiveweapons.entity.EffectiveWeaponsDamageSources;
import net.br_matias_br.effectiveweapons.entity.EffectiveWeaponsEntities;
import net.br_matias_br.effectiveweapons.item.EffectiveWeaponsItemGroup;
import net.br_matias_br.effectiveweapons.item.EffectiveWeaponsItems;
import net.br_matias_br.effectiveweapons.item.custom.AttunableItem;
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
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
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

        EffectiveWeaponsNetworking.registerCustomPayloads();
        EffectiveWeaponsNetworking.registerServerReceivers();
        EffectiveWeaponsEntities.registerEntities();
        EffectiveWeaponsDamageSources.registerDamageSources();
        EffectiveWeaponsModelPredicateProviders.registerModelPredicateProviders();
        EffectiveWeaponsParticles.registerParticles();
        EffectiveWeaponsLootTableModifiers.modifyLootTables();

        EffectiveWeaponsPotions.registerPotions();
    }

    //    public static final StatusEffect FIRE_GUARD = Registry.register(Registries.STATUS_EFFECT, Identifier.of(EffectiveWeapons.MOD_ID, "fire_guard"), new FireGuardEffect());
    public static final RegistryEntry<StatusEffect> FIRE_GUARD_REGISTRY_ENTRY = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(EffectiveWeapons.MOD_ID, "fire_guard"), new FireGuardEffect()
            .addAttributeModifier(EntityAttributes.GENERIC_BURNING_TIME, Identifier.of(MOD_ID ,"effect.fire_guard"), -0.5f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final RegistryEntry<StatusEffect> ELEVATED_REGISTRY_ENTRY = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(EffectiveWeapons.MOD_ID, "elevated"), new ElevatedEffect()
            .addAttributeModifier(EntityAttributes.GENERIC_MOVEMENT_SPEED, Identifier.of(MOD_ID ,"effect.elevated"), 0.65f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    public static final String PASSIVE_ABILITY = "effectiveweapons:passive_ability";
    public static final String METER_ABILITY = "effectiveweapons:meter_ability";
    public static final String PASSIVE_NONE = "effectiveweapons:passive_none";
    public static final String METER_NONE = "effectiveweapons:meter_none";

    public static final String PASSIVE_FEATHERWEIGHT = "effectiveweapons:passive_featherweight";
    public static final String PASSIVE_STRIDE = "effectiveweapons:passive_stride";
    public static final String PASSIVE_LUCKY = "effectiveweapons:passive_lucky";
    public static final String PASSIVE_STURDY = "effectiveweapons:passive_sturdy";
    public static final String PASSIVE_THOUGH = "effectiveweapons:passive_though";
    public static final String PASSIVE_FIRM = "effectiveweapons:passive_firm";
    public static final String PASSIVE_SWEEP = "effectiveweapons:passive_sweep";
    public static final String PASSIVE_ACROBATICS = "effectiveweapons:passive_acrobatics";
    public static final String PASSIVE_BUFFER = "effectiveweapons:passive_buffer";

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
        }

        return attributeModifiersComponent;
    }

    public static RegistryEntry<EntityAttribute> getAttributeOf(String key) {
        return switch (key) {
            case PASSIVE_FEATHERWEIGHT -> EntityAttributes.GENERIC_SAFE_FALL_DISTANCE;
            case PASSIVE_STRIDE -> EntityAttributes.GENERIC_STEP_HEIGHT;
            case PASSIVE_LUCKY -> EntityAttributes.GENERIC_LUCK;
            case PASSIVE_STURDY -> EntityAttributes.GENERIC_ARMOR;
            case PASSIVE_THOUGH -> EntityAttributes.GENERIC_ARMOR_TOUGHNESS;
            case PASSIVE_FIRM -> EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE;
            case PASSIVE_SWEEP -> EntityAttributes.PLAYER_SWEEPING_DAMAGE_RATIO;
            case PASSIVE_ACROBATICS -> EntityAttributes.GENERIC_JUMP_STRENGTH;
            case PASSIVE_BUFFER -> EntityAttributes.GENERIC_MAX_HEALTH;
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
            case PASSIVE_THOUGH -> new EntityAttributeModifier(Identifier.of(EffectiveWeapons.MOD_ID, "effectiveweapons_tough"),
                    5, EntityAttributeModifier.Operation.ADD_VALUE);
            case PASSIVE_FIRM -> new EntityAttributeModifier(Identifier.of(EffectiveWeapons.MOD_ID, "effectiveweapons_firm"),
                    0.3, EntityAttributeModifier.Operation.ADD_VALUE);
            case PASSIVE_SWEEP -> new EntityAttributeModifier(Identifier.of(EffectiveWeapons.MOD_ID, "effectiveweapons_sweep"),
                    0.64, EntityAttributeModifier.Operation.ADD_VALUE);
            case PASSIVE_ACROBATICS -> new EntityAttributeModifier(Identifier.of(EffectiveWeapons.MOD_ID, "effectiveweapons_acrobatics"),
                    0.5, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            case PASSIVE_BUFFER -> new EntityAttributeModifier(Identifier.of(EffectiveWeapons.MOD_ID, "effectiveweapons_buffer"),
                    4, EntityAttributeModifier.Operation.ADD_VALUE);
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

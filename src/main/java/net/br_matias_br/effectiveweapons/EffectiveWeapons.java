package net.br_matias_br.effectiveweapons;

import net.br_matias_br.effectiveweapons.block.EffectiveWeaponsBlocks;
import net.br_matias_br.effectiveweapons.client.particle.EffectiveWeaponsParticles;
import net.br_matias_br.effectiveweapons.effect.EffectiveWeaponsPotions;
import net.br_matias_br.effectiveweapons.effect.FireGuardEffect;
import net.br_matias_br.effectiveweapons.entity.EffectiveWeaponsDamageSources;
import net.br_matias_br.effectiveweapons.entity.EffectiveWeaponsEntities;
import net.br_matias_br.effectiveweapons.item.EffectiveWeaponsItemGroup;
import net.br_matias_br.effectiveweapons.item.EffectiveWeaponsItems;
import net.br_matias_br.effectiveweapons.item.model.EffectiveWeaponsModelPredicateProviders;
import net.br_matias_br.effectiveweapons.networking.EffectiveWeaponsNetworking;
import net.br_matias_br.effectiveweapons.util.EffectiveWeaponsLootTableModifiers;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EffectiveWeapons implements ModInitializer {
    public static final String MOD_ID = "effectiveweapons";

    public static final Logger LOGGER = LoggerFactory.getLogger("effectiveweapons");
//    public static final StatusEffect FIRE_GUARD = Registry.register(Registries.STATUS_EFFECT, Identifier.of(EffectiveWeapons.MOD_ID, "fire_guard"), new FireGuardEffect());
    public static final RegistryEntry<StatusEffect> FIRE_GUARD_REGISTRY_ENTRY = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(EffectiveWeapons.MOD_ID, "fire_guard"), new FireGuardEffect());

    public static final String PASSIVE_ABILITY = "effectiveweapons:passive_ability";
    public static final String METER_ABILITY = "effectiveweapons:meter_ability";
    public static final String PASSIVE_NONE = "effectiveweapons:passive_none";
    public static final String METER_NONE = "effectiveweapons:meter_none";

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

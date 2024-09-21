package net.br_matias_br.effectiveweapons.entity;

import net.br_matias_br.effectiveweapons.EffectiveWeapons;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class EffectiveWeaponsDamageSources {
    public static final RegistryKey<DamageType> PACT_AXE_RECOIL_DAMAGE = RegistryKey.of(
            RegistryKeys.DAMAGE_TYPE,Identifier.of(EffectiveWeapons.MOD_ID, "pact_axe_recoil_damage"));

    public static final RegistryKey<DamageType> DOUBLE_BOW_RECOIL_DAMAGE = RegistryKey.of(
            RegistryKeys.DAMAGE_TYPE,Identifier.of(EffectiveWeapons.MOD_ID, "double_bow_recoil_damage"));

    public static final RegistryKey<DamageType> ELEVATED_RECOIL_DAMAGE = RegistryKey.of(
            RegistryKeys.DAMAGE_TYPE,Identifier.of(EffectiveWeapons.MOD_ID, "elevated_recoil_damage"));

    public static final RegistryKey<DamageType> COUNTER_REFLECTED_DAMAGE = RegistryKey.of(
            RegistryKeys.DAMAGE_TYPE,Identifier.of(EffectiveWeapons.MOD_ID, "counter_reflected_damage"));

    public static final RegistryKey<DamageType> RESONANCE_DAMAGE = RegistryKey.of(
            RegistryKeys.DAMAGE_TYPE,Identifier.of(EffectiveWeapons.MOD_ID, "resonance_damage"));

    public static final RegistryKey<DamageType> SCORCH_DAMAGE = RegistryKey.of(
            RegistryKeys.DAMAGE_TYPE,Identifier.of(EffectiveWeapons.MOD_ID, "scorch_damage"));

    public static final RegistryKey<DamageType> BLADE_BEAM_DAMAGE = RegistryKey.of(
            RegistryKeys.DAMAGE_TYPE,Identifier.of(EffectiveWeapons.MOD_ID, "blade_beam_damage"));

    public static DamageSource of(World world, RegistryKey<DamageType> key) {
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key));
    }

    public static void registerDamageSources(){
        EffectiveWeapons.LOGGER.info("Registering Damage Sources for " + EffectiveWeapons.MOD_ID);
    }
}

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

    public static DamageSource of(World world, RegistryKey<DamageType> key) {
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key));
    }

    public static void registerDamageSources(){
        EffectiveWeapons.LOGGER.info("Registering Damage Sources for " + EffectiveWeapons.MOD_ID);
    }
}

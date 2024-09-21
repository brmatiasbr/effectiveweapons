package net.br_matias_br.effectiveweapons.client.particle;

import net.br_matias_br.effectiveweapons.EffectiveWeapons;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class EffectiveWeaponsParticles {
    public static final SimpleParticleType DOUBLE_BOW_CRIT = FabricParticleTypes.simple();
    public static final SimpleParticleType FIRE_GUARD_EFFECT = FabricParticleTypes.simple();
    public static final SimpleParticleType ELEVATED_EFFECT = FabricParticleTypes.simple();
    public static final SimpleParticleType COUNTER_EFFECT = FabricParticleTypes.simple();
    public static final SimpleParticleType REMOTE_COUNTER_EFFECT = FabricParticleTypes.simple();
    public static final SimpleParticleType ISOLATED_EFFECT = FabricParticleTypes.simple();
    public static void registerParticles(){
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(EffectiveWeapons.MOD_ID, "double_bow_crit"), DOUBLE_BOW_CRIT);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(EffectiveWeapons.MOD_ID, "fire_guard_effect"), FIRE_GUARD_EFFECT);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(EffectiveWeapons.MOD_ID, "elevated_effect"), ELEVATED_EFFECT);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(EffectiveWeapons.MOD_ID, "counter_effect"), COUNTER_EFFECT);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(EffectiveWeapons.MOD_ID, "remote_counter_effect"), REMOTE_COUNTER_EFFECT);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(EffectiveWeapons.MOD_ID, "isolated_effect"), ISOLATED_EFFECT);
    }
}

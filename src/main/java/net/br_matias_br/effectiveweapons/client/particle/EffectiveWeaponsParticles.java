package net.br_matias_br.effectiveweapons.client.particle;

import net.br_matias_br.effectiveweapons.EffectiveWeapons;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class EffectiveWeaponsParticles {
    public static final SimpleParticleType DOUBLE_BOW_CRIT = FabricParticleTypes.simple();
    public static void registerParticles(){
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(EffectiveWeapons.MOD_ID, "double_bow_crit"), DOUBLE_BOW_CRIT);
    }
}

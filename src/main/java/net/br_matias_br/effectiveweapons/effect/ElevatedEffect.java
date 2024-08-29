package net.br_matias_br.effectiveweapons.effect;

import net.br_matias_br.effectiveweapons.client.particle.EffectiveWeaponsParticles;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class ElevatedEffect extends StatusEffect {
    protected ElevatedEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    public ElevatedEffect(){
        super(StatusEffectCategory.BENEFICIAL, 0x94BDFF, EffectiveWeaponsParticles.ELEVATED_EFFECT);
    }
}

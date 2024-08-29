package net.br_matias_br.effectiveweapons.effect;

import net.br_matias_br.effectiveweapons.client.particle.EffectiveWeaponsParticles;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class CounterEffect extends StatusEffect {
    protected CounterEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    public CounterEffect(){
        super(StatusEffectCategory.BENEFICIAL, 0x885051, EffectiveWeaponsParticles.COUNTER_EFFECT);
    }
}

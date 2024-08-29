package net.br_matias_br.effectiveweapons.effect;

import net.br_matias_br.effectiveweapons.client.particle.EffectiveWeaponsParticles;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class RemoteCounterEffect extends StatusEffect {
    protected RemoteCounterEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    public RemoteCounterEffect(){
        super(StatusEffectCategory.BENEFICIAL, 0x3E485F, EffectiveWeaponsParticles.REMOTE_COUNTER_EFFECT);
    }
}

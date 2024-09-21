package net.br_matias_br.effectiveweapons.effect;

import net.br_matias_br.effectiveweapons.client.particle.EffectiveWeaponsParticles;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class IsolatedEffect extends StatusEffect {
    protected IsolatedEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }
    public IsolatedEffect(){
        super(StatusEffectCategory.NEUTRAL, 0x8C1414, EffectiveWeaponsParticles.ISOLATED_EFFECT);
    }
}

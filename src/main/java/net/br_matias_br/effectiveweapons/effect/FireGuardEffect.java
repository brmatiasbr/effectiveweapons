package net.br_matias_br.effectiveweapons.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class FireGuardEffect extends StatusEffect {
    protected FireGuardEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    public FireGuardEffect(){
        super(StatusEffectCategory.BENEFICIAL, 0xCB5112);
    }
}

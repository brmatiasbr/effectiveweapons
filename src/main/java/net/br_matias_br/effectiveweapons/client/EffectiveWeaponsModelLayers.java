package net.br_matias_br.effectiveweapons.client;

import net.br_matias_br.effectiveweapons.EffectiveWeapons;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class EffectiveWeaponsModelLayers {
    public static final EntityModelLayer LAPIS_CIRCLET =
            new EntityModelLayer(Identifier.of(EffectiveWeapons.MOD_ID, "lapis_circlet"), "main");

    public static final EntityModelLayer BLADE_BEAM =
            new EntityModelLayer(Identifier.of(EffectiveWeapons.MOD_ID, "blade_beam"), "main");

    public static final EntityModelLayer AREA_NO_EFFECT_CLOUD =
            new EntityModelLayer(Identifier.of(EffectiveWeapons.MOD_ID, "area_no_effect_cloud"), "main");
}

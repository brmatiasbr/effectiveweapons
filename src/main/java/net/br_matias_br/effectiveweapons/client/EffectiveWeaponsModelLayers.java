package net.br_matias_br.effectiveweapons.client;

import net.br_matias_br.effectiveweapons.EffectiveWeapons;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

import java.util.Map;

public class EffectiveWeaponsModelLayers {
    public static final EntityModelLayer LAPIS_CIRCLET =
            new EntityModelLayer(Identifier.of(EffectiveWeapons.MOD_ID, "lapis_circlet"), "main");
}

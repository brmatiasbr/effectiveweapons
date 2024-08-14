package net.br_matias_br.effectiveweapons.entity.client;

import net.br_matias_br.effectiveweapons.EffectiveWeapons;
import net.br_matias_br.effectiveweapons.entity.custom.FixedDamageArrowEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;

public class FixedDamageArrowRenderer extends ProjectileEntityRenderer<FixedDamageArrowEntity> {
    public static final Identifier TEXTURE = Identifier.of(EffectiveWeapons.MOD_ID, "textures/entity/projectiles/arrow.png");
    public static final Identifier CRIT_TEXTURE = Identifier.of(EffectiveWeapons.MOD_ID, "textures/entity/projectiles/arrow_crit.png");
    public FixedDamageArrowRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public Identifier getTexture(FixedDamageArrowEntity entity) {
        return entity.isCritical() ? CRIT_TEXTURE : TEXTURE;
    }
}

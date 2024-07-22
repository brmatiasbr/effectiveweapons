package net.br_matias_br.effectiveweapons.entity.client;

import net.br_matias_br.effectiveweapons.entity.custom.FixedDamageArrowEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;

public class FixedDamageArrowRenderer extends ProjectileEntityRenderer<FixedDamageArrowEntity> {
    public static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/projectiles/arrow.png");
    public FixedDamageArrowRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public Identifier getTexture(FixedDamageArrowEntity entity) {
        return TEXTURE;
    }
}

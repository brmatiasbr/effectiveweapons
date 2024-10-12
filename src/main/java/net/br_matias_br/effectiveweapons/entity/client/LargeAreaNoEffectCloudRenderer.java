package net.br_matias_br.effectiveweapons.entity.client;

import net.br_matias_br.effectiveweapons.entity.custom.LargeAreaNoEffectCloudEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

public class LargeAreaNoEffectCloudRenderer extends EntityRenderer<LargeAreaNoEffectCloudEntity> {
    public LargeAreaNoEffectCloudRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public Identifier getTexture(LargeAreaNoEffectCloudEntity entity) {
        return null;
    }

    @Override
    public void render(LargeAreaNoEffectCloudEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }
}

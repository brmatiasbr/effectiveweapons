package net.br_matias_br.effectiveweapons.entity.client;

import net.br_matias_br.effectiveweapons.entity.custom.AreaNoEffectCloudEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

public class AreaNoEffectCloudRenderer extends EntityRenderer<AreaNoEffectCloudEntity> {
    public AreaNoEffectCloudRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public Identifier getTexture(AreaNoEffectCloudEntity entity) {
        return null;
    }

    @Override
    public void render(AreaNoEffectCloudEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        Random random = entity.getWorld().getRandom();
        for(int i = 0; i < 3; i++){
            double randomX = random.nextDouble() * 2.5 * (random.nextBoolean() ? 1 : -1);
            double randomZ = random.nextDouble() * 2.5 * (random.nextBoolean() ? 1 : -1);

            entity.getWorld().addParticle(ParticleTypes.EFFECT, entity.getX() + randomX, entity.getY(), entity.getZ() + randomZ, 0, 0.01, 0);
        }
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }
}

package net.br_matias_br.effectiveweapons.entity.client;

import net.br_matias_br.effectiveweapons.EffectiveWeapons;
import net.br_matias_br.effectiveweapons.client.EffectiveWeaponsModelLayers;
import net.br_matias_br.effectiveweapons.entity.custom.AreaNoEffectCloudEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

public class AreaNoEffectCloudRenderer extends EntityRenderer<AreaNoEffectCloudEntity> {
    protected AreaNoEffectCloudEntityModel model;
    protected Random random;
    public AreaNoEffectCloudRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        ModelPart root = ctx.getPart(EffectiveWeaponsModelLayers.AREA_NO_EFFECT_CLOUD);
        model = new AreaNoEffectCloudEntityModel(root);
    }

    @Override
    public Identifier getTexture(AreaNoEffectCloudEntity entity) {
        boolean frigid = entity.isFrigid();
        if(entity.age < 14){
            return Identifier.of(EffectiveWeapons.MOD_ID, "textures/models/area_no_effect_cloud/area_no_effect_cloud" + (frigid ? "_frigid" : "") + (entity.age/2) + ".png");
        }
        else{
            return Identifier.of(EffectiveWeapons.MOD_ID, "textures/models/area_no_effect_cloud/area_no_effect_cloud" + (frigid ? "_frigid" : "") + (7 + (((entity.age)/2 - 3) % 4)) + ".png");
        }
    }

    @Override
    public void render(AreaNoEffectCloudEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if(random == null){
            random = entity.getWorld().getRandom();
        }
        matrices.translate(0, -1.25, 0);
        model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentCull(getTexture(entity))), light, OverlayTexture.DEFAULT_UV);
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        matrices.translate(0, 1.25, 0);
    }
}

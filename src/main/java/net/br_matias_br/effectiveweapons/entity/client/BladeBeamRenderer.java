package net.br_matias_br.effectiveweapons.entity.client;

import net.br_matias_br.effectiveweapons.EffectiveWeapons;
import net.br_matias_br.effectiveweapons.client.EffectiveWeaponsModelLayers;
import net.br_matias_br.effectiveweapons.entity.custom.BladeBeamEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;

public class BladeBeamRenderer extends EntityRenderer<BladeBeamEntity> {
    private BladeBeamModel model = null;

    public BladeBeamRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        ModelPart root = ctx.getPart(EffectiveWeaponsModelLayers.BLADE_BEAM);
        model = new BladeBeamModel(root);
    }

    @Override
    public Identifier getTexture(BladeBeamEntity entity) {
        if(entity.getOrange()){
            return Identifier.of(EffectiveWeapons.MOD_ID, "textures/models/blade_beam_o.png");
        }
        return Identifier.of(EffectiveWeapons.MOD_ID, "textures/models/blade_beam_b.png");
    }

    @Override
    public void render(BladeBeamEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.multiply(new Quaternionf().rotateY((float) (Math.toRadians(entity.model_rotation))));
        matrices.translate(0, -1.25, 0);
        model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(getTexture(entity))), 255, OverlayTexture.DEFAULT_UV);
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        matrices.multiply(new Quaternionf().rotateY((float) -(Math.toRadians(entity.model_rotation))));
        matrices.translate(0, 1.25, 0);
    }
}

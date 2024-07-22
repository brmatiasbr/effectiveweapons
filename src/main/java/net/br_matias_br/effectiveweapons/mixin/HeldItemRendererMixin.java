package net.br_matias_br.effectiveweapons.mixin;

import net.br_matias_br.effectiveweapons.item.EffectiveWeaponsItems;
import net.br_matias_br.effectiveweapons.item.render.LapisCircletRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
    private LapisCircletRenderer lapisCircletRenderer;

    @Inject(method = "renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "HEAD", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V"))
    public void renderCirclet(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci){
        if(item.isOf(EffectiveWeaponsItems.LAPIS_CIRCLET)){
            if(lapisCircletRenderer == null){
                lapisCircletRenderer = new LapisCircletRenderer();
            }
            Arm arm = hand == Hand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();

            float f = arm == Arm.RIGHT ? 1.0F : -1.0F;
            if (!player.isInvisible()) {
                matrices.push();
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f * 10.0F));
                ((HeldItemRendererAccessor)this).callRenderArm(matrices, vertexConsumers, light, equipProgress, swingProgress, arm);
                PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer) ((HeldItemRendererAccessor)(this)).effweap$getEntityRenderDispatcher().<AbstractClientPlayerEntity>getRenderer(player);
                boolean hasThinArms = ((PlayerEntityModelAccessor) (playerEntityRenderer.getModel())).effweap$getThinArms();
                if(arm != Arm.LEFT){
                    lapisCircletRenderer.renderRightArm(matrices, vertexConsumers, light, player, hasThinArms);
                }
                else {
                    lapisCircletRenderer.renderLeftArm(matrices, vertexConsumers, light, player, hasThinArms);
                }
                matrices.pop();
            }

            matrices.push();
            matrices.pop();
        }
    }
}

package net.br_matias_br.effectiveweapons.item.render;

import net.br_matias_br.effectiveweapons.EffectiveWeapons;
import net.br_matias_br.effectiveweapons.client.EffectiveWeaponsModelLayers;
import net.br_matias_br.effectiveweapons.item.model.LapisCircletModel;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;

public class LapisCircletRenderer implements ArmorRenderer {

    private static final Identifier TEXTURE = Identifier.of(EffectiveWeapons.MOD_ID, "textures/models/lapis_circlet.png");
    private static LapisCircletModel lapisCircletModel;

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, ItemStack stack, LivingEntity entity, EquipmentSlot slot, int light, BipedEntityModel<LivingEntity> contextModel) {
        if(lapisCircletModel == null){
            lapisCircletModel = new LapisCircletModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(EffectiveWeaponsModelLayers.LAPIS_CIRCLET));
        }
        lapisCircletModel.setVisible(false);
        lapisCircletModel.head.copyTransform(contextModel.head);
        lapisCircletModel.head.visible = slot == EquipmentSlot.HEAD;
        lapisCircletModel.rightLeg.copyTransform(contextModel.rightLeg);
        lapisCircletModel.rightLeg.visible = slot == EquipmentSlot.FEET;
        lapisCircletModel.leftLeg.visible = slot == EquipmentSlot.FEET;

        ArmorRenderer.renderPart(matrices, vertexConsumers, light, stack, lapisCircletModel, TEXTURE);
    }

    public void renderRightArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, boolean thinArm) {
        if (lapisCircletModel == null) {
            lapisCircletModel = new LapisCircletModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(EffectiveWeaponsModelLayers.LAPIS_CIRCLET));
        }
        if(thinArm){
            matrices.translate(0.063, 0 ,0);
        }
        this.renderArm(matrices, vertexConsumers, light, player, lapisCircletModel.rightArm, thinArm);
        if(thinArm){
            matrices.translate(-0.063, 0 ,0);
        }
    }

    public void renderLeftArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, boolean thinArm) {
        if (lapisCircletModel == null) {
            lapisCircletModel = new LapisCircletModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(EffectiveWeaponsModelLayers.LAPIS_CIRCLET));
        }
        if(thinArm){
            matrices.translate(-0.063, 0 ,0);
        }
        this.renderArm(matrices, vertexConsumers, light, player, lapisCircletModel.leftArm, thinArm);
        if(thinArm){
            matrices.translate(0.063, 0 ,0);
        }
    }

    private void renderArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, boolean thinArm) {
        this.setModelPose(player);
        lapisCircletModel.handSwingProgress = 0.0f;
        lapisCircletModel.sneaking = false;
        lapisCircletModel.leaningPitch = 0.0f;
        lapisCircletModel.setAngles(player, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        arm.pitch = 0.0f;
        arm.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(TEXTURE)), light, OverlayTexture.DEFAULT_UV);
    }

    private void setModelPose(AbstractClientPlayerEntity player) {
        if(lapisCircletModel == null){
            lapisCircletModel = new LapisCircletModel(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(EffectiveWeaponsModelLayers.LAPIS_CIRCLET));
        }
        if (player.isSpectator()) {
            lapisCircletModel.setVisible(false);
            lapisCircletModel.head.visible = true;
            lapisCircletModel.hat.visible = true;
        } else {
            lapisCircletModel.setVisible(true);
            lapisCircletModel.hat.visible = player.isPartVisible(PlayerModelPart.HAT);
            lapisCircletModel.sneaking = player.isInSneakingPose();
            BipedEntityModel.ArmPose armPose = getArmPose(player, Hand.MAIN_HAND);
            BipedEntityModel.ArmPose armPose2 = getArmPose(player, Hand.OFF_HAND);
            if (armPose.isTwoHanded()) {
                BipedEntityModel.ArmPose armPose3 = armPose2 = player.getOffHandStack().isEmpty() ? BipedEntityModel.ArmPose.EMPTY : BipedEntityModel.ArmPose.ITEM;
            }
            if (player.getMainArm() == Arm.RIGHT) {
                lapisCircletModel.rightArmPose = armPose;
                lapisCircletModel.leftArmPose = armPose2;
            } else {
                lapisCircletModel.rightArmPose = armPose2;
                lapisCircletModel.leftArmPose = armPose;
            }
        }
    }

    private static BipedEntityModel.ArmPose getArmPose(AbstractClientPlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isEmpty()) {
            return BipedEntityModel.ArmPose.EMPTY;
        }
        if (player.getActiveHand() == hand && player.getItemUseTimeLeft() > 0) {
            UseAction useAction = itemStack.getUseAction();
            if (useAction == UseAction.BLOCK) {
                return BipedEntityModel.ArmPose.BLOCK;
            }
            if (useAction == UseAction.BOW) {
                return BipedEntityModel.ArmPose.BOW_AND_ARROW;
            }
            if (useAction == UseAction.SPEAR) {
                return BipedEntityModel.ArmPose.THROW_SPEAR;
            }
            if (useAction == UseAction.CROSSBOW && hand == player.getActiveHand()) {
                return BipedEntityModel.ArmPose.CROSSBOW_CHARGE;
            }
            if (useAction == UseAction.SPYGLASS) {
                return BipedEntityModel.ArmPose.SPYGLASS;
            }
            if (useAction == UseAction.TOOT_HORN) {
                return BipedEntityModel.ArmPose.TOOT_HORN;
            }
            if (useAction == UseAction.BRUSH) {
                return BipedEntityModel.ArmPose.BRUSH;
            }
        } else if (!player.handSwinging && itemStack.isOf(Items.CROSSBOW) && CrossbowItem.isCharged(itemStack)) {
            return BipedEntityModel.ArmPose.CROSSBOW_HOLD;
        }
        return BipedEntityModel.ArmPose.ITEM;
    }
}

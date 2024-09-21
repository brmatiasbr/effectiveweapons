package net.br_matias_br.effectiveweapons.mixin;

import net.br_matias_br.effectiveweapons.item.EffectiveWeaponsItems;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedEntityModel.class)
public class BipedEntityModelMixin<T extends LivingEntity> {
    @Shadow public BipedEntityModel.ArmPose rightArmPose;

    @Shadow @Final public ModelPart rightArm;

    @Shadow @Final public ModelPart leftArm;

    @Shadow public BipedEntityModel.ArmPose leftArmPose;

    @Inject(method = "positionRightArm(Lnet/minecraft/entity/LivingEntity;)V", at = @At(value = "HEAD"))
    public void stabilizeBlockingArmRight(T entity, CallbackInfo ci){
        if(entity instanceof PlayerEntity player && (this.rightArmPose == BipedEntityModel.ArmPose.BLOCK || this.rightArmPose == BipedEntityModel.ArmPose.THROW_SPEAR)){
            ItemStack stack = player.getStackInHand(player.getMainArm() == Arm.RIGHT ? Hand.MAIN_HAND : Hand.OFF_HAND);
            if(stack.isOf(EffectiveWeaponsItems.CLOSE_SHIELD) || stack.isOf(EffectiveWeaponsItems.DISTANT_SHIELD)){
                this.rightArm.pitch = 0;
            }
            else if(stack.isOf(EffectiveWeaponsItems.BLESSED_LANCE)){
                this.rightArm.pitch = 0;
            }
        }
    }

    @Inject(method = "positionLeftArm(Lnet/minecraft/entity/LivingEntity;)V", at = @At(value = "HEAD"))
    public void stabilizeBlockingArmLeft(T entity, CallbackInfo ci){
        if(entity instanceof PlayerEntity player && (this.leftArmPose == BipedEntityModel.ArmPose.BLOCK || this.leftArmPose == BipedEntityModel.ArmPose.THROW_SPEAR)){
            ItemStack stack = player.getStackInHand(player.getMainArm() == Arm.LEFT ? Hand.MAIN_HAND : Hand.OFF_HAND);
            if(stack.isOf(EffectiveWeaponsItems.CLOSE_SHIELD) || stack.isOf(EffectiveWeaponsItems.DISTANT_SHIELD)){
                this.leftArm.pitch = 0;
            }
            else if(stack.isOf(EffectiveWeaponsItems.BLESSED_LANCE)){
                this.leftArm.pitch = 0;
            }
        }
    }
}

package net.br_matias_br.effectiveweapons.mixin;

import net.br_matias_br.effectiveweapons.item.custom.LightShieldItem;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends LivingEntity{
    protected ClientPlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow public abstract boolean isUsingItem();
    @Shadow public Input input;
    @Shadow private @Nullable Hand activeHand;

    @Inject(method = "tickMovement()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/tutorial/TutorialManager;onMovement(Lnet/minecraft/client/input/Input;)V"))
    public void removeShieldMovementPenalty(CallbackInfo ci){
        if (this.isUsingItem() && this.getStackInHand(this.activeHand).getItem() instanceof LightShieldItem){
            this.input.movementSideways *= 5f;
            this.input.movementForward *= 5f;
        }
    }
}


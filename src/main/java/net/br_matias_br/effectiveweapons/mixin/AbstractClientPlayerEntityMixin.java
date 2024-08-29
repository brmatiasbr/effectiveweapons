package net.br_matias_br.effectiveweapons.mixin;

import com.mojang.authlib.GameProfile;
import net.br_matias_br.effectiveweapons.EffectiveWeapons;
import net.br_matias_br.effectiveweapons.item.EffectiveWeaponsItems;
import net.br_matias_br.effectiveweapons.item.custom.DoubleBowItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public class AbstractClientPlayerEntityMixin extends PlayerEntity {
    public AbstractClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "getFovMultiplier()F", at = @At(value = "HEAD"), cancellable = true)
    public void applyZoom(CallbackInfoReturnable<Float> cir){
        if(this.isUsingItem() && this.getActiveItem().isOf(EffectiveWeaponsItems.DOUBLE_BOW) && MinecraftClient.getInstance().options.getPerspective().isFirstPerson()){
            DoubleBowItem doubleBowItem = (DoubleBowItem) this.getActiveItem().getItem();
            NbtCompound compound = doubleBowItem.getCompoundOrDefault(this.getActiveItem());
            if(compound.getString(EffectiveWeapons.PASSIVE_ABILITY).equals(DoubleBowItem.PASSIVE_DEADEYE)){
                cir.setReturnValue(0.1f);
            }
        }
        return;
    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public boolean isCreative() {
        return false;
    }
}

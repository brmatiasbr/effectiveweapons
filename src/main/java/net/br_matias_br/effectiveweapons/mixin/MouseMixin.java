package net.br_matias_br.effectiveweapons.mixin;

import net.br_matias_br.effectiveweapons.EffectiveWeapons;
import net.br_matias_br.effectiveweapons.item.EffectiveWeaponsItems;
import net.br_matias_br.effectiveweapons.item.custom.DoubleBowItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Smoother;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Mouse.class)
public class MouseMixin {
    @Shadow @Final private MinecraftClient client;

    @Shadow @Final private Smoother cursorXSmoother;

    @Shadow @Final private Smoother cursorYSmoother;

    @Shadow private double cursorDeltaX;

    @Shadow private double cursorDeltaY;

    @ModifyArgs(method = "updateMouse(D)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/tutorial/TutorialManager;onUpdateMouse(DD)V"))
    public void applySmoothMouse(Args args){
        ClientPlayerEntity player = this.client.player;
        if(player != null && player.isUsingItem() && this.client.options.getPerspective().isFirstPerson()){
            if(player.getActiveItem().isOf(EffectiveWeaponsItems.DOUBLE_BOW)){
                DoubleBowItem doubleBowItem = (DoubleBowItem) player.getActiveItem().getItem();
                NbtCompound compound = doubleBowItem.getCompoundOrDefault(player.getActiveItem());
                if(compound.getString(EffectiveWeapons.PASSIVE_ABILITY).equals(DoubleBowItem.PASSIVE_DEADEYE)){
                    double d = this.client.options.getMouseSensitivity().getValue() * 0.6F + 0.2F;
                    double e = d * d * d;
                    this.cursorXSmoother.clear();
                    this.cursorYSmoother.clear();
                    args.set(0, this.cursorDeltaX * e);
                    args.set(1, this.cursorDeltaY * e);
                }
            }
        }
    }

    @ModifyArgs(method = "updateMouse(D)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"))
    public void applySmoothMouseRemaining(Args args){
        ClientPlayerEntity player = this.client.player;
        if(player != null && player.isUsingItem() && this.client.options.getPerspective().isFirstPerson()){
            if(player.getActiveItem().isOf(EffectiveWeaponsItems.DOUBLE_BOW)){
                DoubleBowItem doubleBowItem = (DoubleBowItem) player.getActiveItem().getItem();
                NbtCompound compound = doubleBowItem.getCompoundOrDefault(player.getActiveItem());
                if(compound.getString(EffectiveWeapons.PASSIVE_ABILITY).equals(DoubleBowItem.PASSIVE_DEADEYE)){
                    double d = this.client.options.getMouseSensitivity().getValue() * 0.6F + 0.2F;
                    double e = d * d * d;
                    this.cursorXSmoother.clear();
                    this.cursorYSmoother.clear();
                    args.set(0, this.cursorDeltaX * e);
                    args.set(1, this.cursorDeltaY * e);
                }
            }
        }
    }
}

package net.br_matias_br.effectiveweapons.mixin;

import net.br_matias_br.effectiveweapons.EffectiveWeapons;
import net.br_matias_br.effectiveweapons.item.EffectiveWeaponsItems;
import net.br_matias_br.effectiveweapons.item.custom.AttunableItem;
import net.br_matias_br.effectiveweapons.item.custom.DekajaTomeItem;
import net.br_matias_br.effectiveweapons.item.custom.RogueDaggerItem;
import net.br_matias_br.effectiveweapons.networking.SwingActionPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow @Nullable public ClientPlayerEntity player;

    @Shadow @Nullable public HitResult crosshairTarget;

    @Shadow @Final private static Logger LOGGER;

    @Shadow @Nullable public ClientPlayerInteractionManager interactionManager;

    @Shadow public int attackCooldown;

    @Shadow @Nullable public ClientWorld world;

    @Shadow @Final public GameOptions options;

    @Shadow protected abstract void doItemUse();

    @Inject(method = "handleInputEvents()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;doAttack()Z"))
    public void swingAction(CallbackInfo info){
        if (this.player != null && this.attackCooldown <= 0 && this.crosshairTarget != null && !this.player.isRiding()) {
            ItemStack itemStack = this.player.getStackInHand(Hand.MAIN_HAND);
            if (itemStack.isItemEnabled(this.world.getEnabledFeatures()) && itemStack.getItem() instanceof AttunableItem attunableItem) {
                NbtCompound compound = attunableItem.getCompoundOrDefault(itemStack);
                int charge = compound.getInt(attunableItem.getItemChargeId());
                String meterAbility = compound.getString(EffectiveWeapons.METER_ABILITY);

                if(itemStack.isOf(EffectiveWeaponsItems.ROGUE_DAGGER) && this.crosshairTarget.getType() != HitResult.Type.ENTITY){
                    if(meterAbility.equals(RogueDaggerItem.METER_BLADE_BEAM) && charge >= RogueDaggerItem.MAX_CHARGE/5){
                        ClientPlayNetworking.send(new SwingActionPayload(this.player.getId(), 1));
                    }
                }
                else if(itemStack.isOf(EffectiveWeaponsItems.DEKAJA_TOME)){
                    if(meterAbility.equals(DekajaTomeItem.METER_REPULSION) && charge >= DekajaTomeItem.MAX_CHARGE_REPULSION/2){
                        ClientPlayNetworking.send(new SwingActionPayload(this.player.getId(), 2));
                    }
                }
            }
        }
    }
}

package net.br_matias_br.effectiveweapons.mixin;

import net.br_matias_br.effectiveweapons.EffectiveWeapons;
import net.br_matias_br.effectiveweapons.item.EffectiveWeaponsItems;
import net.br_matias_br.effectiveweapons.item.custom.LightShieldItem;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow protected ItemStack activeItemStack;

    @Shadow public abstract boolean isUsingItem();

    @Shadow public abstract boolean hasStatusEffect(RegistryEntry<StatusEffect> effect);

    @Inject(method = "blockedByShield(Lnet/minecraft/entity/damage/DamageSource;)Z", at = @At(value = "HEAD"), cancellable = true)
    public void cancelIfHoldingSpecialShield(DamageSource source, CallbackInfoReturnable<Boolean> cir){
        if (this.isUsingItem() && !this.activeItemStack.isEmpty()) {
            boolean newValue = false;
            if(this.activeItemStack.isOf(EffectiveWeaponsItems.CLOSE_SHIELD)){
                newValue = (!(source.getSource() instanceof LivingEntity) || source.isIn(DamageTypeTags.IS_PROJECTILE));
            }
            else if(this.activeItemStack.isOf(EffectiveWeaponsItems.DISTANT_SHIELD)){
                newValue =  (!(source.getSource() instanceof ProjectileEntity) || !source.isIn(DamageTypeTags.IS_PROJECTILE));
            }
            if(!newValue){
                if(this.activeItemStack.getItem() instanceof LightShieldItem lightShieldItem){
                    if(lightShieldItem.hasMeterAbility(this.activeItemStack)){
                        NbtCompound compound = lightShieldItem.getCompoundOrDefault(this.activeItemStack);
                        int currentCharge = compound.getInt(LightShieldItem.CURRENT_CHARGE);
                        if(currentCharge < 20) {
                            compound.putInt(LightShieldItem.CURRENT_CHARGE, currentCharge + 1);
                            this.activeItemStack.setDamage(1001 - (50 * (currentCharge + 1)));
                            this.activeItemStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(compound));
                        }
                        else{
                            if(compound.getString(EffectiveWeapons.METER_ABILITY).equals(LightShieldItem.METER_STAGGER)){
                                if(source.getSource() != null){
                                    Entity attacker;
                                    if(source.getSource() instanceof ProjectileEntity projectileEntity){
                                        attacker = projectileEntity.getOwner();
                                    }
                                    else attacker = source.getSource();
                                    if(attacker instanceof LivingEntity livingEntity){
                                        if(!this.getWorld().isClient()){
                                            this.getWorld().playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), SoundEvents.ENTITY_GENERIC_HURT, SoundCategory.PLAYERS);
                                        }
                                        compound.putInt(LightShieldItem.CURRENT_CHARGE, 0);
                                        this.activeItemStack.setDamage(1001);
                                        this.activeItemStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(compound));
                                        StatusEffectInstance targetWeakness = livingEntity.getStatusEffect(StatusEffects.WEAKNESS);
                                        StatusEffectInstance targetFatigue = livingEntity.getStatusEffect(StatusEffects.MINING_FATIGUE);

                                        if(targetWeakness == null || (!(targetWeakness.getDuration() > 400) && !(targetWeakness.getAmplifier() > 0))) {
                                            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 400), this);
                                        }
                                        if(targetFatigue == null || (!(targetFatigue.getDuration() > 400) && !(targetFatigue.getAmplifier() > 0))) {
                                            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 400), this);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            cir.setReturnValue(!newValue);
        }

        return;
    }

    @ModifyVariable(method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", at = @At(value = "HEAD"), argsOnly = true)
    public float applyFireGuardDamageReduction(float amount, DamageSource source){
        if(this.hasStatusEffect(EffectiveWeapons.FIRE_GUARD_REGISTRY_ENTRY) && source.isIn(DamageTypeTags.IS_FIRE)){
            System.out.println(amount * 0.6f);
            return amount * 0.6f;
        }
            return amount;
    }
}

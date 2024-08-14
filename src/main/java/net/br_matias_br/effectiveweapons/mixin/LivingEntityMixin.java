package net.br_matias_br.effectiveweapons.mixin;

import net.br_matias_br.effectiveweapons.EffectiveWeapons;
import net.br_matias_br.effectiveweapons.entity.EffectiveWeaponsDamageSources;
import net.br_matias_br.effectiveweapons.item.EffectiveWeaponsItems;
import net.br_matias_br.effectiveweapons.item.custom.AttunableItem;
import net.br_matias_br.effectiveweapons.item.custom.BlessedLanceItem;
import net.br_matias_br.effectiveweapons.item.custom.LightShieldItem;
import net.br_matias_br.effectiveweapons.item.custom.SpiralingSwordItem;
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
import org.jetbrains.annotations.Nullable;
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

    @Shadow private @Nullable LivingEntity attacker;

    @Inject(method = "blockedByShield(Lnet/minecraft/entity/damage/DamageSource;)Z", at = @At(value = "HEAD"), cancellable = true)
    public void cancelIfHoldingSpecialShield(DamageSource source, CallbackInfoReturnable<Boolean> cir){
        if (this.isUsingItem() && !this.activeItemStack.isEmpty() && this.activeItemStack.getItem() instanceof LightShieldItem) {
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
    public float applyDamageModifiers(float amount, DamageSource source){
        float damageMultiplier = 1;

        if(this.hasStatusEffect(EffectiveWeapons.FIRE_GUARD_REGISTRY_ENTRY) && source.isIn(DamageTypeTags.IS_FIRE)) damageMultiplier *= 0.6f; // Fire Guard fire damage reduction

        if(this.hasStatusEffect(EffectiveWeapons.ELEVATED_REGISTRY_ENTRY) && source.isIn(DamageTypeTags.NO_KNOCKBACK)) damageMultiplier *= 0.8f; // Elevated damage reduction, ignored by elevated recoil

        Entity sourceEntity = source.getSource();
        if(sourceEntity != null){ // Checks damage source for any attacking entities, then if the entity is a projectile, recognizes its owner as the attacker
            LivingEntity attacker = null;
            if(sourceEntity instanceof ProjectileEntity projectile && projectile.getOwner() instanceof LivingEntity){
                attacker = (LivingEntity) projectile.getOwner();
            }
            else if(sourceEntity instanceof LivingEntity livingEntity){
                attacker = livingEntity;
            }
            if(attacker != null){
                if(attacker.hasStatusEffect(EffectiveWeapons.ELEVATED_REGISTRY_ENTRY)) { // Applies elevated damage multiplier and recoil damage
                    damageMultiplier *= 1.5f;
                    attacker.damage(EffectiveWeaponsDamageSources.of(this.getWorld(), EffectiveWeaponsDamageSources.ELEVATED_RECOIL_DAMAGE),
                            attacker.getMaxHealth() * 0.1f);
                    attacker.timeUntilRegen = 0;
                }

                if(attacker.getStackInHand(attacker.getActiveHand()).getItem() instanceof AttunableItem attunableItem){
                    ItemStack stack = attacker.getStackInHand(attacker.getActiveHand());
                    NbtCompound compound = attunableItem.getCompoundOrDefault(stack);
                    String passiveAbility = compound.getString(EffectiveWeapons.PASSIVE_ABILITY);
                    String meterAbility = compound.getString(EffectiveWeapons.METER_ABILITY);

                    if (stack.isOf(EffectiveWeaponsItems.SPIRALING_SWORD) && !attacker.getWorld().isClient()) { // Spiraling Sword default charge logic
                        if (!passiveAbility.equals(SpiralingSwordItem.PASSIVE_DEATH_CHARGE)) {
                            float charge = compound.getFloat(attunableItem.getItemChargeId());
                            charge += amount * damageMultiplier;
                            if (charge > SpiralingSwordItem.MAX_CHARGE) {
                                charge = 0;
                                SpiralingSwordItem.extendEffects(attacker, meterAbility.equals(SpiralingSwordItem.METER_CHAOTIC_SPIRAL));
                            }
                            stack.setDamage(1001 - (int) (charge * 5));
                            compound.putFloat(attunableItem.getItemChargeId(), charge);
                            NbtComponent component = NbtComponent.of(compound);
                            stack.set(DataComponentTypes.CUSTOM_DATA, component);
                        }
                    }

                    if (stack.isOf(EffectiveWeaponsItems.BLESSED_LANCE) && !attacker.getWorld().isClient()) {
                        if(!meterAbility.equals(BlessedLanceItem.METER_GRAVE_KEEPER) && !meterAbility.equals(EffectiveWeapons.METER_NONE)){ // Blessed Lance default charge logic
                            int charge = compound.getInt(attunableItem.getItemChargeId());
                            if(amount * damageMultiplier >= 7){
                                charge += (int)(amount * damageMultiplier);
                            }
                            if(charge >= BlessedLanceItem.MAX_CHARGE){
                                charge = 0;
                                if(meterAbility.equals(BlessedLanceItem.METER_ELEVATED)){
                                    attacker.addStatusEffect(new StatusEffectInstance(EffectiveWeapons.ELEVATED_REGISTRY_ENTRY, 600, 0, false, false, true));
                                }
                                else if(meterAbility.equals(BlessedLanceItem.METER_REFRESH)){
                                    BlessedLanceItem.neutralizeEffects(attacker, attacker.getWorld(), true);
                                    float maxHealth = attacker.getMaxHealth();
                                    attacker.heal((maxHealth * 0.15f) + (maxHealth * 0.1f * attacker.getWorld().getRandom().nextFloat()));
                                }
                            }
                            stack.setDamage(1001 - (charge * 5));
                            compound.putInt(attunableItem.getItemChargeId(), charge);
                            NbtComponent component = NbtComponent.of(compound);
                            stack.set(DataComponentTypes.CUSTOM_DATA, component);
                        }
                    }
                }
            }
        }
        return amount * damageMultiplier;
    }
}

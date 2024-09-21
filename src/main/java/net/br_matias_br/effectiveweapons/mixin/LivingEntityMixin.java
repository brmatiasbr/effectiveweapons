package net.br_matias_br.effectiveweapons.mixin;

import net.br_matias_br.effectiveweapons.EffectiveWeapons;
import net.br_matias_br.effectiveweapons.effect.EffectiveWeaponsEffects;
import net.br_matias_br.effectiveweapons.entity.EffectiveWeaponsDamageSources;
import net.br_matias_br.effectiveweapons.item.EffectiveWeaponsItems;
import net.br_matias_br.effectiveweapons.item.custom.*;
import net.br_matias_br.effectiveweapons.networking.ParticleRequestPayload;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow protected ItemStack activeItemStack;

    @Shadow public abstract boolean isUsingItem();

    @Shadow public abstract boolean hasStatusEffect(RegistryEntry<StatusEffect> effect);

    @Shadow private @Nullable LivingEntity attacker;

    @Shadow public abstract boolean addStatusEffect(StatusEffectInstance effect);

    @Shadow public abstract boolean damage(DamageSource source, float amount);

    @Shadow public abstract @Nullable StatusEffectInstance getStatusEffect(RegistryEntry<StatusEffect> effect);

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
                        if(currentCharge < LightShieldItem.MAX_CHARGE) {
                            compound.putInt(LightShieldItem.CURRENT_CHARGE, currentCharge + 1);
                            this.activeItemStack.setDamage(1001 - (50 * (currentCharge + 1)));
                            this.activeItemStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(compound));
                        }
                        else{
                            if(source.getSource() != null){

                                Entity attacker;
                                if(source.getSource() instanceof ProjectileEntity projectileEntity){
                                    attacker = projectileEntity.getOwner();
                                }
                                else attacker = source.getSource();

                                if(attacker instanceof LivingEntity livingEntity){
                                    String meterAbility = compound.getString(EffectiveWeapons.METER_ABILITY);
                                    if(meterAbility.equals(LightShieldItem.METER_STAGGER)){
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
                                    else if(meterAbility.equals(LightShieldItem.METER_REMOTE_COUNTER)){
                                        this.addStatusEffect(new StatusEffectInstance(EffectiveWeaponsEffects.REMOTE_COUNTER_REGISTRY_ENTRY, 400, 0, false, false, true));

                                        compound.putInt(LightShieldItem.CURRENT_CHARGE, 0);
                                        this.activeItemStack.setDamage(1001);
                                        this.activeItemStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(compound));
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

        if(this.hasStatusEffect(EffectiveWeaponsEffects.FIRE_GUARD_REGISTRY_ENTRY) && source.isIn(DamageTypeTags.IS_FIRE)) damageMultiplier *= 0.6f; // Fire Guard fire damage reduction

        if(this.hasStatusEffect(EffectiveWeaponsEffects.ELEVATED_REGISTRY_ENTRY) && source.isIn(DamageTypeTags.NO_KNOCKBACK)) damageMultiplier *= 0.8f; // Elevated damage reduction, ignored by elevated recoil

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
                if(attacker.hasStatusEffect(EffectiveWeaponsEffects.ELEVATED_REGISTRY_ENTRY)) { // Applies elevated damage multiplier and recoil damage
                    damageMultiplier *= 1.5f;
                    attacker.damage(EffectiveWeaponsDamageSources.of(this.getWorld(), EffectiveWeaponsDamageSources.ELEVATED_RECOIL_DAMAGE),
                            attacker.getMaxHealth() * 0.1f);
                    attacker.timeUntilRegen = 0;
                }
                if(this.hasStatusEffect(EffectiveWeaponsEffects.COUNTER_REGISTRY_ENTRY) && !source.isIn(DamageTypeTags.NO_KNOCKBACK) && !source.isIn(DamageTypeTags.IS_PROJECTILE)){
                    if((Entity)this instanceof PlayerEntity player){
                        attacker.setAttacking(player);
                    }
                    attacker.damage(EffectiveWeaponsDamageSources.of(this.getWorld(), EffectiveWeaponsDamageSources.COUNTER_REFLECTED_DAMAGE), amount * damageMultiplier);
                    damageMultiplier = 0;
                }
                if(this.hasStatusEffect(EffectiveWeaponsEffects.REMOTE_COUNTER_REGISTRY_ENTRY) && !source.isIn(DamageTypeTags.NO_KNOCKBACK) && source.isIn(DamageTypeTags.IS_PROJECTILE)){
                    if((Entity)this instanceof PlayerEntity player){
                        attacker.setAttacking(player);
                    }
                    attacker.damage(EffectiveWeaponsDamageSources.of(this.getWorld(), EffectiveWeaponsDamageSources.COUNTER_REFLECTED_DAMAGE), amount * damageMultiplier);
                    damageMultiplier = 0;
                }

                if(attacker.getStackInHand(attacker.getActiveHand()).getItem() instanceof AttunableItem attunableItem){
                    ItemStack stack = attacker.getStackInHand(attacker.getActiveHand());
                    NbtCompound compound = attunableItem.getCompoundOrDefault(stack);
                    String passiveAbility = compound.getString(EffectiveWeapons.PASSIVE_ABILITY);
                    String meterAbility = compound.getString(EffectiveWeapons.METER_ABILITY);

                    if(stack.isOf(EffectiveWeaponsItems.SPIRALING_SWORD) && !attacker.getWorld().isClient()) { // Spiraling Sword default charge logic
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

                    if(stack.isOf(EffectiveWeaponsItems.BLESSED_LANCE) && !attacker.getWorld().isClient()) {
                        if(!meterAbility.equals(BlessedLanceItem.METER_GRAVE_KEEPER) && !meterAbility.equals(EffectiveWeapons.METER_NONE)){ // Blessed Lance default charge logic
                            int charge = compound.getInt(attunableItem.getItemChargeId());
                            charge += (int)(amount * damageMultiplier);

                            if(charge >= BlessedLanceItem.MAX_CHARGE){
                                charge = 0;
                                if(meterAbility.equals(BlessedLanceItem.METER_ELEVATED)){
                                    attacker.addStatusEffect(new StatusEffectInstance(EffectiveWeaponsEffects.ELEVATED_REGISTRY_ENTRY, 600, 0, false, false, true));
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

                    else if(stack.isOf(EffectiveWeaponsItems.PACT_AXE) && !attacker.getWorld().isClient() && !source.isIn(DamageTypeTags.NO_KNOCKBACK)) {
                        Random random = attacker.getWorld().getRandom();
                        boolean backfire = random.nextInt(100) < 20;
                        if(meterAbility.equals(EffectiveWeapons.METER_NONE)) stack.setDamage(backfire ? 1 : 2);
                        boolean divideDamage = false;
                        if(backfire){
                            Collection<StatusEffectInstance> userStatusEffects = attacker.getStatusEffects();
                            if(!userStatusEffects.isEmpty()){
                                divideDamage = true;
                                LinkedList<StatusEffectInstance> statusEffectsToSacrifice = new LinkedList<>();
                                for(StatusEffectInstance statusEffect : userStatusEffects){
                                    if(statusEffect.getEffectType().value().isBeneficial()){
                                        statusEffectsToSacrifice.add(statusEffect);
                                    }
                                }
                                attacker.removeStatusEffect(statusEffectsToSacrifice.get(random.nextInt(statusEffectsToSacrifice.size())).getEffectType());
                            }
                            float recoilDamage = amount * damageMultiplier;
                            if(divideDamage) recoilDamage *= 0.5f;
                            attacker.damage(EffectiveWeaponsDamageSources.of(attacker.getWorld(),
                                    EffectiveWeaponsDamageSources.PACT_AXE_RECOIL_DAMAGE), recoilDamage);
                        }

                        if(!meterAbility.equals(EffectiveWeapons.METER_NONE)){
                            int charge = compound.getInt(attunableItem.getItemChargeId());
                            charge += (int) (amount * damageMultiplier * (backfire ? 1.5f : 1));
                            if(charge >= PactAxeItem.MAX_CHARGE) charge = PactAxeItem.MAX_CHARGE;

                            if (charge == PactAxeItem.MAX_CHARGE && !meterAbility.equals(PactAxeItem.METER_RESONANCE)) {
                                charge = 0;
                                if (meterAbility.equals(PactAxeItem.METER_COUNTER)) {
                                    attacker.addStatusEffect(new StatusEffectInstance(EffectiveWeaponsEffects.COUNTER_REGISTRY_ENTRY, 400, 0, false, true, true));
                                }
                                else if(meterAbility.equals(PactAxeItem.METER_DOMAIN_OF_FIRE)){
                                    List<Entity> nearbyEntities = this.getWorld().getOtherEntities(attacker, Box.of(this.getPos(),
                                            16, 4, 16), EntityPredicates.VALID_LIVING_ENTITY);
                                    for (ServerPlayerEntity player : PlayerLookup.around((ServerWorld) this.getWorld(),
                                            new Vec3d(this.getX(), this.getY(), this.getZ()), 128)) {
                                        ServerPlayNetworking.send(player, new ParticleRequestPayload(this.getId(), 8));
                                    }
                                    this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS);
                                    if(!nearbyEntities.isEmpty()) for(Entity entity : nearbyEntities){
                                        if(entity instanceof LivingEntity livingEntity){
                                            if(attacker instanceof PlayerEntity player){
                                                livingEntity.setAttacking(player);
                                            }
                                            livingEntity.damage(EffectiveWeaponsDamageSources.of(this.getWorld(), EffectiveWeaponsDamageSources.SCORCH_DAMAGE), 4);
                                            livingEntity.setOnFireFor(120);
                                        }
                                    }
                                    if(nearbyEntities.size() > 4){
                                        attacker.addStatusEffect(new StatusEffectInstance(EffectiveWeaponsEffects.FIRE_GUARD_REGISTRY_ENTRY, 300, 0, false, true, true));
                                    }
                                }
                            }
                            stack.setDamage(1001 - (charge * 5));
                            compound.putInt(attunableItem.getItemChargeId(), charge);
                            NbtComponent component = NbtComponent.of(compound);
                            stack.set(DataComponentTypes.CUSTOM_DATA, component);
                        }
                        if(backfire) damageMultiplier = 0;
                    }
                    else if(stack.isOf(EffectiveWeaponsItems.ROGUE_DAGGER)){
                        if(!meterAbility.equals(EffectiveWeapons.METER_NONE)){
                            int charge = compound.getInt(attunableItem.getItemChargeId());
                            charge += (int)(amount * damageMultiplier);
                            if(meterAbility.equals(RogueDaggerItem.METER_BLADE_BEAM)){
                                if(charge > RogueDaggerItem.MAX_CHARGE) charge = RogueDaggerItem.MAX_CHARGE;
                            }
                            else if(charge > RogueDaggerItem.MAX_CHARGE_PURSUIT) charge = RogueDaggerItem.MAX_CHARGE_PURSUIT;

                            stack.setDamage(1001 - (charge * (meterAbility.equals(RogueDaggerItem.METER_BLADE_BEAM) ? 10 : 25)));
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

    @Inject(method = "addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/entity/Entity;)Z", at = @At("HEAD"), cancellable = true)
    public void applyIsolatedEffect(StatusEffectInstance effect, Entity source, CallbackInfoReturnable<Boolean> cir){
        if(this.getStatusEffect(EffectiveWeaponsEffects.ISOLATED_REGISTRY_ENTRY) != null){
            cir.setReturnValue(false);
        }
    }
}

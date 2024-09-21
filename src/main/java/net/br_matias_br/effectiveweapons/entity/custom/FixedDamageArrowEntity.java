package net.br_matias_br.effectiveweapons.entity.custom;

import net.br_matias_br.effectiveweapons.EffectiveWeapons;
import net.br_matias_br.effectiveweapons.effect.EffectiveWeaponsEffects;
import net.br_matias_br.effectiveweapons.entity.EffectiveWeaponsEntities;
import net.br_matias_br.effectiveweapons.item.custom.AttunableItem;
import net.br_matias_br.effectiveweapons.item.custom.DoubleBowItem;
import net.br_matias_br.effectiveweapons.networking.ParticleRequestPayload;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FixedDamageArrowEntity extends PersistentProjectileEntity {
    private FixedDamageArrowEntity secondShot = null;
    private boolean firstShot = false;
    private boolean pickup = true;
    private int tickTimer = 5;
    protected ItemStack weaponUsed = null;

    protected double damage;
    public FixedDamageArrowEntity(EntityType<FixedDamageArrowEntity> entityType, World world) {
        super(entityType, world);
    }

    public FixedDamageArrowEntity(World world, LivingEntity owner, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(EffectiveWeaponsEntities.FIXED_DAMAGE_ARROW_ENTITY, owner, world, stack, shotFrom);
    }

    public FixedDamageArrowEntity(World world, LivingEntity owner, ItemStack stack, @Nullable ItemStack shotFrom, boolean critical) {
        this(world, owner, stack, shotFrom);
        this.setCritical(critical);
        this.weaponUsed = shotFrom;
    }

    public FixedDamageArrowEntity(World world, LivingEntity owner, ItemStack stack, @Nullable ItemStack shotFrom, boolean critical, double damage){
        this(world, owner, stack, shotFrom, critical);
        this.damage = damage;
        if(secondShot != null){
            pickup = false;
        }
    }

    public FixedDamageArrowEntity(World world, LivingEntity owner, ItemStack stack, @Nullable ItemStack shotFrom, boolean critical, double damage, FixedDamageArrowEntity secondShot){
        this(world, owner, stack, shotFrom, critical, damage);
        this.secondShot = secondShot;
        if(secondShot != null){
            pickup = false;
        }
    }

    public FixedDamageArrowEntity(World world, LivingEntity owner, ItemStack stack, @Nullable ItemStack shotFrom, boolean critical, double damage, FixedDamageArrowEntity secondShot, boolean firstShot){
        this(world, owner, stack, shotFrom, critical, damage, secondShot);
        this.firstShot = firstShot;
    }


    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity entityHit = entityHitResult.getEntity();
        double d = this.damage == 0 ? 4 : this.damage;
        if(this.isCritical()){
            d = Math.pow(d, 1.5);
        }
        Entity owner = this.getOwner();
        DamageSource damageSource = this.getDamageSources().arrow(this, (owner != null ? owner : this));

        if (owner instanceof LivingEntity livingEntity) {
            livingEntity.onAttacking(entityHit);
        }

        boolean bl = entityHit.getType() == EntityType.ENDERMAN;
        int j = entityHit.getFireTicks();
        if (this.isOnFire() && !bl) {
            entityHit.setOnFireFor(5.0F);
        }
        if(bl && this.isCritical()){
            damageSource = this.getDamageSources().magic();
        }
        double damageBeforePlayerReduction = d;
        d = this.reduceDamageForPlayer(d, entityHit, damageSource, false);

        if (entityHit.damage(damageSource, (float) d)) {
            this.checkForMeterAbility(entityHit, damageBeforePlayerReduction, damageSource);
            if(this.firstShot && this.secondShot != null) {
                entityHit.timeUntilRegen = 0;
                this.spawnSecondArrow();
            }
            if(this.isCritical() && !this.getWorld().isClient()){
                for(ServerPlayerEntity player : PlayerLookup.around((ServerWorld) this.getWorld(),
                        new Vec3d(this.getX(), this.getY(), this.getZ()), 128)){
                    ServerPlayNetworking.send(player, new ParticleRequestPayload(entityHit.getId(), 5));
                }
            }

            if (entityHit instanceof LivingEntity livingEntity2) {
                if (!this.getWorld().isClient && this.getPierceLevel() <= 0) {
                    livingEntity2.setStuckArrowCount(livingEntity2.getStuckArrowCount() + 1);
                }

                this.knockback(livingEntity2, damageSource);
                if (this.getWorld() instanceof ServerWorld serverWorld2) {
                    EnchantmentHelper.onTargetDamaged(serverWorld2, livingEntity2, damageSource, this.getWeaponStack());
                }

                this.onHit(livingEntity2);
                if (livingEntity2 != owner && livingEntity2 instanceof PlayerEntity && owner instanceof ServerPlayerEntity && !this.isSilent()) {
                    ((ServerPlayerEntity)owner)
                            .networkHandler
                            .sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.PROJECTILE_HIT_PLAYER, GameStateChangeS2CPacket.DEMO_OPEN_SCREEN));
                }
            }

            this.playSound(this.getSound(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
            if (this.getPierceLevel() <= 0) {
                this.discard();
            }
        } else {
            entityHit.setFireTicks(j);
            this.deflect(ProjectileDeflection.SIMPLE, entityHit, this.getOwner(), false);
            this.setVelocity(this.getVelocity().multiply(0.2));
            if (!this.getWorld().isClient && this.getVelocity().lengthSquared() < 1.0E-7) {
                if (this.pickupType == PersistentProjectileEntity.PickupPermission.ALLOWED) {
                    this.dropStack(this.asItemStack(), 0.1F);
                }

                this.discard();
            }
        }
    }

    protected double reduceDamageForPlayer(double damage, Entity entity, DamageSource source, boolean justTheDamage){
        if(!(entity instanceof PlayerEntity player) || entity.isInvulnerableTo(source) && !justTheDamage){
            return damage;
        }
        double originalDamage = damage;
        if (source.isIn(DamageTypeTags.IS_FREEZING) && this.getType().isIn(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES)) {
            damage *= 5.0f;
        }

        if (source.isIn(DamageTypeTags.IS_FIRE) && player.hasStatusEffect(EffectiveWeaponsEffects.FIRE_GUARD_REGISTRY_ENTRY)) {
            damage *= 0.6f;
        }

        if (!source.isIn(DamageTypeTags.NO_KNOCKBACK) && player.hasStatusEffect(EffectiveWeaponsEffects.ELEVATED_REGISTRY_ENTRY)) {
            damage *= 0.8f;
        }

        if (!source.isIn(DamageTypeTags.BYPASSES_ARMOR)) {
            damage = DamageUtil.getDamageLeft(player, (float) damage, source, (float)player.getArmor(), (float)player.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS));
        }
        damage = modifyAppliedDamage(source, (float) damage, player);

        damage = Math.max(damage - player.getAbsorptionAmount(), 0.0F);

        if(damage > player.getMaxHealth() * 0.8){
            float overkill = (float) (damage - player.getMaxHealth() * 0.8f);
            damage = player.getMaxHealth() * 0.8f;
            damage = damage + player.getAbsorptionAmount();
            damage = this.revertModifiedDamage((float) damage, player, source);

            for(int i = 0; i < 10; i++){
                if(overkill > 10){
                    player.setAir(player.getAir() - 1);
                    overkill -= 10;
                }
            }
            for(int i = 0; i < 20; i++){
                if(overkill > 100){
                    player.getHungerManager().setFoodLevel(player.getHungerManager().getFoodLevel() - 1);
                    overkill -= 100;
                }
            }
            return damage;
        }
        return originalDamage;
    }

    protected float modifyAppliedDamage(DamageSource source, float amount, PlayerEntity player) {
        if (source.isIn(DamageTypeTags.BYPASSES_EFFECTS)) {
            return amount;
        } else {
            if (player.hasStatusEffect(StatusEffects.RESISTANCE) && !source.isIn(DamageTypeTags.BYPASSES_RESISTANCE)) {
                StatusEffectInstance resistanceEffect = player.getStatusEffect(StatusEffects.RESISTANCE);
                int effectAmplifier = resistanceEffect != null ? resistanceEffect.getAmplifier() : 0;
                int i = (effectAmplifier + 1) * 5;
                int j = 25 - i;
                float f = amount * (float)j;
                amount = Math.max(f / 25.0F, 0.0F);
            }

            if (amount <= 0.0F) {
                return 0.0F;
            } else if (source.isIn(DamageTypeTags.BYPASSES_ENCHANTMENTS)) {
                return amount;
            } else {
                float k;
                if (player.getWorld() instanceof ServerWorld serverWorld) {
                    k = EnchantmentHelper.getProtectionAmount(serverWorld, player, source);
                } else {
                    k = 0.0F;
                }

                if (k > 0.0F) {
                    amount = DamageUtil.getInflictedDamage(amount, k);
                }

                return amount;
            }
        }
    }

    protected float revertModifiedDamage(float damage, PlayerEntity player, DamageSource source){
        float damageToReturn = damage;
        if (player.hasStatusEffect(StatusEffects.RESISTANCE) && !source.isIn(DamageTypeTags.BYPASSES_RESISTANCE)) {
            StatusEffectInstance resistanceEffect = player.getStatusEffect(StatusEffects.RESISTANCE);
            int effectAmplifier = resistanceEffect != null ? resistanceEffect.getAmplifier() : 0;
            int i = (effectAmplifier + 1) * 5;
            int j = 25 - i;
            float f = (float) j/25;
            damageToReturn = damageToReturn/f;
        }
        float k;
        if (player.getWorld() instanceof ServerWorld serverWorld) {
            k = EnchantmentHelper.getProtectionAmount(serverWorld, player, source);
        } else {
            k = 0.0F;
        }

        if (k > 0.0F) {
            float f = MathHelper.clamp(k, 0.0F, 20.0F);
            damageToReturn = damageToReturn / (1.0F - f / 25.0F);
        }

        damageToReturn = this.reverseArmorDamage(damageToReturn, (float)player.getArmor(), (float)player.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS));

        return damageToReturn;
    }

    protected float reverseArmorDamage(float returnValue, float armor, float armorToughness){
        float f = 2.0F + armorToughness / 4.0F;

        float sqrtF = (float) Math.sqrt(f);
        float result = (sqrtF * ((-25 * sqrtF) + (armor * sqrtF) + (float) Math.sqrt((625 * f) - (50 * armor * f) + (Math.pow(armor, 2) * f) + (100 * returnValue))))/2;
        if(armor == 0 && armorToughness == 0) result = returnValue;
        return result;
    }

    protected void checkForMeterAbility(Entity entityHit, double damage, DamageSource source){
        ItemStack weapon = this.weaponUsed;
        Entity owner = this.getOwner();
        double damagePlayer = reduceDamageForPlayer(damage, this, source, true);

        if(weapon != null && !weapon.isEmpty()){
            if(weapon.getItem() instanceof AttunableItem attunableItem){
                NbtCompound compound = attunableItem.getCompoundOrDefault(weapon);
                String meterAbility = compound.getString(EffectiveWeapons.METER_ABILITY);
                int charge = compound.getInt(attunableItem.getItemChargeId());
                if(meterAbility.equals(DoubleBowItem.METER_BURST_IMPACT)){
                    if(charge >= DoubleBowItem.MAX_CHARGE){
                        for (Entity targetEntity : this.getWorld().getOtherEntities(this,
                                new Box(entityHit.getX() - 2.5, entityHit.getY() - 2.5, entityHit.getZ() - 2.5,
                                        entityHit.getX() + 2.5, entityHit.getY() + 2.5, entityHit.getZ() + 2.5))) {
                            if (targetEntity instanceof LivingEntity livingEntity && livingEntity != entityHit && livingEntity != this.getOwner()) {
                                double explosionDamage = (livingEntity instanceof PlayerEntity) ? damagePlayer : damage;
                                livingEntity.damage(this.getDamageSources().explosion(this, (owner != null ? owner : this)), (float) explosionDamage / 2);
                            }
                        }
                        if(!this.getWorld().isClient()){
                            for(ServerPlayerEntity player : PlayerLookup.around((ServerWorld) this.getWorld(),
                                    new Vec3d(this.getX(), this.getY(), this.getZ()), 128)){
                                ServerPlayNetworking.send(player, new ParticleRequestPayload(entityHit.getId(), 7));
                            }
                        }
                        charge = 0;
                    }

                    else{
                        charge += (int) ((entityHit instanceof PlayerEntity) ? damagePlayer : damage);
                        if(charge > DoubleBowItem.MAX_CHARGE) charge = DoubleBowItem.MAX_CHARGE;
                    }

                    compound.putInt(attunableItem.getItemChargeId(), charge);
                    weapon.setDamage(1001 - (5 * charge));
                    weapon.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(compound));
                }
                else if(meterAbility.equals(DoubleBowItem.METER_VAMPIRISM)){
                    if(charge >= DoubleBowItem.MAX_CHARGE){
                        if(owner instanceof LivingEntity livingEntity){
                            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 1200, (int) Math.min(1, damage/8), true, false, true), this);
                        }
                        if(entityHit instanceof LivingEntity livingEntityHit){
                            livingEntityHit.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 300, 0, true, false, true), this);
                        }
                        charge = 0;
                    }
                    else{
                        charge += (int) ((entityHit instanceof PlayerEntity) ? damagePlayer : damage);
                        if(charge > DoubleBowItem.MAX_CHARGE) charge = DoubleBowItem.MAX_CHARGE;
                    }

                    compound.putInt(attunableItem.getItemChargeId(), charge);
                    weapon.setDamage(1001 - (5 * charge));
                    weapon.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(compound));
                }
            }
        }
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(Items.ARROW);
    }

    @Override
    protected ItemStack asItemStack() {
        return this.pickup ? new ItemStack(Items.ARROW) : ItemStack.EMPTY;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.inGround && this.inGroundTime != 0 && this.inGroundTime >= 600) {
            this.getWorld().sendEntityStatus(this, (byte)0);
        }
        if(this.secondShot != null && this.tickTimer <= 0){
            this.spawnSecondArrow();
        }
        if(this.tickTimer > 0) this.tickTimer--;
    }

    private void spawnSecondArrow(){
        if(!this.getWorld().isClient()) {
            this.getWorld().spawnEntity(this.secondShot);
            this.secondShot = null;
        }
    }
}

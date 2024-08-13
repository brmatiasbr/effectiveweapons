package net.br_matias_br.effectiveweapons.entity.custom;

import net.br_matias_br.effectiveweapons.entity.EffectiveWeaponsEntities;
import net.br_matias_br.effectiveweapons.networking.ParticleRequestPayload;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileDeflection;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FixedDamageArrowEntity extends PersistentProjectileEntity {
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
    }
    public FixedDamageArrowEntity(World world, LivingEntity owner, ItemStack stack, @Nullable ItemStack shotFrom, boolean critical, double damage){
        this(world, owner, stack, shotFrom, critical);
        this.damage = damage;
    }


    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        double d = this.damage == 0 ? 4 : this.damage;
        if(this.isCritical()){
            d = Math.pow(d, 2.5);
        }
        Entity entity2 = this.getOwner();
        DamageSource damageSource = this.getDamageSources().arrow(this, (entity2 != null ? entity2 : this));

        if (entity2 instanceof LivingEntity livingEntity) {
            livingEntity.onAttacking(entity);
        }

        boolean bl = entity.getType() == EntityType.ENDERMAN;
        int j = entity.getFireTicks();
        if (this.isOnFire() && !bl) {
            entity.setOnFireFor(5.0F);
        }
        if(bl && this.isCritical()){
            damageSource = this.getDamageSources().magic();
        }

        if(entity instanceof PlayerEntity player){
            if(d > player.getMaxHealth() * 0.8){
                float overkill = (float) (d - player.getMaxHealth() * 0.8f);
                d = player.getMaxHealth() * 0.8;

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
            }
        }

        if (entity.damage(damageSource, (float) d)) {
            if(this.isCritical() && !this.getWorld().isClient()){
                for(ServerPlayerEntity player : PlayerLookup.around((ServerWorld) this.getWorld(),
                        new Vec3d(this.getX(), this.getY(), this.getZ()), 128)){
                    ServerPlayNetworking.send(player, new ParticleRequestPayload(entity.getId(), 5));
                }
            }

            if (entity instanceof LivingEntity livingEntity2) {
                if (!this.getWorld().isClient && this.getPierceLevel() <= 0) {
                    livingEntity2.setStuckArrowCount(livingEntity2.getStuckArrowCount() + 1);
                }

                this.knockback(livingEntity2, damageSource);
                if (this.getWorld() instanceof ServerWorld serverWorld2) {
                    EnchantmentHelper.onTargetDamaged(serverWorld2, livingEntity2, damageSource, this.getWeaponStack());
                }

                this.onHit(livingEntity2);
                if (livingEntity2 != entity2 && livingEntity2 instanceof PlayerEntity && entity2 instanceof ServerPlayerEntity && !this.isSilent()) {
                    ((ServerPlayerEntity)entity2)
                            .networkHandler
                            .sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.PROJECTILE_HIT_PLAYER, GameStateChangeS2CPacket.DEMO_OPEN_SCREEN));
                }
            }

            this.playSound(this.getSound(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
            if (this.getPierceLevel() <= 0) {
                this.discard();
            }
        } else {
            entity.setFireTicks(j);
            this.deflect(ProjectileDeflection.SIMPLE, entity, this.getOwner(), false);
            this.setVelocity(this.getVelocity().multiply(0.2));
            if (!this.getWorld().isClient && this.getVelocity().lengthSquared() < 1.0E-7) {
                if (this.pickupType == PersistentProjectileEntity.PickupPermission.ALLOWED) {
                    this.dropStack(this.asItemStack(), 0.1F);
                }

                this.discard();
            }
        }
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(Items.ARROW);
    }

    @Override
    protected ItemStack asItemStack() {
        return new ItemStack(Items.ARROW);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.inGround && this.inGroundTime != 0 && this.inGroundTime >= 600) {
            this.getWorld().sendEntityStatus(this, (byte)0);
            this.setStack(new ItemStack(Items.ARROW));
        }
    }

}

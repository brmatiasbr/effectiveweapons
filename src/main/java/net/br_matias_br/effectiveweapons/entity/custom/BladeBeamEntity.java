package net.br_matias_br.effectiveweapons.entity.custom;

import net.br_matias_br.effectiveweapons.entity.EffectiveWeaponsDamageSources;
import net.br_matias_br.effectiveweapons.networking.EntitySynchronizationPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BladeBeamEntity extends ProjectileEntity {
    protected Entity summoner;
    private static final TrackedData<Boolean> ORANGE = DataTracker.registerData(BladeBeamEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public int model_rotation = 0;
    protected int duration = 200;
    protected boolean init = false;
    protected int extraDamage = 0;

    public Entity getSummoner(){
        return this.summoner;
    }

    public BladeBeamEntity(EntityType<BladeBeamEntity> entityType, World world) {
        super(entityType, world);
    }

    public BladeBeamEntity(EntityType<BladeBeamEntity> entityType, World world, Entity summoner) {
        this(entityType, world);
        this.summoner = summoner;
    }

    public BladeBeamEntity(EntityType<BladeBeamEntity> entityType, World world, Entity summoner, double x, double y, double z) {
        this(entityType, world, summoner);
        this.setPosition(x, y, z);
    }

    public BladeBeamEntity(EntityType<BladeBeamEntity> entityType, World world, Entity summoner, double x, double y, double z, int damage) {
        this(entityType, world, summoner, x, y, z);
        this.extraDamage = damage;
        this.setOrange(damage >= 5);
    }

    public BladeBeamEntity(EntityType<BladeBeamEntity> entityType, World world, Entity summoner, double x, double y, double z, int damage, int duration) {
        this(entityType, world, summoner, x, y, z, damage);
        this.duration = duration;
    }

    public void setSummoner(Entity summoner){
        this.summoner = summoner;
    }

    @Override
    public void tick() {
        super.tick();
        if(this.getWorld().isClient() && this.summoner == null && !this.init){              // Sends request to get owner to server:
            ClientPlayNetworking.send(new EntitySynchronizationPayload(this.getId(), 0, 0));   // Request for [entity ID] -> server gets owner from server-side entity -> client receives owner
            this.init = true;
            return;
        }
        if(this.getWorld().isClient()){
            if(model_rotation < 324) {
                model_rotation += 36;
            }
            else model_rotation = 0;
        }

        this.checkBlockCollision();
        Vec3d vec3d = this.getVelocity();
        double d = this.getX() + vec3d.x;
        double e = this.getY() + vec3d.y;
        double f = this.getZ() + vec3d.z;

        EntityHitResult entityHitResult = this.getEntityCollision(this.getPos(), this.getPos().add(this.getVelocity()));
        if(entityHitResult != null){
            this.hitOrDeflect(entityHitResult);
        }

        this.setPosition(d, e, f);
        if(this.duration <= 0){
            this.discard();
        }
        else this.duration--;
    }

    public int getRemainingDuration(){
        return this.duration;
    }
    public int getExtraDamage(){
        return this.extraDamage;
    }

    @Override
    protected void onBlockCollision(BlockState state) {
        if(state.isSolidBlock(this.getWorld(), new BlockPos((int) this.getX(), (int) this.getY(), (int) this.getZ()))){
            this.discard();
        }
    }

    @Override
    public boolean collidesWith(Entity other) {
        return other != summoner;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity target = entityHitResult.getEntity();
        if(target != this.summoner){
            if(this.summoner instanceof LivingEntity livingEntity){
                livingEntity.onAttacking(target);
            }
            DamageSource damageSource = EffectiveWeaponsDamageSources.of(this.getWorld(), EffectiveWeaponsDamageSources.BLADE_BEAM_DAMAGE);
            if(target instanceof LivingEntity livingEntity && this.summoner instanceof PlayerEntity player){
                livingEntity.setAttacking(player);
            }
            target.damage(damageSource, 5 + this.extraDamage);
            target.timeUntilRegen = 0;
            this.discard();
        }
    }

    @Nullable
    protected EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition) {
        return ProjectileUtil.getEntityCollision(
                this.getWorld(), this, currentPosition, nextPosition, this.getBoundingBox().stretch(this.getVelocity()).expand(1.0), this::canHit
        );
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(ORANGE, false);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.setOrange(nbt.getBoolean("effectiveweapons:orange"));
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putBoolean("effectiveweapons:orange", this.getOrange());
    }

    public void setOrange(boolean orange){
        this.dataTracker.set(ORANGE, orange);
    }

    public boolean getOrange(){
        return this.dataTracker.get(ORANGE);
    }
}

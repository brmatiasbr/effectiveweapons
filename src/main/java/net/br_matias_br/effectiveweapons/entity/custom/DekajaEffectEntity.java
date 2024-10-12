package net.br_matias_br.effectiveweapons.entity.custom;

import net.br_matias_br.effectiveweapons.entity.EffectiveWeaponsEntities;
import net.br_matias_br.effectiveweapons.networking.EntitySynchronizationPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class DekajaEffectEntity extends ProjectileEntity {
    protected Entity summoner;
    protected int duration = 200;
    protected boolean init = false;
    private static final TrackedData<Boolean> FRIGID = DataTracker.registerData(DekajaEffectEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> GRAVITY = DataTracker.registerData(DekajaEffectEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private ItemStack weaponStack = null;

    public Entity getSummoner(){
        return this.summoner;
    }

    public DekajaEffectEntity(EntityType<DekajaEffectEntity> entityType, World world) {
        super(entityType, world);
    }

    public DekajaEffectEntity(EntityType<DekajaEffectEntity> entityType, World world, Entity summoner) {
        this(entityType, world);
        this.summoner = summoner;
    }

    public DekajaEffectEntity(EntityType<DekajaEffectEntity> entityType, World world, Entity summoner, double x, double y, double z, ItemStack weaponStack) {
        this(entityType, world,summoner);
        this.setPosition(x, y, z);
        this.weaponStack = weaponStack;
    }

    public DekajaEffectEntity(EntityType<DekajaEffectEntity> entityType, World world, Entity summoner, double x, double y, double z, ItemStack weaponStack, boolean frigid) {
        this(entityType, world,summoner, x, y, z,weaponStack);
        this.setFrigid(frigid);
    }

    public DekajaEffectEntity(EntityType<DekajaEffectEntity> entityType, World world, Entity summoner, double x, double y, double z, ItemStack weaponStack, boolean frigid, boolean gravity) {
        this(entityType, world,summoner, x, y, z,weaponStack, frigid);
        this.setGravityEnabled(gravity);
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

        List<LivingEntity> nearbyEntities = this.getWorld().getEntitiesByType(TypeFilter.instanceOf(LivingEntity.class),
                this.getDetectionBox(3), EntityPredicates.VALID_LIVING_ENTITY);

        boolean hasOwner = this.summoner != null;

        if(hasOwner) nearbyEntities.remove(this.summoner); // Makes it so this doesn't get disrupted by its own user

        if(!nearbyEntities.isEmpty() && hasOwner){ // Makes it so this doesn't get disrupted by teammates
            nearbyEntities.removeIf(entity -> !(entity.getScoreboardTeam() == summoner.getScoreboardTeam()) && entity.getScoreboardTeam() != null);
        }

        if(!nearbyEntities.isEmpty()){
            boolean anySameHeight = false;
            for(LivingEntity entity : nearbyEntities){
                if (entity.getY() < this.getY() && this.getY() < (entity.getY() + entity.getHeight())) {
                    anySameHeight = true;
                    break;
                }
            }

            if(anySameHeight) this.trigger(100, true);
        }

        this.checkBlockCollision();
        Vec3d vec3d = this.getVelocity();
        double d = this.getX() + vec3d.x;
        double e = this.getY() + vec3d.y;
        double f = this.getZ() + vec3d.z;

        this.setPosition(d, e, f);
        if(this.duration <= 0){
            this.trigger(100, false);
        }
        else this.duration--;
    }

    public int getRemainingDuration(){
        return this.duration;
    }

    @Override
    protected void onBlockCollision(BlockState state) {
        if(state.isSolidBlock(this.getWorld(), new BlockPos((int) this.getX(), (int) this.getY(), (int) this.getZ()))){
            this.trigger(100, false);
        }
    }

    private void trigger(int duration, boolean fromEntity){
        if(!this.getWorld().isClient()){
            double spawnX = this.getX(), spawnY = this.getY(), spawnZ = this.getZ();
            double velocityX = this.getVelocity().getX(), velocityY = this.getVelocity().getY(), velocityZ = this.getVelocity().getZ();
            if(!fromEntity){
                spawnX -= velocityX;
                spawnY -= velocityY;
                spawnZ -= velocityZ;
            }
            AreaNoEffectCloudEntity areaNoEffectCloud = new AreaNoEffectCloudEntity(EffectiveWeaponsEntities.AREA_NO_EFFECT_CLOUD_ENTITY_TYPE,
                    this.getWorld(), duration, this.getSummoner() == null ? null : this.getSummoner(),
                    spawnX, spawnY, spawnZ, weaponStack, this.isFrigid(), this.isGravityEnabled());
            this.getWorld().spawnEntity(areaNoEffectCloud);
        }
        else {
            this.getWorld().addParticle(ParticleTypes.FLASH, this.getX(), this.getY() + (this.getHeight()/2), this.getZ(),
                    0, 0, 0);
        }
        this.discard();
    }

    private Box getDetectionBox(double side){
        double halfSide = side/2;
        return new Box(this.getX() - halfSide, this.getY() - halfSide, this.getZ() - halfSide, this.getX() + halfSide, this.getY() + halfSide, this.getZ() + halfSide);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(FRIGID, false);
        builder.add(GRAVITY, false);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.setFrigid(nbt.getBoolean("effectiveweapons:frigid"));
        this.setGravityEnabled(nbt.getBoolean("effectiveweapons:gravity"));
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putBoolean("effectiveweapons:frigid", this.isFrigid());
        nbt.putBoolean("effectiveweapons:gravity", this.isGravityEnabled());
    }

    public boolean isFrigid(){
        return this.dataTracker.get(FRIGID);
    }

    public void setFrigid(boolean frigid){
        this.dataTracker.set(FRIGID, frigid);
    }

    public boolean isGravityEnabled(){
        return this.dataTracker.get(GRAVITY);
    }

    public void setGravityEnabled(boolean frigid){
        this.dataTracker.set(GRAVITY, frigid);
    }
}

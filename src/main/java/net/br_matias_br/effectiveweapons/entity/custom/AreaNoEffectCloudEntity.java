package net.br_matias_br.effectiveweapons.entity.custom;

import net.br_matias_br.effectiveweapons.item.EffectiveWeaponsItems;
import net.br_matias_br.effectiveweapons.networking.EntitySynchronizationPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class AreaNoEffectCloudEntity extends Entity {

    protected Entity owner;
    protected int duration = 5;
    protected int initCountdown = 2;
    protected int highestDuration = 0;

    public AreaNoEffectCloudEntity(EntityType<AreaNoEffectCloudEntity> entityType, World world) {
        super(entityType, world);
    }

    public AreaNoEffectCloudEntity(EntityType<AreaNoEffectCloudEntity> entityType, World world, int duration) {
        super(entityType, world);
        this.duration = duration;
        this.owner = null;
    }

    public AreaNoEffectCloudEntity(EntityType<AreaNoEffectCloudEntity> entityType, World world, int duration, Entity owner) {
        this(entityType, world, duration);
        this.owner = owner;
    }

    public AreaNoEffectCloudEntity(EntityType<AreaNoEffectCloudEntity> entityType, World world, int duration, Entity owner, double x, double y, double z) {
        this(entityType, world, duration, owner);
        this.setPosition(x, y, z);
    }


    @Override
    public void tick() {
        super.tick();
        if(this.getWorld().isClient() && this.initCountdown > 0){
            ClientPlayNetworking.send(new EntitySynchronizationPayload(this.getId(), 0, initCountdown));
//            System.out.println("Synchronizing");
            this.initCountdown--;
            return;
        }

        BlockPos pos = new BlockPos((int) this.getX(), (int) this.getY(), (int) this.getZ());
        if(this.getWorld().getBlockState(pos).isSolidBlock(this.getWorld(), pos)){
            if(!this.getWorld().getBlockState(pos.east()).isSolidBlock(this.getWorld(), pos.east())){
                this.setPosition(this.getX() + 1, this.getY(), this.getZ());
            }
            else if(!this.getWorld().getBlockState(pos.west()).isSolidBlock(this.getWorld(), pos.west())){
                this.setPosition(this.getX() - 1, this.getY(), this.getZ());
            }
            else if(!this.getWorld().getBlockState(pos.north()).isSolidBlock(this.getWorld(), pos.north())){
                this.setPosition(this.getX(), this.getY(), this.getZ() - 1);
            }
            else if(!this.getWorld().getBlockState(pos.south()).isSolidBlock(this.getWorld(), pos.south())){
                this.setPosition(this.getX(), this.getY(), this.getZ() + 1);
            }
            else if(!this.getWorld().getBlockState(pos.down()).isSolidBlock(this.getWorld(), pos.down())){
                this.setPosition(this.getX(), this.getY() -1, this.getZ());
            }
            else{
                this.setPosition(this.getX(), this.getY() + 1, this.getZ());
            }
        }

        List<LivingEntity> entitiesInside = this.getWorld().getEntitiesByType(TypeFilter.instanceOf(LivingEntity.class),
                this.getBoundingBox(), EntityPredicates.VALID_LIVING_ENTITY);

        boolean hasOwner = this.owner != null;

        if(hasOwner) entitiesInside.remove(this.owner);

        if(!entitiesInside.isEmpty()){
            for(LivingEntity entity : entitiesInside){
                boolean entityHasTeam = entity.getScoreboardTeam() != null;
                boolean affectEntity = !entityHasTeam || (!(entity.getScoreboardTeam() == (hasOwner ? owner.getScoreboardTeam() : null)));
                if(affectEntity){
                    Collection<StatusEffectInstance> foeStatusEffects = entity.getStatusEffects();
                    LinkedList<StatusEffectInstance> statusesToNeutralize = new LinkedList<>();

                    if(!foeStatusEffects.isEmpty()) for(StatusEffectInstance statusEffect : foeStatusEffects){
                        if(statusEffect.getEffectType().value().isBeneficial()) statusesToNeutralize.add(statusEffect);
                    }

                    if(!statusesToNeutralize.isEmpty()) for(StatusEffectInstance statusEffect : statusesToNeutralize){
                        if(statusEffect.getDuration() > this.highestDuration) this.highestDuration = statusEffect.getDuration();
                        System.out.println("Removed " + statusEffect.getEffectType().value().getName().toString() + " from " + entity.getName().toString() + ", owner: " + this.owner.getName().toString());
                        entity.removeStatusEffect(statusEffect.getEffectType());
                    }
                }
            }
        }
        if(this.duration <= 0){
            if(this.owner instanceof PlayerEntity player){
                if(highestDuration > 0) player.getItemCooldownManager().set(EffectiveWeaponsItems.DEKAJA_TOME, (int) (this.highestDuration * 0.75f));
            }
            this.discard();
        }
        else this.duration--;
    }

    public int getRemainingDuration(){
        return this.duration;
    }

    public void setDuration(int duration){
        this.duration = duration;
    }

    public void setOwner(Entity entity){
        this.owner = entity;
    }

    public Entity getOwner(){
        return this.owner;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }
}

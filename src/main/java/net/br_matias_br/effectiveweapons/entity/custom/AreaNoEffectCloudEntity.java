package net.br_matias_br.effectiveweapons.entity.custom;

import net.br_matias_br.effectiveweapons.EffectiveWeapons;
import net.br_matias_br.effectiveweapons.effect.EffectiveWeaponsEffects;
import net.br_matias_br.effectiveweapons.item.EffectiveWeaponsItems;
import net.br_matias_br.effectiveweapons.item.custom.DekajaTomeItem;
import net.br_matias_br.effectiveweapons.item.custom.LightShieldItem;
import net.br_matias_br.effectiveweapons.networking.EntitySynchronizationPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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
    protected int gatheredCharge = 0;
    protected boolean frigid = false;
    private ItemStack weaponStack = null;

    public AreaNoEffectCloudEntity(EntityType<AreaNoEffectCloudEntity> entityType, World world) {
        super(entityType, world);
    }

    public AreaNoEffectCloudEntity(EntityType<?> entityType, World world, boolean dummy) {
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

    public AreaNoEffectCloudEntity(EntityType<AreaNoEffectCloudEntity> entityType, World world, int duration, Entity owner, double x, double y, double z, ItemStack weaponStack) {
        this(entityType, world, duration, owner);
        this.setPosition(x, y, z);
        this.weaponStack = weaponStack;
    }

    public AreaNoEffectCloudEntity(EntityType<AreaNoEffectCloudEntity> entityType, World world, int duration, Entity owner, double x, double y, double z, ItemStack weaponStack, boolean frigid) {
        this(entityType, world, duration, owner, x, y, z, weaponStack);
        this.frigid = frigid;
    }

    public ItemStack getWeaponStack(){
        return this.weaponStack;
    }

    @Override
    public void tick() {
        super.tick();
        if(this.getWorld().isClient() && this.initCountdown > 0){
            ClientPlayNetworking.send(new EntitySynchronizationPayload(this.getId(), 0, initCountdown));
            this.initCountdown--;
            return;
        }

        BlockPos pos = new BlockPos((int) this.getX(), (int) this.getY(), (int) this.getZ());
        if(this.getWorld().getBlockState(pos).isSolidBlock(this.getWorld(), pos)){
            double posX = this.getX(), posY = this.getY(), posZ = this.getZ();
            World world = this.getWorld();
            if(!world.getBlockState(pos.east()).isSolidBlock(this.getWorld(), pos.east())){
                this.setPosition(posX + 1, posY, posZ);
            }
            else if(!world.getBlockState(pos.west()).isSolidBlock(this.getWorld(), pos.west())){
                this.setPosition(posX - 1, posY, posZ);
            }
            else if(!world.getBlockState(pos.north()).isSolidBlock(this.getWorld(), pos.north())){
                this.setPosition(posX, posY, posZ - 1);
            }
            else if(!world.getBlockState(pos.south()).isSolidBlock(this.getWorld(), pos.south())){
                this.setPosition(posX, posY, posZ + 1);
            }
            else if(!world.getBlockState(pos.down()).isSolidBlock(this.getWorld(), pos.down())){
                this.setPosition(posX, posY -1, posZ);
            }
            else{
                this.setPosition(posX, posY + 1, posZ);
            }
        }

        List<LivingEntity> entitiesInside = this.getWorld().getEntitiesByType(TypeFilter.instanceOf(LivingEntity.class),
                this.getBoundingBox(), EntityPredicates.VALID_LIVING_ENTITY);

        boolean hasOwner = this.owner != null, ownerInside = false, activatePowerSwing = false;

        if(hasOwner) ownerInside = entitiesInside.remove(this.owner);

        if(!entitiesInside.isEmpty()){
            String meterAbility = this.getWeaponPassiveAbility();
            if(ownerInside && meterAbility.equals(DekajaTomeItem.PASSIVE_POWER_SWING)){
                if(this.owner instanceof LivingEntity){
                    activatePowerSwing = true;}
            }
            for(LivingEntity entity : entitiesInside){
                if(this.frigid && entity.canFreeze()) {
                    entity.setFrozenTicks(entity.getMinFreezeDamageTicks() + 400);
                }
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
                        entity.removeStatusEffect(statusEffect.getEffectType());
                        this.gatheredCharge++;
                    }

                    if(activatePowerSwing){
                        int duration = ((10 + (this.gatheredCharge * 2)) * 20) + 1;
                        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, duration, 0, false, true, true));
                        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, duration, 0, false, true, true));
                        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, duration, 0, false, true, true));
                    }
                    else if(meterAbility.equals(DekajaTomeItem.PASSIVE_ISOLATION)){
                        entity.addStatusEffect(new StatusEffectInstance(EffectiveWeaponsEffects.ISOLATED_REGISTRY_ENTRY, 1200, 0, false, true, true));
                    }
                }
            }
            if(activatePowerSwing) {
                LivingEntity livingEntity = (LivingEntity) this.owner;
                int duration = ((10 + (this.gatheredCharge * 2)) * 20) + 1;
                livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, duration, 0, false, false, true));
                livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, duration, 0, false, false, true));
                livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, duration, 0, false, false, true));
            }
        }
        if(this.duration <= 0){
            if(this.weaponStack != null){
                if(this.weaponStack.isOf(EffectiveWeaponsItems.DEKAJA_TOME)){
                    NbtCompound compound = ((DekajaTomeItem) (weaponStack.getItem())).getCompoundOrDefault(weaponStack);
                    String meterAbility = compound.getString(EffectiveWeapons.METER_ABILITY);
                    if (!meterAbility.equals(DekajaTomeItem.METER_REPULSION)) {
                        int currentCharge = compound.getInt(DekajaTomeItem.CURRENT_CHARGE);
                        currentCharge += this.gatheredCharge;
                        if (currentCharge > DekajaTomeItem.MAX_CHARGE) currentCharge = DekajaTomeItem.MAX_CHARGE;
                        compound.putInt(LightShieldItem.CURRENT_CHARGE, currentCharge);
                        this.weaponStack.setDamage(1001 - (100 * (currentCharge)));
                        this.weaponStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(compound));
                    }
                }
            }
            if(this.owner instanceof PlayerEntity player){
                if(highestDuration > 0) player.getItemCooldownManager().set(EffectiveWeaponsItems.DEKAJA_TOME, (int) (this.highestDuration * 0.75f));
            }
            this.discard();
        }
        else this.duration--;
    }

    public String getWeaponPassiveAbility(){
        if(this.weaponStack != null){
            if(this.weaponStack.isOf(EffectiveWeaponsItems.DEKAJA_TOME)) {
                NbtCompound compound = ((DekajaTomeItem) (this.weaponStack.getItem())).getCompoundOrDefault(this.weaponStack);
                return compound.getString(EffectiveWeapons.PASSIVE_ABILITY);
            }
        }
        return "";
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

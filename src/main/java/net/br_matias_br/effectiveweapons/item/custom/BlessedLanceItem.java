package net.br_matias_br.effectiveweapons.item.custom;

import net.br_matias_br.effectiveweapons.EffectiveWeapons;
import net.br_matias_br.effectiveweapons.item.material.EffectiveWeaponMaterial;
import net.br_matias_br.effectiveweapons.networking.ParticleRequestPayload;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class BlessedLanceItem extends LanceItem implements AttunableItem{
    public BlessedLanceItem(ToolMaterial material, Settings settings) {
        super(material, settings);
    }

    public static final String METER_ELEVATED = "effectiveweapons:meter_elevated";
    public static final String METER_GRAVE_KEEPER = "effectiveweapons:meter_grave_keeper";
    public static final String METER_REFRESH = "effectiveweapons:meter_refresh";
    public static final String CURRENT_CHARGE = "effectiveweapons:current_charge";

    public static final int MAX_CHARGE = 200;
    public static final int MAX_DASH_CHARGE = 15;

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        Collection<StatusEffectInstance> statusEffects = user.getStatusEffects();
        boolean isCoolingDown = false;
        ItemCooldownManager cooldownManager = null;
        if(user instanceof PlayerEntity player) {
            isCoolingDown = player.getItemCooldownManager().isCoolingDown(this);
            cooldownManager = player.getItemCooldownManager();
        }
        int cooldown = 0;

        if(!statusEffects.isEmpty() && !isCoolingDown){
            cooldown = neutralizeEffects(user, world, false);
        }
        if(cooldown > 0 && cooldownManager != null){
            cooldownManager.set(this, Math.min(cooldown, 2400));
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if(user.getItemCooldownManager().isCoolingDown(this)){
            return TypedActionResult.fail(stack);
        }
        stack.setHolder(user);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(stack);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPEAR;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        Collection<StatusEffectInstance> statusEffects = entity.getStatusEffects();
        int cooldown = 0;

        if(!statusEffects.isEmpty() && !user.getItemCooldownManager().isCoolingDown(this)){
            cooldown = neutralizeEffects(entity, user.getWorld(), false);
        }
        if(cooldown > 0){
            user.getItemCooldownManager().set(this, Math.min(cooldown, 2400));
        }

        return cooldown > 0 ? ActionResult.success(true) :  super.useOnEntity(stack, user, entity, hand);
    }

    public static int neutralizeEffects(LivingEntity entity, World world, boolean refresh){
        Collection<StatusEffectInstance> statusEffects = entity.getStatusEffects();
        LinkedList<StatusEffectInstance> effectsToClear = new LinkedList<>();
        boolean success = false;
        int highestDuration = 0;
        int cooldown = 0;

        for(StatusEffectInstance statusEffect : statusEffects){
            if(!statusEffect.getEffectType().value().isBeneficial() && statusEffect.getEffectType() != StatusEffects.TRIAL_OMEN){
                effectsToClear.add(statusEffect);
            }
        }
        if(!effectsToClear.isEmpty()){
            success = true;
            if(!refresh){
                for (StatusEffectInstance statusEffect : effectsToClear) {
                    if (highestDuration < statusEffect.getDuration()) {
                        highestDuration = statusEffect.getDuration();
                    }
                    entity.removeStatusEffect(statusEffect.getEffectType());
                }
            }
            else entity.removeStatusEffect(effectsToClear.get(world.getRandom().nextInt(effectsToClear.size())).getEffectType());
        }

        if(success){
            if(!world.isClient()){
                for(ServerPlayerEntity player : PlayerLookup.around((ServerWorld) world,
                        new Vec3d(entity.getX(), entity.getY(), entity.getZ()), 128)){
                    ServerPlayNetworking.send(player, new ParticleRequestPayload(entity.getId(), 2));
                }
                world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENTITY_PLAYER_SWIM, SoundCategory.PLAYERS);
            }
            cooldown = (int) (highestDuration * 0.75f);
        }
        return cooldown;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if(!selected){
            return;
        }
        NbtCompound compound = this.getCompoundOrDefault(stack);
        if(compound.getString(EffectiveWeapons.METER_ABILITY).equals(METER_GRAVE_KEEPER)){
            int charge = compound.getInt(CURRENT_CHARGE);
            if(charge < MAX_DASH_CHARGE){
                charge++;
            }
            else if(!entity.isOnGround() && entity.isSneaking()){
                charge = 0;
                double power = 0.75, velocityX, velocityY, velocityZ;
                double yawRadian = Math.toRadians(entity.getYaw());

                velocityY = 0.05;
                velocityX = Math.sin(-yawRadian) * -power;
                velocityZ = Math.cos(yawRadian) * -power;
                Vec3d velocity = new Vec3d(velocityX, velocityY, velocityZ);

                entity.setVelocity(velocity);
                if(entity instanceof PlayerEntity player) {
                    player.currentExplosionImpactPos = entity.getPos().add(0, -20, 0);
                    player.setIgnoreFallDamageFromCurrentExplosion(true);
                }
            }

            compound.putFloat(CURRENT_CHARGE, charge);
            NbtComponent component = NbtComponent.of(compound);
            stack.set(DataComponentTypes.CUSTOM_DATA, component);
            stack.setDamage(1001 - ((1000/MAX_DASH_CHARGE) * charge));
        }
    }

    @Override
    public boolean allowComponentsUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        NbtCompound compound = this.getCompoundOrDefault(stack);
        String meterAbility = compound.getString(EffectiveWeapons.METER_ABILITY);
        int color = 0x32A60C;
        if(meterAbility.equals(METER_GRAVE_KEEPER)){
            color = 0x575757;
        }
        if(meterAbility.equals(METER_ELEVATED)){
            color = 0x94BDFF;
        }
        return color;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        boolean onCooldown = false;
        float cooldown = 0;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        boolean controlHeld = Screen.hasControlDown();
        String passiveAbility, meterAbility;

        NbtCompound compound = this.getCompoundOrDefault(stack);

        passiveAbility = compound.getString(EffectiveWeapons.PASSIVE_ABILITY);
        meterAbility = compound.getString(EffectiveWeapons.METER_ABILITY);

        if(player != null){
            onCooldown = player.getItemCooldownManager().isCoolingDown(this);
            cooldown = player.getItemCooldownManager().getCooldownProgress(this, 0);
        }

        NumberFormat formatter = new DecimalFormat("#0");

        if(controlHeld){
            tooltip.add(Text.translatable("tooltip.blessed_lance").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
            tooltip.add(Text.translatable("tooltip.blessed_lance_cont").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
            tooltip.add(Text.translatable("tooltip.blessed_lance_cooldown").formatted(Formatting.ITALIC).formatted(Formatting.BLUE));
            tooltip.add(Text.translatable("tooltip.blessed_lance_cooldown_cont").formatted(Formatting.ITALIC).formatted(Formatting.BLUE));
            this.buildCustomizationTooltip(tooltip, passiveAbility, meterAbility);
            if(!meterAbility.equals(EffectiveWeapons.METER_NONE))
                tooltip.add(Text.translatable(meterAbility.equals(METER_GRAVE_KEEPER) ? "tooltip.sneak_air_meter" : "tooltip.auto_meter").formatted(Formatting.ITALIC).formatted(Formatting.DARK_PURPLE));
            tooltip.add(Text.translatable("tooltip.attuned_customization_enabled").formatted(Formatting.ITALIC).formatted(Formatting.BLUE));
        }
        else{
            tooltip.add(Text.translatable("tooltip.blessed_lance_summary").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
            this.buildCustomizationTooltip(tooltip, passiveAbility, meterAbility);
            tooltip.add(Text.translatable("tooltip.more_info").formatted(Formatting.ITALIC).formatted(Formatting.BLUE));
        }
        if(onCooldown) tooltip.add(Text.literal("Remaining cooldown: " + formatter.format(cooldown * 100) + "%").formatted(Formatting.ITALIC).formatted(Formatting.RED));
        super.appendTooltip(stack, context, tooltip, type);
    }

    @Override
    public float getBonusAttackDamage(Entity target, float baseAttackDamage, DamageSource damageSource) {
        return target.getType().isIn(EntityTypeTags.SENSITIVE_TO_SMITE) ? 4 : 0;
    }

    @Override
    public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if(stack.getDamage() != 0){
            stack.setDamage(0);
        }
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if(stack.getDamage() != 0){
            stack.setDamage(0);
        }
        return false;
    }

    @Override
    public ArrayList<String> getPossibleAttunedCustomizations() {
        ArrayList<String> customizations = new ArrayList<>();
        customizations.add(EffectiveWeapons.PASSIVE_FIRM);
        customizations.add(EffectiveWeapons.PASSIVE_FEATHERWEIGHT);
        customizations.add(EffectiveWeapons.PASSIVE_BUFFER);
        customizations.add(METER_ELEVATED);
        customizations.add(METER_REFRESH);
        customizations.add(METER_GRAVE_KEEPER);
        return customizations;
    }

    @Override
    public ArrayList<String> getPossibleAttunedPassiveCustomizations() {
        ArrayList<String> customizations = new ArrayList<>();
        customizations.add(EffectiveWeapons.PASSIVE_FIRM);
        customizations.add(EffectiveWeapons.PASSIVE_FEATHERWEIGHT);
        customizations.add(EffectiveWeapons.PASSIVE_BUFFER);
        return customizations;
    }

    @Override
    public ArrayList<String> getPossibleAttunedMeterCustomizations() {
        ArrayList<String> customizations = new ArrayList<>();
        customizations.add(METER_ELEVATED);
        customizations.add(METER_REFRESH);
        customizations.add(METER_GRAVE_KEEPER);
        return customizations;
    }

    @Override
    public AttributeModifiersComponent getDefaultAttributeModifiers() {
        return LanceItem.createAttributeModifiers(EffectiveWeaponMaterial.INSTANCE, 8f, -2.7f);
    }

    @Override
    public String getDefaultPassiveCustomization() {
        return EffectiveWeapons.PASSIVE_NONE;
    }

    @Override
    public String getDefaultMeterCustomization() {
        return EffectiveWeapons.METER_NONE;
    }

    @Override
    public String getItemChargeId() {
        return CURRENT_CHARGE;
    }

    @Override
    public int getDefaultDurabilityDamage() {
        return 0;
    }

    @Override
    public boolean canChargeByHit() {
        return true;
    }
}

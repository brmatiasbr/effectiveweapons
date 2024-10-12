package net.br_matias_br.effectiveweapons.item.custom;

import net.br_matias_br.effectiveweapons.EffectiveWeapons;
import net.br_matias_br.effectiveweapons.item.material.EffectiveWeaponMaterial;
import net.br_matias_br.effectiveweapons.networking.ItemModificationPayload;
import net.br_matias_br.effectiveweapons.networking.ParticleRequestPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
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
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class RogueDaggerItem extends DaggerItem implements AttunableItem{
    public RogueDaggerItem(ToolMaterial material, Settings settings) {
        super(material, settings);
    }

    public static final String CURRENT_CHARGE = "effectiveweapons:current_charge";
    public static final String METER_BLADE_BEAM = "effectiveweapons:meter_blade_beam";
    public static final String METER_PURSUIT = "effectiveweapons:meter_pursuit";
    public static final String METER_SNATCH = "effectiveweapons:meter_snatch";
    public static final int MAX_CHARGE = 100;
    public static final int MAX_CHARGE_PURSUIT = 40;

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        float yawDifference = Math.abs(target.getHeadYaw() - attacker.getYaw());
        boolean positiveSteal = false;
        if(yawDifference > 180) yawDifference = 360 - yawDifference;

        if(yawDifference < 40 || (yawDifference < 95 && attacker.isSneaking())){
            positiveSteal = true;
        }

        int highestDuration = this.stealEffects(attacker, target, positiveSteal, false);

        if(highestDuration > -1) {
            for(ServerPlayerEntity player : PlayerLookup.around((ServerWorld) attacker.getWorld(),
                    new Vec3d(target.getX(), target.getY(), target.getZ()), 128)){
                ServerPlayNetworking.send(player, new ParticleRequestPayload(target.getId(), 3));
            }
            if(attacker instanceof PlayerEntity player){
                int cooldown = (int) (highestDuration * 0.75f);
                player.getItemCooldownManager().set(this, Math.min(cooldown, 3600));
            }
        }

        return super.postHit(stack, target, attacker);
    }

    protected int stealEffects(LivingEntity user, LivingEntity target, boolean positiveSteal, boolean ignoreCooldown){
        Collection<StatusEffectInstance> effectsToSteal = target.getStatusEffects();
        LinkedList<StatusEffectInstance> myBagAfterIStealThoseStatusEffects = new LinkedList<>();
        boolean canSteal = true;
        int highestDuration = -1;

        if(user instanceof PlayerEntity player && !ignoreCooldown){
            if(player.getItemCooldownManager().isCoolingDown(this)) canSteal = false;
        }

        if(!effectsToSteal.isEmpty() && canSteal) for(StatusEffectInstance statusEffect: effectsToSteal){
            if(!statusEffect.getEffectType().value().isBeneficial()){
                if(!positiveSteal) myBagAfterIStealThoseStatusEffects.add(statusEffect);
            }
            else myBagAfterIStealThoseStatusEffects.add(statusEffect);
        }
        if(!myBagAfterIStealThoseStatusEffects.isEmpty()) {
            for (StatusEffectInstance stolenStatusEffect : myBagAfterIStealThoseStatusEffects) {
                if(highestDuration < stolenStatusEffect.getDuration()) highestDuration = stolenStatusEffect.getDuration();
                user.addStatusEffect(new StatusEffectInstance(stolenStatusEffect.getEffectType(), stolenStatusEffect.getDuration(), stolenStatusEffect.getAmplifier()));

                if (target.hasStatusEffect(stolenStatusEffect.getEffectType())) {
                    target.removeStatusEffect(stolenStatusEffect.getEffectType());
                }
            }
        }

        return highestDuration;
    }

    @Override
    public float getBonusAttackDamage(Entity target, float baseAttackDamage, DamageSource damageSource) {
        if(damageSource.getSource() instanceof LivingEntity attacker){
            float yawDifference = Math.abs(target.getHeadYaw() - attacker.getYaw());
            if(yawDifference < 40) return 2f;
        }
        return 0f;
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        NbtCompound compound = this.getCompoundOrDefault(stack);
        String meterAbility = compound.getString(EffectiveWeapons.METER_ABILITY);
        if(meterAbility.equals(METER_SNATCH)){
            int charge = compound.getInt(CURRENT_CHARGE);
            if(charge >= MAX_CHARGE_PURSUIT){
                int highestDuration = this.stealEffects(user, entity, true, true);
                if(highestDuration > -1){
                    for(ServerPlayerEntity player : PlayerLookup.around((ServerWorld) user.getWorld(),
                            new Vec3d(entity.getX(), entity.getY(), entity.getZ()), 128)){
                        ServerPlayNetworking.send(player, new ParticleRequestPayload(entity.getId(), 3));
                    }
                    ItemModificationPayload itemModificationPayload = new ItemModificationPayload("reset_charge", false);
                    ClientPlayNetworking.send(itemModificationPayload);
                    user.clearActiveItem();

                    return ActionResult.SUCCESS;
                }
            }
        }
        return super.useOnEntity(stack, user, entity, hand);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        NbtCompound compound = this.getCompoundOrDefault(stack);
        String meterAbility = compound.getString(EffectiveWeapons.METER_ABILITY);
        if(meterAbility.equals(METER_PURSUIT)){
            int charge = compound.getInt(CURRENT_CHARGE);
            LivingEntity target = world.getClosestEntity(LivingEntity.class, TargetPredicate.DEFAULT, user, user.getX(), user.getY(), user.getZ(),
                    Box.of(user.getPos(), 16, 4, 16));
            if(target != null && charge >= MAX_CHARGE_PURSUIT/2){
                user.addVelocity((target.getX() - user.getX()) * 0.2, (target.getY() - user.getY()) * 0.2, (target.getZ() - user.getZ()) * 0.2);
                charge -= 20;

                stack.setDamage(1001 - (charge * 25));
                compound.putInt(this.getItemChargeId(), charge);
                NbtComponent component = NbtComponent.of(compound);
                stack.set(DataComponentTypes.CUSTOM_DATA, component);

                return TypedActionResult.success(stack, false);
            }
        }
        return super.use(world, user, hand);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        NbtCompound compound = this.getCompoundOrDefault(stack);
        int color = 0xFFFFFF, damage = stack.getDamage(),  i = stack.getMaxDamage();
        float f = Math.max(0.0F, ((float)i - (float)damage) / (float)i);
        switch (compound.getString(EffectiveWeapons.METER_ABILITY)){
            case METER_PURSUIT -> {
                if(damage > 501){
                    color = 0x424242;
                }
                else if(damage > 1){
                    color = 0x4369D1;
                }
                else {
                    color = 0xA35Ef7;
                }
            }
            case METER_BLADE_BEAM -> color = EffectiveWeapons.getColorFromGradient(0x7CCfff, 0x1674AB, f);
            case METER_SNATCH -> color = EffectiveWeapons.getColorFromGradient(0x580000, 0xD03C3C, f);
            default -> super.getItemBarColor(stack);
        }

        return color;
    }

    @Override
    public boolean allowComponentsUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        boolean onCooldown = false;
        float cooldown = 0;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        boolean controlHeld = Screen.hasControlDown();
        boolean shiftHeld = Screen.hasShiftDown();
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
            if (shiftHeld) {
                tooltip.add(Text.translatable("tooltip.rogue_dagger").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
                tooltip.add(Text.translatable("tooltip.rogue_dagger_cont").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
                tooltip.add(Text.translatable("tooltip.rogue_dagger_cont_part_two").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
                tooltip.add(Text.translatable("tooltip.rogue_dagger_cooldown").formatted(Formatting.ITALIC).formatted(Formatting.BLUE));
                tooltip.add(Text.translatable("tooltip.rogue_dagger_cooldown_cont").formatted(Formatting.ITALIC).formatted(Formatting.BLUE));
                this.buildCustomizationTooltip(tooltip, passiveAbility, meterAbility);
                if (!meterAbility.equals(EffectiveWeapons.METER_NONE)) {
                    switch (meterAbility) {
                        case METER_PURSUIT ->
                                tooltip.add(Text.translatable("tooltip.use_meter").formatted(Formatting.ITALIC).formatted(Formatting.DARK_PURPLE));
                        case METER_BLADE_BEAM ->
                                tooltip.add(Text.translatable("tooltip.swing_meter").formatted(Formatting.ITALIC).formatted(Formatting.DARK_PURPLE));
                        case METER_SNATCH ->
                                tooltip.add(Text.translatable("tooltip.entity_meter").formatted(Formatting.ITALIC).formatted(Formatting.DARK_PURPLE));
                    }
                }
                tooltip.add(Text.translatable("tooltip.attuned_customization_enabled").formatted(Formatting.ITALIC).formatted(Formatting.BLUE));
            } else {
                tooltip.add(Text.translatable("tooltip.rogue_dagger_summary").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
                this.buildCustomizationTooltip(tooltip, passiveAbility, meterAbility);
                tooltip.add(Text.translatable("tooltip.more_info").formatted(Formatting.ITALIC).formatted(Formatting.BLUE));
            }
        }
        else{
            tooltip.add(Text.translatable("tooltip.show_weapon_summary").formatted(Formatting.ITALIC).formatted(Formatting.BLUE));
        }
        if(onCooldown) tooltip.add(Text.literal("Remaining cooldown: " + formatter.format(cooldown * 100) + "%").formatted(Formatting.ITALIC).formatted(Formatting.RED));
        super.appendTooltip(stack, context, tooltip, type);
    }

    @Override
    public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {}

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
        customizations.add(EffectiveWeapons.PASSIVE_ACROBATICS);
        customizations.add(EffectiveWeapons.PASSIVE_STABBING);
        customizations.add(EffectiveWeapons.PASSIVE_MOBILE);
        customizations.add(METER_BLADE_BEAM);
        customizations.add(METER_PURSUIT);
        customizations.add(METER_SNATCH);
        return customizations;
    }

    @Override
    public ArrayList<String> getPossibleAttunedPassiveCustomizations() {
        ArrayList<String> customizations = new ArrayList<>();
        customizations.add(EffectiveWeapons.PASSIVE_ACROBATICS);
        customizations.add(EffectiveWeapons.PASSIVE_STABBING);
        customizations.add(EffectiveWeapons.PASSIVE_MOBILE);
        return customizations;
    }

    @Override
    public ArrayList<String> getPossibleAttunedMeterCustomizations() {
        ArrayList<String> customizations = new ArrayList<>();
        customizations.add(METER_BLADE_BEAM);
        customizations.add(METER_PURSUIT);
        customizations.add(METER_SNATCH);
        return customizations;
    }

    @Override
    public AttributeModifiersComponent getDefaultAttributeModifiers() {
        return DaggerItem.createAttributeModifiers(EffectiveWeaponMaterial.INSTANCE, 2.2f, -1f);
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
}

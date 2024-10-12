package net.br_matias_br.effectiveweapons.item.custom;

import com.google.common.collect.BiMap;
import net.br_matias_br.effectiveweapons.EffectiveWeapons;
import net.br_matias_br.effectiveweapons.entity.EffectiveWeaponsDamageSources;
import net.br_matias_br.effectiveweapons.item.material.EffectiveWeaponMaterial;
import net.br_matias_br.effectiveweapons.networking.ParticleRequestPayload;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable;
import net.minecraft.block.PillarBlock;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class PactAxeItem extends AxeItem implements AttunableItem{
    public static final String CURRENT_CHARGE = "effectiveweapons:current_charge";
    public static final String METER_COUNTER = "effectiveweapons:meter_counter";
    public static final String METER_RESONANCE = "effectiveweapons:meter_resonance";
    public static final String METER_DOMAIN_OF_FIRE = "effectiveweapons:meter_domain_of_fire";
    public static final int MAX_CHARGE = 200;

    public PactAxeItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if(target.getHealth() <= 0 && !target.isExperienceDroppingDisabled()){
            int extraEXP = 3;
            if(target.canTarget(attacker)){
                extraEXP = 8;
            }

            ExperienceOrbEntity.spawn((ServerWorld) attacker.getWorld(), attacker.getPos(),
                    EnchantmentHelper.getMobExperience((ServerWorld) attacker.getWorld(), attacker, target, extraEXP));
        }

        return super.postHit(stack, target, attacker);
    }

    @Override
    public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {}

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        return false;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        PlayerEntity playerEntity = context.getPlayer();
        if (shouldCancelStripAttempt(context)) {
            return ActionResult.PASS;
        } else {
            Optional<BlockState> optional = this.tryStrip(world, blockPos, playerEntity, world.getBlockState(blockPos));
            if (optional.isEmpty()) {
                return ActionResult.PASS;
            } else {
                ItemStack itemStack = context.getStack();
                if (playerEntity instanceof ServerPlayerEntity) {
                    Criteria.ITEM_USED_ON_BLOCK.trigger((ServerPlayerEntity)playerEntity, blockPos, itemStack);
                }

                world.setBlockState(blockPos, (BlockState)optional.get(), Block.NOTIFY_ALL_AND_REDRAW);
                world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Emitter.of(playerEntity, (BlockState)optional.get()));

                return ActionResult.success(world.isClient);
            }
        }
    }


    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        NbtCompound compound = this.getCompoundOrDefault(stack);
        String meterAbility = compound.getString(EffectiveWeapons.METER_ABILITY);
        int charge = compound.getInt(this.getItemChargeId());
        if(meterAbility.equals(METER_RESONANCE) && charge >= MAX_CHARGE){
            LivingEntity target = world.getClosestEntity(LivingEntity.class, TargetPredicate.DEFAULT, user, user.getX(), user.getY(), user.getZ(),
                    Box.of(user.getPos(), 64, 64, 64));

            if(target != null){
                Collection<StatusEffectInstance> userStatusEffect = user.getStatusEffects();
                Collection<StatusEffectInstance> targetStatusEffect = target.getStatusEffects();
                if(!userStatusEffect.isEmpty()) for (StatusEffectInstance instance : userStatusEffect){
                    target.addStatusEffect(instance, user);
                }
                if(!targetStatusEffect.isEmpty()) for (StatusEffectInstance instance : targetStatusEffect){
                    user.addStatusEffect(instance, target);
                }

                float userHealth = user.getHealth(), userMaxHealth = user.getMaxHealth(), targetHealth = target.getHealth();
                if(targetHealth < userMaxHealth * 2.5f && targetHealth > userHealth){
                    DamageSource resonanceDamage = EffectiveWeaponsDamageSources.of(world, EffectiveWeaponsDamageSources.RESONANCE_DAMAGE);
                    if (userHealth > userHealth * 0.1f) {
                        user.damage(resonanceDamage, userMaxHealth * 0.1f);
                    }

                    target.damage(resonanceDamage, targetHealth - userHealth);
                }
                if(!user.getWorld().isClient()){
                    for (ServerPlayerEntity player : PlayerLookup.around((ServerWorld) user.getWorld(),
                            new Vec3d(user.getX(), user.getY(), user.getZ()), 128)) {
                        ServerPlayNetworking.send(player, new ParticleRequestPayload(user.getId(), 9));
                    }
                }

                charge = 0;
                compound.putInt(this.getItemChargeId(), charge);
                stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(compound));
                stack.setDamage(1001);
                return TypedActionResult.success(stack);
            }
        }
        return super.use(world, user, hand);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        boolean controlHeld = Screen.hasControlDown();
        boolean shiftHeld = Screen.hasShiftDown();
        String passiveAbility, meterAbility;

        NbtCompound compound = this.getCompoundOrDefault(stack);
        passiveAbility = compound.getString(EffectiveWeapons.PASSIVE_ABILITY);
        meterAbility = compound.getString(EffectiveWeapons.METER_ABILITY);

        if(controlHeld){
            if (shiftHeld) {
                tooltip.add(Text.translatable("tooltip.pact_axe").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
                tooltip.add(Text.translatable("tooltip.pact_axe_cont").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
                tooltip.add(Text.translatable("tooltip.pact_axe_cont_part_two").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
                tooltip.add(Text.translatable("tooltip.pact_axe_cont_part_three").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
                tooltip.add(Text.translatable("tooltip.pact_axe_cont_part_four").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
                tooltip.add(Text.translatable("tooltip.pact_axe_backfire").formatted(Formatting.ITALIC).formatted(Formatting.BLUE));
                this.buildCustomizationTooltip(tooltip, passiveAbility, meterAbility);
                if (!meterAbility.equals(EffectiveWeapons.METER_NONE))
                    tooltip.add(Text.translatable(meterAbility.equals(METER_RESONANCE) ? "tooltip.use_meter" : "tooltip.auto_meter").formatted(Formatting.ITALIC).formatted(Formatting.DARK_PURPLE));
                tooltip.add(Text.translatable("tooltip.attuned_customization_enabled").formatted(Formatting.ITALIC).formatted(Formatting.BLUE));
            } else {
                tooltip.add(Text.translatable("tooltip.pact_axe_summary").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
                this.buildCustomizationTooltip(tooltip, passiveAbility, meterAbility);
                tooltip.add(Text.translatable("tooltip.more_info").formatted(Formatting.ITALIC).formatted(Formatting.BLUE));
            }
        }
        else{
            tooltip.add(Text.translatable("tooltip.show_weapon_summary").formatted(Formatting.ITALIC).formatted(Formatting.BLUE));
        }
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        NbtCompound compound = this.getCompoundOrDefault(stack);
        String meterAbility = compound.getString(EffectiveWeapons.METER_ABILITY);
        int maxDamage = stack.getMaxDamage();
        float f = Math.max(0.0F, ((float)maxDamage - (float)stack.getDamage()) / (float)maxDamage);

        return switch (meterAbility) {
            case EffectiveWeapons.METER_NONE -> stack.getDamage() == 1 ? 0x810202: 0x1d8102;
            case METER_COUNTER -> EffectiveWeapons.getColorFromGradient(0x642A2D, 0x885051, f);
            case METER_RESONANCE -> EffectiveWeapons.getColorFromGradient(0x39C8CB, 0x22797B, f);
            case METER_DOMAIN_OF_FIRE -> EffectiveWeapons.getColorFromGradient(0xE0643C, 0xE0DD3C, f);
            default -> 0xFFFFFF;
        };
    }

    @Override
    public ArrayList<String> getPossibleAttunedCustomizations() {
        ArrayList<String> customizations = new ArrayList<>();
        customizations.add(EffectiveWeapons.PASSIVE_TOUGH);
        customizations.add(EffectiveWeapons.PASSIVE_LUMBERJACK);
        customizations.add(EffectiveWeapons.PASSIVE_MIGHTY);
        customizations.add(METER_COUNTER);
        customizations.add(METER_RESONANCE);
        customizations.add(METER_DOMAIN_OF_FIRE);
        return customizations;
    }

    @Override
    public ArrayList<String> getPossibleAttunedPassiveCustomizations() {
        ArrayList<String> customizations = new ArrayList<>();
        customizations.add(EffectiveWeapons.PASSIVE_TOUGH);
        customizations.add(EffectiveWeapons.PASSIVE_LUMBERJACK);
        customizations.add(EffectiveWeapons.PASSIVE_MIGHTY);
        return customizations;
    }

    @Override
    public ArrayList<String> getPossibleAttunedMeterCustomizations() {
        ArrayList<String> customizations = new ArrayList<>();
        customizations.add(METER_COUNTER);
        customizations.add(METER_RESONANCE);
        customizations.add(METER_DOMAIN_OF_FIRE);
        return customizations;
    }

    @Override
    public AttributeModifiersComponent getDefaultAttributeModifiers() {
        return AxeItem.createAttributeModifiers(EffectiveWeaponMaterial.INSTANCE, 12f, -2.9f);
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
        return 2;
    }

    private static boolean shouldCancelStripAttempt(ItemUsageContext context) {
        PlayerEntity playerEntity = context.getPlayer();
        return context.getHand().equals(Hand.MAIN_HAND) && playerEntity.getOffHandStack().isOf(Items.SHIELD) && !playerEntity.shouldCancelInteraction();
    }

    private Optional<BlockState> tryStrip(World world, BlockPos pos, @Nullable PlayerEntity player, BlockState state) {
        Optional<BlockState> optional = this.getStrippedState(state);
        if (optional.isPresent()) {
            world.playSound(player, pos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return optional;
        } else {
            Optional<BlockState> optional2 = Oxidizable.getDecreasedOxidationState(state);
            if (optional2.isPresent()) {
                world.playSound(player, pos, SoundEvents.ITEM_AXE_SCRAPE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.syncWorldEvent(player, WorldEvents.BLOCK_SCRAPED, pos, 0);
                return optional2;
            } else {
                Optional<BlockState> optional3 = Optional.ofNullable((Block)((BiMap)HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get()).get(state.getBlock()))
                        .map(block -> block.getStateWithProperties(state));
                if (optional3.isPresent()) {
                    world.playSound(player, pos, SoundEvents.ITEM_AXE_WAX_OFF, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    world.syncWorldEvent(player, WorldEvents.WAX_REMOVED, pos, 0);
                    return optional3;
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    private Optional<BlockState> getStrippedState(BlockState state) {
        return Optional.ofNullable((Block)STRIPPED_BLOCKS.get(state.getBlock()))
                .map(block -> block.getDefaultState().with(PillarBlock.AXIS, (Direction.Axis)state.get(PillarBlock.AXIS)));
    }

    @Override
    public boolean canChargeByHit() {
        return true;
    }

}

package net.br_matias_br.effectiveweapons.item.custom;

import net.br_matias_br.effectiveweapons.EffectiveWeapons;
import net.br_matias_br.effectiveweapons.entity.EffectiveWeaponsDamageSources;
import net.br_matias_br.effectiveweapons.entity.custom.FixedDamageArrowEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class DoubleBowItem extends BowItem implements AttunableItem{
    public DoubleBowItem(Settings settings) {
        super(settings);
    }
    public static final Predicate<ItemStack> DOUBLE_BOW_PROJECTILES = stack -> stack.isOf(Items.ARROW);
    public static final String METER_HEROIC_SPIRIT = "effectiveweapons:meter_heroic_spirit";
    public static final String METER_BURST_IMPACT = "effectiveweapons:meter_burst_impact";
    public static final String METER_VAMPIRISM = "effectiveweapons:meter_vampirism";
    public static final String PASSIVE_DEADEYE = "effectiveweapons:passive_deadeye";
    public static final String PASSIVE_DOUBLE_SHOT = "effectiveweapons:passive_double_shot";
    public static final String PASSIVE_FIRM_STANCE = "effectiveweapons:passive_firm_stance";
    public static final String CURRENT_CHARGE = "effectiveweapons:current_charge";
    public static final int MAX_CHARGE = 200;

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity playerEntity) {
            ItemStack itemStack = playerEntity.getProjectileType(stack);
            if (!itemStack.isEmpty()) {
                int i = this.getMaxUseTime(stack, user) - remainingUseTicks;
                float f = getPullProgress(i);
                if (!((double)f < 0.5)) {
                    List<ItemStack> list = load(stack, itemStack, playerEntity);
                    if (world instanceof ServerWorld serverWorld && !list.isEmpty()) {
                        this.shootAll(serverWorld, playerEntity, playerEntity.getActiveHand(), stack, list, f * 3.0F, 1.0F, f == 2.0F, null);
                    }

                    world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(),
                            SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F,
                            1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F
                    );
                    playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
                }
            }
        }
    }

    @Override
    protected void shootAll(ServerWorld world, LivingEntity shooter, Hand hand, ItemStack stack, List<ItemStack> projectiles, float speed, float divergence, boolean critical, @Nullable LivingEntity target) {
        float f = EnchantmentHelper.getProjectileSpread(world, stack, shooter, 0.0F);
        float g = projectiles.size() == 1 ? 0.0F : 2.0F * f / (float)(projectiles.size() - 1);
        float h = (float)((projectiles.size() - 1) % 2) * g / 2.0F;
        float i = 1.0F;
        NbtCompound compound = this.getCompoundOrDefault(stack);
        String passiveAbility = compound.getString(EffectiveWeapons.PASSIVE_ABILITY);
        String meterAbility = compound.getString(EffectiveWeapons.METER_ABILITY);

        for (int j = 0; j < projectiles.size(); j++) {
            ItemStack itemStack = projectiles.get(j);
            if (!itemStack.isEmpty()) {
                float k = h + i * (float)((j + 1) / 2) * g;
                i = -i;
                int bonus = 0;
                if(critical){
                    StatusEffectInstance strenghStatusEffect = null;
                    Collection<StatusEffectInstance> statusEffectInstances = shooter.getStatusEffects();
                    for(StatusEffectInstance statusEffect : statusEffectInstances){
                        if(statusEffect.getEffectType() == StatusEffects.STRENGTH){
                            strenghStatusEffect = statusEffect;
                            break;
                        }
                    }
                    if(strenghStatusEffect != null){
                        bonus = (strenghStatusEffect.getAmplifier() + 1) * 2;
                        shooter.removeStatusEffect(StatusEffects.STRENGTH);
                    }
                    else shooter.damage(EffectiveWeaponsDamageSources.of(shooter.getWorld(),
                            EffectiveWeaponsDamageSources.DOUBLE_BOW_RECOIL_DAMAGE), shooter.getMaxHealth() * 0.249f);
                }
                FixedDamageArrowEntity doubleShotArrow = null;
                boolean firstShot = false;
                if(passiveAbility.equals(PASSIVE_DOUBLE_SHOT)){
                    doubleShotArrow = new FixedDamageArrowEntity(world, shooter, itemStack, stack, critical, 3 + shooter.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) + bonus, null);
                    this.shoot(shooter, doubleShotArrow, j, speed * 2, doubleShotArrow.hasNoGravity() ? 0 : divergence, k, target);
                    firstShot = true;
                }
                ProjectileEntity projectileEntity = new FixedDamageArrowEntity(world, shooter, itemStack, stack, critical, 3 + shooter.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) + bonus, doubleShotArrow, firstShot);
                if(passiveAbility.equals(PASSIVE_DEADEYE)) projectileEntity.setNoGravity(true);
                this.shoot(shooter, projectileEntity, j, speed * 2, projectileEntity.hasNoGravity() ? 0 : divergence, k, target);
                world.spawnEntity(projectileEntity);
            }
        }
    }

    public static float getPullProgress(int useTicks) {
        float f = (float)useTicks / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 2.0F) {
            f = 2.0F;
        }

        return f;
    }

    @Override
    public Predicate<ItemStack> getProjectiles() {
        return DOUBLE_BOW_PROJECTILES;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        NbtCompound compound = this.getCompoundOrDefault(stack);
        if(entity instanceof PlayerEntity player && compound.getString(EffectiveWeapons.METER_ABILITY).equals(METER_HEROIC_SPIRIT)){
            int charge = compound.getInt(CURRENT_CHARGE);
            if(player.isOnGround() && charge < MAX_CHARGE){
                charge++;
                compound.putInt(CURRENT_CHARGE, charge);
                stack.setDamage(1001 - (5 * charge));
                stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(compound));
            }
            if(selected){
                if(player.isUsingItem() && !player.isOnGround() && charge > 0) {
                    charge -= 2;
                    if(charge < 0) charge = 0;
                    compound.putInt(this.getItemChargeId(), charge);
                    stack.setDamage(1001 - (5 * charge));
                    stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(compound));

                    AttributeModifiersComponent attributeModifiersComponent = stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
                    AttributeModifiersComponent.Entry lowGravityEntry = new AttributeModifiersComponent.Entry(EntityAttributes.GENERIC_GRAVITY,
                            new EntityAttributeModifier(Identifier.of(EffectiveWeapons.MOD_ID, "effectiveweapons_heroic_spirit_slowdown"),
                                    -0.9375, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), AttributeModifierSlot.HAND);

                    if (!attributeModifiersComponent.modifiers().contains(lowGravityEntry)) {
                        attributeModifiersComponent = attributeModifiersComponent.with(EntityAttributes.GENERIC_GRAVITY,
                                new EntityAttributeModifier(Identifier.of(EffectiveWeapons.MOD_ID, "effectiveweapons_heroic_spirit_slowdown"),
                                        -0.9375, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), AttributeModifierSlot.HAND);
                        attributeModifiersComponent = attributeModifiersComponent.with(EntityAttributes.GENERIC_MOVEMENT_SPEED,
                                new EntityAttributeModifier(Identifier.of(EffectiveWeapons.MOD_ID, "effectiveweapons_heroic_spirit_slowdown_speed"),
                                        -0.5, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), AttributeModifierSlot.HAND);

                        stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, attributeModifiersComponent);
                        Vec3d velocity = player.getVelocity();
                        if(velocity.getY() < 0) {
                            player.setVelocity(velocity.multiply(1, 0.005, 1));
                        }
                        else player.setVelocity(velocity.multiply(1, 1.5, 1));
                    }
                }
                else {
                    stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, this.getDefaultAttributeModifiers(stack));
                }
            }
            else {
                stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, this.getDefaultAttributeModifiers(stack));
            }
        }
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        NbtCompound compound = this.getCompoundOrDefault(stack);
        String meterAbility = compound.getString(EffectiveWeapons.METER_ABILITY);
        int maxDamage = stack.getMaxDamage();
        float f = Math.max(0.0F, ((float)maxDamage - (float)stack.getDamage()) / (float)maxDamage);

        return switch (meterAbility) {
            case METER_VAMPIRISM -> EffectiveWeapons.getColorFromGradient( 0x9C1717, 0xC94b4b, f);
            case METER_BURST_IMPACT -> EffectiveWeapons.getColorFromGradient( 0x9C4E17, 0xC9714B,f);
            case METER_HEROIC_SPIRIT -> f <= 0.33f ? 0xFF4829 : 0x00f54A;
            default -> 0xFFFFFF;
        };
    }

    @Override
    public boolean allowComponentsUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        boolean controlHeld = Screen.hasControlDown();
        String passiveAbility, meterAbility;

        NbtCompound compound = this.getCompoundOrDefault(stack);
        passiveAbility = compound.getString(EffectiveWeapons.PASSIVE_ABILITY);
        meterAbility = compound.getString(EffectiveWeapons.METER_ABILITY);

        if(controlHeld){
            tooltip.add(Text.translatable("tooltip.double_bow").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
            tooltip.add(Text.translatable("tooltip.double_bow_cont").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
            tooltip.add(Text.translatable("tooltip.double_bow_cont_part_two").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
            tooltip.add(Text.translatable("tooltip.double_bow_cont_part_three").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
            tooltip.add(Text.translatable("tooltip.double_bow_damage").formatted(Formatting.ITALIC).formatted(Formatting.BLUE));
            this.buildCustomizationTooltip(tooltip, passiveAbility, meterAbility);
            if(!meterAbility.equals(EffectiveWeapons.METER_NONE))
                tooltip.add(Text.translatable( meterAbility.equals(METER_HEROIC_SPIRIT) ? "tooltip.air_meter" : "tooltip.auto_meter").formatted(Formatting.ITALIC).formatted(Formatting.DARK_PURPLE));
            tooltip.add(Text.translatable("tooltip.attuned_customization_enabled").formatted(Formatting.ITALIC).formatted(Formatting.BLUE));
        }
        else{
            tooltip.add(Text.translatable("tooltip.double_bow_summary").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
            this.buildCustomizationTooltip(tooltip, passiveAbility, meterAbility);
            tooltip.add(Text.translatable("tooltip.more_info").formatted(Formatting.ITALIC).formatted(Formatting.BLUE));
        }
        super.appendTooltip(stack, context, tooltip, type);
    }

    @Override
    public ArrayList<String> getPossibleAttunedCustomizations() {
        ArrayList<String> customizations = new ArrayList<>();
        customizations.add(PASSIVE_DEADEYE);
        customizations.add(PASSIVE_DOUBLE_SHOT);
        customizations.add(PASSIVE_FIRM_STANCE);
        customizations.add(METER_HEROIC_SPIRIT);
        customizations.add(METER_BURST_IMPACT);
        customizations.add(METER_VAMPIRISM);
        return customizations;
    }

    @Override
    public ArrayList<String> getPossibleAttunedPassiveCustomizations() {
        ArrayList<String> customizations = new ArrayList<>();
        customizations.add(PASSIVE_DEADEYE);
        customizations.add(PASSIVE_DOUBLE_SHOT);
        customizations.add(PASSIVE_FIRM_STANCE);
        return customizations;
    }

    @Override
    public ArrayList<String> getPossibleAttunedMeterCustomizations() {
        ArrayList<String> customizations = new ArrayList<>();
        customizations.add(METER_HEROIC_SPIRIT);
        customizations.add(METER_BURST_IMPACT);
        customizations.add(METER_VAMPIRISM);
        return customizations;
    }

    @Override
    public AttributeModifiersComponent getDefaultAttributeModifiers() {
        return AttributeModifiersComponent.DEFAULT;
    }

    protected AttributeModifiersComponent getDefaultAttributeModifiers(ItemStack stack){
        NbtCompound compound = this.getCompoundOrDefault(stack);
        if(compound.getString(EffectiveWeapons.PASSIVE_ABILITY).equals(PASSIVE_FIRM_STANCE)){
            AttributeModifiersComponent attributeModifiersComponent = this.getDefaultAttributeModifiers();
            attributeModifiersComponent = attributeModifiersComponent.with(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,
                    new EntityAttributeModifier(Identifier.of(EffectiveWeapons.MOD_ID, "effectiveweapons_firm_stance"),
                            3, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.HAND);

            attributeModifiersComponent = attributeModifiersComponent.with(EntityAttributes.GENERIC_MOVEMENT_SPEED,
                    new EntityAttributeModifier(Identifier.of(EffectiveWeapons.MOD_ID, "effectiveweapons_firm_stance_slowness"), -0.75, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
                    AttributeModifierSlot.HAND);

            return attributeModifiersComponent;
        }
        return this.getDefaultAttributeModifiers();
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

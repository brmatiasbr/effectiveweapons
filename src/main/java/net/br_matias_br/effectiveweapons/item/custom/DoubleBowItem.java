package net.br_matias_br.effectiveweapons.item.custom;

import net.br_matias_br.effectiveweapons.entity.EffectiveWeaponsDamageSources;
import net.br_matias_br.effectiveweapons.entity.custom.FixedDamageArrowEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class DoubleBowItem extends BowItem {
    public DoubleBowItem(Settings settings) {
        super(settings);
    }
    public static final Predicate<ItemStack> DOUBLE_BOW_PROJECTILES = stack -> stack.isOf(Items.ARROW);

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        int statusEffects = 0;
        Collection<StatusEffectInstance> statusEffectsActive = user.getStatusEffects();
        LinkedList<StatusEffectInstance> statusEffectsToClear = new LinkedList<>();
        if(!statusEffectsActive.isEmpty()) for(StatusEffectInstance effectInstance : statusEffectsActive){
            if(effectInstance.getEffectType().value().isBeneficial()){
                statusEffects++;
                statusEffectsToClear.add(effectInstance);
            }
        }

        if(!user.isOnGround() && user.isSprinting() && statusEffects > 0){
            double boost = statusEffects * 0.3;
            user.setVelocity(user.getVelocity().getX(), user.getVelocity().getY() + boost, user.getVelocity().getZ());
            for(StatusEffectInstance statusEffectInstance : statusEffectsToClear){
                if(world.getRandom().nextBoolean()){
                    user.removeStatusEffect(statusEffectInstance.getEffectType());
                    if(statusEffectInstance.getAmplifier() > 0){
                        user.addStatusEffect(
                                new StatusEffectInstance(statusEffectInstance.getEffectType(),
                                    statusEffectInstance.getDuration(), statusEffectInstance.getAmplifier() - 1,
                                    statusEffectInstance.isAmbient(), statusEffectInstance.shouldShowParticles(),
                                    statusEffectInstance.shouldShowIcon()));
                    }
                }
            }
        }
        return super.use(world, user, hand);
    }

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

                    world.playSound(
                            null,
                            playerEntity.getX(),
                            playerEntity.getY(),
                            playerEntity.getZ(),
                            SoundEvents.ENTITY_ARROW_SHOOT,
                            SoundCategory.PLAYERS,
                            1.0F,
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

        for (int j = 0; j < projectiles.size(); j++) {
            ItemStack itemStack = (ItemStack)projectiles.get(j);
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
                            EffectiveWeaponsDamageSources.PACT_AXE_RECOIL_DAMAGE), shooter.getMaxHealth() * 0.249f);
                }
                ProjectileEntity projectileEntity = new FixedDamageArrowEntity(world, shooter, stack, itemStack, critical, 3 + shooter.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) + bonus);
                this.shoot(shooter, projectileEntity, j, speed * 2, divergence, k, target);
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
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        boolean controlHeld = Screen.hasControlDown();

        if(controlHeld){
            tooltip.add(Text.translatable("tooltip.double_bow").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GREEN));
            tooltip.add(Text.translatable("tooltip.double_bow_cont").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GREEN));
            tooltip.add(Text.translatable("tooltip.double_bow_cont_part_two").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GREEN));
            tooltip.add(Text.translatable("tooltip.double_bow_cont_part_three").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GREEN));
            tooltip.add(Text.translatable("tooltip.double_bow_damage").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
        }
        else{
            tooltip.add(Text.translatable("tooltip.double_bow_summary").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GREEN));
            tooltip.add(Text.translatable("tooltip.more_info").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
        }
        super.appendTooltip(stack, context, tooltip, type);
    }
}

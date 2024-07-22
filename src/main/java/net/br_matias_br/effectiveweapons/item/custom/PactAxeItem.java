package net.br_matias_br.effectiveweapons.item.custom;

import net.br_matias_br.effectiveweapons.entity.EffectiveWeaponsDamageSources;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.random.Random;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class PactAxeItem extends AxeItem {
    public PactAxeItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        Random random = attacker.getWorld().getRandom();
        int backfireChance = 20 - (int) Math.min(target.getArmor() * 0.75f, 20);
        boolean backfire = random.nextInt(100) < backfireChance;
        Collection<StatusEffectInstance> userStatusEffects = attacker.getStatusEffects();

        if(backfire){
            stack.setDamage(1);
            if(!userStatusEffects.isEmpty()){
                LinkedList<StatusEffectInstance> statusEffectsToSacrifice = new LinkedList<>();
                for(StatusEffectInstance statusEffect : userStatusEffects){
                    if(statusEffect.getEffectType().value().isBeneficial()){
                        statusEffectsToSacrifice.add(statusEffect);
                    }
                }
                attacker.removeStatusEffect(statusEffectsToSacrifice.get(random.nextInt(statusEffectsToSacrifice.size())).getEffectType());
            }
            else attacker.damage(EffectiveWeaponsDamageSources.of(attacker.getWorld(),
                EffectiveWeaponsDamageSources.PACT_AXE_RECOIL_DAMAGE), (float) attacker.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) * 2f);
        }
        else{
            stack.setDamage(2);
            target.damage(target.getDamageSources().mobAttack(attacker), (float) attacker.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) * 2f);
        }

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
    public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        boolean controlHeld = Screen.hasControlDown();

        if(controlHeld){
            tooltip.add(Text.translatable("tooltip.pact_axe").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GREEN));
            tooltip.add(Text.translatable("tooltip.pact_axe_cont").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GREEN));
            tooltip.add(Text.translatable("tooltip.pact_axe_cont_part_two").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GREEN));
            tooltip.add(Text.translatable("tooltip.pact_axe_cont_part_three").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GREEN));
            tooltip.add(Text.translatable("tooltip.pact_axe_cont_part_four").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GREEN));
            tooltip.add(Text.translatable("tooltip.pact_axe_backfire").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
        }
        else{
            tooltip.add(Text.translatable("tooltip.pact_axe_summary").formatted(Formatting.ITALIC).formatted(Formatting.DARK_GREEN));
            tooltip.add(Text.translatable("tooltip.more_info").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
        }
        super.appendTooltip(stack, context, tooltip, type);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return stack.getDamage() == 1 ? 0x810202: 0x1d8102;
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        stack.set(DataComponentTypes.DAMAGE, 2);
        return stack;
    }
}

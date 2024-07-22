package net.br_matias_br.effectiveweapons.item.custom;

import net.br_matias_br.effectiveweapons.EffectiveWeapons;
import net.minecraft.block.BlockState;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DaggerItem extends ToolItem {
    public DaggerItem(ToolMaterial material, Settings settings) {
        super(material, settings);
    }

    public static AttributeModifiersComponent createAttributeModifiers(ToolMaterial material, float baseAttackDamage, float attackSpeed) {
        return AttributeModifiersComponent.builder()
                .add(
                        EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(
                                BASE_ATTACK_DAMAGE_MODIFIER_ID, (double)(baseAttackDamage + material.getAttackDamage()), EntityAttributeModifier.Operation.ADD_VALUE
                        ),
                        AttributeModifierSlot.MAINHAND
                )
                .add(
                        EntityAttributes.GENERIC_ATTACK_SPEED,
                        new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, (double)attackSpeed, EntityAttributeModifier.Operation.ADD_VALUE),
                        AttributeModifierSlot.MAINHAND
                )
                .add(
                        EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE,
                        new EntityAttributeModifier(Identifier.of(EffectiveWeapons.MOD_ID, "dagger_entity_range"), -1.0, EntityAttributeModifier.Operation.ADD_VALUE),
                        AttributeModifierSlot.HAND
                )
                .add(
                        EntityAttributes.PLAYER_SNEAKING_SPEED,
                        new EntityAttributeModifier(Identifier.of(EffectiveWeapons.MOD_ID, "dagger_sneak_speed"), 0.3, EntityAttributeModifier.Operation.ADD_VALUE),
                        AttributeModifierSlot.HAND
                )
                .add(
                        EntityAttributes.GENERIC_SAFE_FALL_DISTANCE,
                        new EntityAttributeModifier(Identifier.of(EffectiveWeapons.MOD_ID, "dagger_safe_fall_distance"), 3, EntityAttributeModifier.Operation.ADD_VALUE),
                        AttributeModifierSlot.HAND
                )
                .build();
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        target.timeUntilRegen = 0;
        return super.postHit(stack, target, attacker);
    }

    @Override
    public void postDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.postDamageEntity(stack, target, attacker);
        stack.setDamage(stack.getDamage() + 1);
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        stack.setDamage(stack.getDamage() + 2);
        return super.postMine(stack, world, state, pos, miner);
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return !miner.isCreative();
    }
}

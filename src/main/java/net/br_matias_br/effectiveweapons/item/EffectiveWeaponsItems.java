package net.br_matias_br.effectiveweapons.item;

import net.br_matias_br.effectiveweapons.EffectiveWeapons;
import net.br_matias_br.effectiveweapons.item.custom.*;
import net.br_matias_br.effectiveweapons.item.material.EffectiveArmorMaterials;
import net.br_matias_br.effectiveweapons.item.material.EffectiveWeaponMaterial;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class EffectiveWeaponsItems {
    public static final Item BLANK_IRON_SMITHING_TEMPLATE = registerItem("blank_iron_smithing_template", new CustomSmithingTemplate(new Item.Settings(), CustomSmithingTemplate.CustomSmithingTemplateType.IRON));
    public static final Item SPIRALING_SWORD_SMITHING_TEMPLATE = registerItem("spiraling_sword_smithing_template", new CustomSmithingTemplate(new Item.Settings(), CustomSmithingTemplate.CustomSmithingTemplateType.SPIRALING_SWORD));
    public static final Item SPIRALING_SWORD = registerItem("spiraling_sword",
            new SpiralingSwordItem(EffectiveWeaponMaterial.INSTANCE, new Item.Settings().attributeModifiers(SwordItem.createAttributeModifiers(EffectiveWeaponMaterial.INSTANCE, 5, -2.4f)).rarity(Rarity.RARE)));
    public static final Item IRON_DAGGER = registerItem("iron_dagger",
            new DaggerItem(ToolMaterials.IRON, new Item.Settings().attributeModifiers(DaggerItem.createAttributeModifiers(ToolMaterials.IRON, 0, -1f))));
    public static final Item ROGUE_DAGGER_SMITHING_TEMPLATE = registerItem("rogue_dagger_smithing_template", new CustomSmithingTemplate(new Item.Settings(), CustomSmithingTemplate.CustomSmithingTemplateType.ROGUE_DAGGER));
    public static final Item ROGUE_DAGGER = registerItem("rogue_dagger",
            new RogueDaggerItem(EffectiveWeaponMaterial.INSTANCE, new Item.Settings().attributeModifiers(DaggerItem.createAttributeModifiers(EffectiveWeaponMaterial.INSTANCE, 2.2f, -1f)).rarity(Rarity.RARE)));
    public static final Item IRON_LANCE = registerItem("iron_lance",
            new LanceItem(ToolMaterials.IRON, new Item.Settings().attributeModifiers(LanceItem.createAttributeModifiers(ToolMaterials.IRON, 5f, -2.9f))));
    public static final Item BLESSED_LANCE_SMITHING_TEMPLATE = registerItem("blessed_lance_smithing_template", new CustomSmithingTemplate(new Item.Settings(), CustomSmithingTemplate.CustomSmithingTemplateType.BLESSED_LANCE));
    public static final Item BLESSED_LANCE = registerItem("blessed_lance",
            new BlessedLanceItem(EffectiveWeaponMaterial.INSTANCE, new Item.Settings().attributeModifiers(LanceItem.createAttributeModifiers(EffectiveWeaponMaterial.INSTANCE, 8f, -2.7f)).rarity(Rarity.RARE)));
    public static final Item PACT_AXE_SMITHING_TEMPLATE = registerItem("pact_axe_smithing_template", new CustomSmithingTemplate(new Item.Settings(), CustomSmithingTemplate.CustomSmithingTemplateType.PACT_AXE));
    public static final Item PACT_AXE = registerItem("pact_axe",
            new PactAxeItem(EffectiveWeaponMaterial.INSTANCE, new Item.Settings().attributeModifiers(AxeItem.createAttributeModifiers(EffectiveWeaponMaterial.INSTANCE, 12f, -2.9f)).rarity(Rarity.RARE)));
    public static final Item DEKAJA_TOME_SMITHING_TEMPLATE = registerItem("dekaja_tome_smithing_template", new CustomSmithingTemplate(new Item.Settings(), CustomSmithingTemplate.CustomSmithingTemplateType.DEKAJA_TOME));
    public static final Item DEKAJA_TOME = registerItem("dekaja_tome", new DekajaTomeItem(new Item.Settings().maxCount(1).rarity(Rarity.RARE)));
    public static final Item DOUBLE_BOW_SMITHING_TEMPLATE = registerItem("double_bow_smithing_template", new CustomSmithingTemplate(new Item.Settings(), CustomSmithingTemplate.CustomSmithingTemplateType.DOUBLE_BOW));
    public static final Item DOUBLE_BOW = registerItem("double_bow", new DoubleBowItem(new Item.Settings().maxCount(1).rarity(Rarity.RARE).maxDamage(1001)));
    public static final Item CLOSE_SHIELD_SMITHING_TEMPLATE = registerItem("close_shield_smithing_template", new CustomSmithingTemplate(new Item.Settings(), CustomSmithingTemplate.CustomSmithingTemplateType.CLOSE_SHIELD));
    public static final Item CLOSE_SHIELD = registerItem("close_shield", new LightShieldItem(new Item.Settings().maxCount(1).rarity(Rarity.RARE).maxDamage(1001), true));
    public static final Item DISTANT_SHIELD_SMITHING_TEMPLATE = registerItem("distant_shield_smithing_template", new CustomSmithingTemplate(new Item.Settings(), CustomSmithingTemplate.CustomSmithingTemplateType.DISTANT_SHIELD));
    public static final Item DISTANT_SHIELD = registerItem("distant_shield", new LightShieldItem(new Item.Settings().maxCount(1).rarity(Rarity.RARE).maxDamage(1001), false));
    public static final Item LAPIS_CIRCLET = registerItem("lapis_circlet", new CircletArmorItem(EffectiveArmorMaterials.CIRCLET, ArmorItem.Type.HELMET , new Item.Settings().maxCount(1).rarity(Rarity.RARE).equipmentSlot(((entity, stack) -> stack.getDamage() > 0 ? EquipmentSlot.FEET : EquipmentSlot.HEAD)).maxDamage(1001)));
    public static final Item CRUSHED_NETHERRACK = registerItem("crushed_netherrack", new Item(new Item.Settings()));


    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(EffectiveWeapons.MOD_ID, name), item);
    }
    public static void registerItems() {
        EffectiveWeapons.LOGGER.info("Registering items for " + EffectiveWeapons.MOD_ID);
    }
}

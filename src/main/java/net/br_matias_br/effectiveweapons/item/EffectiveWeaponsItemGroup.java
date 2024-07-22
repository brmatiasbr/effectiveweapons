package net.br_matias_br.effectiveweapons.item;

import net.br_matias_br.effectiveweapons.EffectiveWeapons;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class EffectiveWeaponsItemGroup {
    public static final ItemGroup EFFECTIVE_WEAPONS = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(EffectiveWeapons.MOD_ID, "effectiveweapons"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.effectiveweapons"))
                    .icon(() -> new ItemStack(EffectiveWeaponsItems.SPIRALING_SWORD)).entries((displayContext, entries) -> {
                        entries.add(EffectiveWeaponsItems.BLANK_IRON_SMITHING_TEMPLATE);
                        entries.add(EffectiveWeaponsItems.SPIRALING_SWORD_SMITHING_TEMPLATE);
                        entries.add(EffectiveWeaponsItems.SPIRALING_SWORD);
                        entries.add(EffectiveWeaponsItems.IRON_DAGGER);
                        entries.add(EffectiveWeaponsItems.ROGUE_DAGGER_SMITHING_TEMPLATE);
                        entries.add(EffectiveWeaponsItems.ROGUE_DAGGER);
                        entries.add(EffectiveWeaponsItems.IRON_LANCE);
                        entries.add(EffectiveWeaponsItems.BLESSED_LANCE_SMITHING_TEMPLATE);
                        entries.add(EffectiveWeaponsItems.BLESSED_LANCE);
                        entries.add(EffectiveWeaponsItems.DEKAJA_TOME_SMITHING_TEMPLATE);
                        entries.add(EffectiveWeaponsItems.DEKAJA_TOME);
                        entries.add(EffectiveWeaponsItems.PACT_AXE_SMITHING_TEMPLATE);
                        entries.add(EffectiveWeaponsItems.PACT_AXE);
                        entries.add(EffectiveWeaponsItems.DOUBLE_BOW_SMITHING_TEMPLATE);
                        entries.add(EffectiveWeaponsItems.DOUBLE_BOW);
                        entries.add(EffectiveWeaponsItems.LAPIS_CIRCLET);
                    }).build());

    public static void registerItemGroups(){
        EffectiveWeapons.LOGGER.info("Registering Item Groups for " + EffectiveWeapons.MOD_ID);
    }
}

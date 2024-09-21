package net.br_matias_br.effectiveweapons.effect;

import net.br_matias_br.effectiveweapons.EffectiveWeapons;
import net.br_matias_br.effectiveweapons.item.EffectiveWeaponsItems;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class EffectiveWeaponsPotions {
    public static Potion FIRE_GUARD_POTION = Registry.register(Registries.POTION,
            Identifier.of(EffectiveWeapons.MOD_ID, "fire_guard_potion"),
            new Potion(new StatusEffectInstance(EffectiveWeaponsEffects.FIRE_GUARD_REGISTRY_ENTRY, 1800, 0)));
    public static Potion FIRE_GUARD_POTION_LONG = Registry.register(Registries.POTION,
            Identifier.of(EffectiveWeapons.MOD_ID, "fire_guard_potion_long"),
            new Potion(new StatusEffectInstance(EffectiveWeaponsEffects.FIRE_GUARD_REGISTRY_ENTRY, 4800, 0)));
    public static void registerPotions(){
        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
            builder.registerPotionRecipe(
                    Potions.MUNDANE, EffectiveWeaponsItems.CRUSHED_NETHERRACK, Registries.POTION.getEntry(FIRE_GUARD_POTION)
            );
        });

        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
            builder.registerPotionRecipe(
                    Registries.POTION.getEntry(FIRE_GUARD_POTION), Items.REDSTONE, Registries.POTION.getEntry(FIRE_GUARD_POTION_LONG)
            );
        });
    }
}

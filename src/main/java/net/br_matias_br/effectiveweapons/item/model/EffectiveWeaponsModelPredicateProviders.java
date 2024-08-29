package net.br_matias_br.effectiveweapons.item.model;

import net.br_matias_br.effectiveweapons.EffectiveWeapons;
import net.br_matias_br.effectiveweapons.item.EffectiveWeaponsItems;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;

public class EffectiveWeaponsModelPredicateProviders {
    public static void registerModelPredicateProviders() {
        ModelPredicateProviderRegistry.register(EffectiveWeaponsItems.DOUBLE_BOW, Identifier.ofVanilla("pull"), (itemStack, clientWorld, livingEntity, seed) -> {
            if (livingEntity == null) {
                return 0.0F;
            }
            return livingEntity.getActiveItem() != itemStack ? 0.0F : (itemStack.getMaxUseTime(livingEntity) - livingEntity.getItemUseTimeLeft()) / 20.0F;
        });

        ModelPredicateProviderRegistry.register(EffectiveWeaponsItems.DOUBLE_BOW, Identifier.ofVanilla("pulling"), (itemStack, clientWorld, livingEntity, seed) -> {
            if (livingEntity == null) {
                return 0.0F;
            }
            return livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F;
        });

        ModelPredicateProviderRegistry.register(EffectiveWeaponsItems.DOUBLE_BOW, Identifier.of(EffectiveWeapons.MOD_ID,"max_charge"), (itemStack, clientWorld, livingEntity, seed) -> {
            if (livingEntity == null) {
                return 0.0F;
            }
            if(livingEntity.getActiveItem() != itemStack){
                return 0.0f;
            }
            return (itemStack.getMaxUseTime(livingEntity) - livingEntity.getItemUseTimeLeft()) / 20.0F >= 2.0 ? 1.0f : 0.0f;
        });

        ModelPredicateProviderRegistry.register(EffectiveWeaponsItems.CLOSE_SHIELD, Identifier.ofVanilla("blocking"),
                (itemStack, clientWorld, livingEntity, seed) -> livingEntity != null &&
                        livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F);

        ModelPredicateProviderRegistry.register(EffectiveWeaponsItems.DISTANT_SHIELD, Identifier.ofVanilla("blocking"),
                (itemStack, clientWorld, livingEntity, seed) -> livingEntity != null &&
                        livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F);


        ModelPredicateProviderRegistry.register(EffectiveWeaponsItems.BLESSED_LANCE, Identifier.ofVanilla("throwing"),
                (itemStack, clientWorld, livingEntity, seed) -> {
            if (livingEntity == null) {
                return 0.0F;
            }
            return livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F;
        });
    }
}

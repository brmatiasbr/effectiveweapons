package net.br_matias_br.effectiveweapons.screen;

import net.br_matias_br.effectiveweapons.EffectiveWeapons;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class EffectiveWeaponsScreenHandlers {
    public static final ScreenHandlerType<AttuningTableScreenHandler> ATTUNING_TABLE_SCREEN_HANDLER_TYPE =
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(EffectiveWeapons.MOD_ID, "attuning_table_screen_handler_type"),
                    new ScreenHandlerType<>(AttuningTableScreenHandler::new, FeatureSet.empty()));
}

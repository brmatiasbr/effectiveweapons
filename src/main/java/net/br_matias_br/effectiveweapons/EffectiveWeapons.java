package net.br_matias_br.effectiveweapons;

import net.br_matias_br.effectiveweapons.client.particle.EffectiveWeaponsParticles;
import net.br_matias_br.effectiveweapons.entity.EffectiveWeaponsDamageSources;
import net.br_matias_br.effectiveweapons.entity.EffectiveWeaponsEntities;
import net.br_matias_br.effectiveweapons.item.EffectiveWeaponsItemGroup;
import net.br_matias_br.effectiveweapons.item.EffectiveWeaponsItems;
import net.br_matias_br.effectiveweapons.item.model.EffectiveWeaponsModelPredicateProviders;
import net.br_matias_br.effectiveweapons.networking.EffectiveWeaponsNetworking;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EffectiveWeapons implements ModInitializer {
    public static final String MOD_ID = "effectiveweapons";

    public static final Logger LOGGER = LoggerFactory.getLogger("effectiveweapons");
    @Override
    public void onInitialize() {
        LOGGER.info("Initializing EffectiveWeapons");

        EffectiveWeaponsItems.registerItems();
        EffectiveWeaponsItemGroup.registerItemGroups();

        EffectiveWeaponsNetworking.registerCustomPayloads();
        EffectiveWeaponsNetworking.registerServerReceivers();
        EffectiveWeaponsEntities.registerEntities();
        EffectiveWeaponsDamageSources.registerDamageSources();
        EffectiveWeaponsModelPredicateProviders.registerModelPredicateProviders();
        EffectiveWeaponsParticles.registerParticles();
    }


    public static int getColorFromGradient(int colorHex1, int colorHex2, float pos){
        int r1, g1, b1, r2, g2, b2, rF, gF, bF;
        r1 = colorHex1 >> 16;
        g1 = (colorHex1 >> 8) & 0xFF;
        b1 = colorHex1 & 0xFF;

        r2 = colorHex2 >> 16;
        g2 = (colorHex2 >> 8) & 0xFF;
        b2 = colorHex2 & 0xFF;

        rF = (int)((r1 * pos) + (r2 * (1 - pos)));
        gF = (int)((g1 * pos) + (g2 * (1 - pos)));
        bF = (int)((b1 * pos) + (b2 * (1 - pos)));

        return (rF << 16) | (gF << 8) | (bF);
    }
}

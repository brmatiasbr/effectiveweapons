package net.br_matias_br.effectiveweapons.client;

import net.br_matias_br.effectiveweapons.client.particle.CriticalHitParticle;
import net.br_matias_br.effectiveweapons.client.particle.EffectiveWeaponsParticles;
import net.br_matias_br.effectiveweapons.entity.EffectiveWeaponsEntities;
import net.br_matias_br.effectiveweapons.entity.client.AreaNoEffectCloudRenderer;
import net.br_matias_br.effectiveweapons.entity.client.DekajaEffectEntityRenderer;
import net.br_matias_br.effectiveweapons.entity.client.FixedDamageArrowRenderer;
import net.br_matias_br.effectiveweapons.item.EffectiveWeaponsItems;
import net.br_matias_br.effectiveweapons.item.model.LapisCircletModel;
import net.br_matias_br.effectiveweapons.item.render.LapisCircletRenderer;
import net.br_matias_br.effectiveweapons.networking.EffectiveWeaponsNetworking;
import net.br_matias_br.effectiveweapons.screen.AttuningTableScreen;
import net.br_matias_br.effectiveweapons.screen.EffectiveWeaponsScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class EffectiveWeaponsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EffectiveWeaponsNetworking.registerClientReceivers();
        EntityRendererRegistry.register(EffectiveWeaponsEntities.DEKAJA_EFFECT_ENTITY_TYPE, DekajaEffectEntityRenderer::new);
        EntityRendererRegistry.register(EffectiveWeaponsEntities.AREA_NO_EFFECT_CLOUD_ENTITY_TYPE, AreaNoEffectCloudRenderer::new);
        EntityRendererRegistry.register(EffectiveWeaponsEntities.FIXED_DAMAGE_ARROW_ENTITY, FixedDamageArrowRenderer::new);


        ArmorRenderer.register(new LapisCircletRenderer(), EffectiveWeaponsItems.LAPIS_CIRCLET);
        EntityModelLayerRegistry.registerModelLayer(EffectiveWeaponsModelLayers.LAPIS_CIRCLET, LapisCircletModel::getTexturedModelData);

        ParticleFactoryRegistry.getInstance().register(EffectiveWeaponsParticles.DOUBLE_BOW_CRIT, CriticalHitParticle.Factory::new);

        HandledScreens.register(EffectiveWeaponsScreenHandlers.ATTUNING_TABLE_SCREEN_HANDLER_TYPE, AttuningTableScreen::new);
    }
}

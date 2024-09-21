package net.br_matias_br.effectiveweapons.networking;

import net.br_matias_br.effectiveweapons.EffectiveWeapons;
import net.br_matias_br.effectiveweapons.entity.custom.AreaNoEffectCloudEntity;
import net.br_matias_br.effectiveweapons.entity.custom.BladeBeamEntity;
import net.br_matias_br.effectiveweapons.entity.custom.DekajaEffectEntity;
import net.br_matias_br.effectiveweapons.item.custom.AttunableItem;
import net.br_matias_br.effectiveweapons.screen.AttuningTableScreenHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public class EffectiveWeaponsNetworking {
    public static final Identifier PARTICLE_REQUEST_PACKET_ID = Identifier.of(EffectiveWeapons.MOD_ID, "particle_request");
    public static final Identifier ENTITY_SYNC_PACKET_ID = Identifier.of(EffectiveWeapons.MOD_ID, "entity_sync");
    public static final Identifier ITEM_MODIFICATION_PACKET_ID = Identifier.of(EffectiveWeapons.MOD_ID, "item_modification");
    public static final Identifier SWING_ACTION_PACKET_ID = Identifier.of(EffectiveWeapons.MOD_ID, "swing_action");

    public static void registerClientReceivers(){
        ClientPlayNetworking.registerGlobalReceiver(ParticleRequestPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                Entity entity = context.player().getWorld().getEntityById(payload.entityId());
                if(entity != null){
                    ParticleEvents.particleEvent((ClientWorld) context.player().getWorld(), (int) payload.particleEventType(), entity);
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(EntitySynchronizationPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                Entity entity1 = context.player().getWorld().getEntityById(payload.entityId1());

                if(entity1 instanceof DekajaEffectEntity dekajaEffect){
                    boolean noOwner = payload.information2() == 0;
                    Entity entity2 = context.player().getWorld().getEntityById((int) payload.information2());
                    if(entity2 != null || noOwner){
                        if(!noOwner){
                            dekajaEffect.setSummoner(entity2);
                        }
                    }
                }
                if(entity1 instanceof AreaNoEffectCloudEntity areaNoEffectCloud){
                    if(payload.type() == 0){
                        areaNoEffectCloud.setDuration((int) payload.information2() - 1);
                    }
                    else areaNoEffectCloud.setOwner(context.player().getWorld().getEntityById((int) payload.information2()));
                }

                if(entity1 instanceof BladeBeamEntity bladeBeam){
                    boolean noOwner = payload.information2() == 0;
                    Entity entity2 = context.player().getWorld().getEntityById((int) payload.information2());
                    if(entity2 != null || noOwner){
                        if(!noOwner){
                            bladeBeam.setSummoner(entity2);
                        }
                    }
                }
            });
        });
    }

    public static void registerServerReceivers(){
        ServerPlayNetworking.registerGlobalReceiver(EntitySynchronizationPayload.ID, (payload, context) -> {
            context.server().execute(() ->{
                Entity entity1 = context.player().getWorld().getEntityById(payload.entityId1());
                if(entity1 != null){
                    if(entity1 instanceof DekajaEffectEntity dekajaEffect){
                        int entityId2 = 0;
                        if(dekajaEffect.getSummoner() != null) entityId2 = dekajaEffect.getSummoner().getId();
                        ServerPlayNetworking.send(context.player(), new EntitySynchronizationPayload(payload.entityId1(), entityId2, 0));
                    }
                    if(entity1 instanceof AreaNoEffectCloudEntity areaNoEffectCloud){
                        if(payload.type() == 1){
                            ServerPlayNetworking.send(context.player(), new EntitySynchronizationPayload(payload.entityId1(), areaNoEffectCloud.getRemainingDuration(), 0));
                        }
                        if(payload.type() == 2){
                            int entityId2 = 0;
                            if(areaNoEffectCloud.getOwner() != null) entityId2 = areaNoEffectCloud.getOwner().getId();
                            ServerPlayNetworking.send(context.player(), new EntitySynchronizationPayload(payload.entityId1(), entityId2, 1));
                        }
                    }
                    if(entity1 instanceof BladeBeamEntity bladeBeam){
                        int entityId2 = 0;
                        if(bladeBeam.getSummoner() != null) entityId2 = bladeBeam.getSummoner().getId();
                        ServerPlayNetworking.send(context.player(), new EntitySynchronizationPayload(payload.entityId1(), entityId2, 0));
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(ItemModificationPayload.ID, (payload, context) -> {
            context.server().execute(() ->{
                if(payload.componentKey().equals("reset_charge") && context.player().getStackInHand(context.player().getActiveHand()).getItem() instanceof AttunableItem attunableItem){
                    ItemStack stack =  context.player().getStackInHand(context.player().getActiveHand());
                    NbtCompound compound = attunableItem.getCompoundOrDefault(stack);
                    int charge = 0;
                    stack.setDamage(1001);

                    compound.putInt(attunableItem.getItemChargeId(), charge);
                    NbtComponent component = NbtComponent.of(compound);
                    stack.set(DataComponentTypes.CUSTOM_DATA, component);

                    return;
                }
                ScreenHandler screenHandler = context.player().currentScreenHandler;
                if(screenHandler instanceof AttuningTableScreenHandler attuningTableScreenHandler){
                    if(!payload.revertToDefault()) {
                        attuningTableScreenHandler.writeCustomization(payload.componentKey());
                    }
                    else attuningTableScreenHandler.revertItemToDefault();
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(SwingActionPayload.ID, (payload, context) -> {
            context.server().execute(() ->{
                ItemStack stack = context.player().getMainHandStack();
                if(stack != null){
                    SwingActionsEvents.swingAction((ServerWorld) context.player().getWorld(), payload.swingActionType(), context.player());
                }
            });
        });
    }

    public static void registerCustomPayloads(){
        PayloadTypeRegistry.playS2C().register(ParticleRequestPayload.ID, ParticleRequestPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(EntitySynchronizationPayload.ID, EntitySynchronizationPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(EntitySynchronizationPayload.ID, EntitySynchronizationPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ItemModificationPayload.ID, ItemModificationPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(SwingActionPayload.ID, SwingActionPayload.CODEC);
    }
}

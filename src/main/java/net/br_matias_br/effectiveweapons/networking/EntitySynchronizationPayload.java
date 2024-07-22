package net.br_matias_br.effectiveweapons.networking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record EntitySynchronizationPayload(int entityId1, long information2, float type) implements CustomPayload {
    public static final CustomPayload.Id<EntitySynchronizationPayload> ID = new CustomPayload.Id<>(EffectiveWeaponsNetworking.ENTITY_SYNC_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, EntitySynchronizationPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, EntitySynchronizationPayload::entityId1,
            PacketCodecs.VAR_LONG, EntitySynchronizationPayload::information2,
            PacketCodecs.FLOAT, EntitySynchronizationPayload::type,
            EntitySynchronizationPayload::new);


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
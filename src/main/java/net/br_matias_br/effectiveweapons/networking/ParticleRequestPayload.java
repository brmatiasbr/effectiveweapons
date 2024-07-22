package net.br_matias_br.effectiveweapons.networking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record ParticleRequestPayload(int entityId, long particleEventType) implements CustomPayload {
    public static final CustomPayload.Id<ParticleRequestPayload> ID = new CustomPayload.Id<>(EffectiveWeaponsNetworking.PARTICLE_REQUEST_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, ParticleRequestPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, ParticleRequestPayload::entityId,
            PacketCodecs.VAR_LONG, ParticleRequestPayload::particleEventType,
            ParticleRequestPayload::new);


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class PeckPacket extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, PeckPacket> CODEC  = StreamCodec.ofMember(PeckPacket::encode, PeckPacket::new);
    public static final Type<PeckPacket> TYPE = new Type<>(HexereiUtil.getResource("peck"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    int sourceId;
    int duration;
    public PeckPacket(Entity entity) {
        this.sourceId = entity.getId();
        this.duration = 15;
    }
    public PeckPacket(Entity entity, int duration) {
        this.sourceId = entity.getId();
        this.duration = duration;
    }
    public PeckPacket(RegistryFriendlyByteBuf buf) {
        this.sourceId = buf.readInt();
        this.duration = buf.readInt();
    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(sourceId);
        buffer.writeInt(duration);
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {

        if(player.level().getEntity(sourceId) != null) {
            if((player.level().getEntity(sourceId)) instanceof CrowEntity crow) {
                crow.peck();
            }
            if((player.level().getEntity(sourceId)) instanceof OwlEntity owl) {
                owl.peckAnimation.start();
                owl.peckAnimation.activeTimer = duration;
            }
        }
    }
}
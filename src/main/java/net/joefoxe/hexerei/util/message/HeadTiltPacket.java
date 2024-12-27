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

public class HeadTiltPacket extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, HeadTiltPacket> CODEC  = StreamCodec.ofMember(HeadTiltPacket::encode, HeadTiltPacket::new);
    public static final Type<HeadTiltPacket> TYPE = new Type<>(HexereiUtil.getResource("head_tilt"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    int sourceId;
    int duration;
    float xTilt;
    float zTilt;

    public HeadTiltPacket(Entity entity, int duration, float xTilt, float zTilt) {
        this.sourceId = entity.getId();
        this.duration = duration;
        this.xTilt = xTilt;
        this.zTilt = zTilt;
    }
    public HeadTiltPacket(RegistryFriendlyByteBuf buf) {
        this.sourceId = buf.readInt();
        this.duration = buf.readInt();
        this.xTilt = buf.readFloat();
        this.zTilt = buf.readFloat();
    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(sourceId);
        buffer.writeInt(duration);
        buffer.writeFloat(xTilt);
        buffer.writeFloat(zTilt);
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {

        if(player.level().getEntity(sourceId) != null) {
            if((player.level().getEntity(sourceId)) instanceof CrowEntity crow) {
                crow.peck();
            }
            if((player.level().getEntity(sourceId)) instanceof OwlEntity owl) {
                owl.headTiltAnimation.start();
                owl.headTiltAnimation.xTiltTarget = xTilt;
                owl.headTiltAnimation.zTiltTarget = zTilt;
                owl.headTiltAnimation.activeTimer = duration;
            }
        }
    }
}
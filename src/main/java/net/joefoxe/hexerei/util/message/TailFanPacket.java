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

import java.util.Random;

public class TailFanPacket extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, TailFanPacket> CODEC  = StreamCodec.ofMember(TailFanPacket::encode, TailFanPacket::new);
    public static final Type<TailFanPacket> TYPE = new Type<>(HexereiUtil.getResource("tail_fan"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    int sourceId;
    int duration;

    public TailFanPacket(Entity entity, int duration) {
        this.sourceId = entity.getId();
        this.duration = duration;
    }
    public TailFanPacket(RegistryFriendlyByteBuf buf) {
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
                crow.tailFan = true;
                crow.tailFanTimer = 15;
                crow.tailFanTiltAngle = 20 + new Random().nextInt(20);
            }
            if((player.level().getEntity(sourceId)) instanceof OwlEntity owl) {
                owl.tailFanAnimation.start();
                owl.tailFanAnimation.activeTimer = duration;
            }
        }
    }
}
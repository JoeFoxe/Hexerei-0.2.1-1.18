package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class HeadShakePacket extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, HeadShakePacket> CODEC  = StreamCodec.ofMember(HeadShakePacket::encode, HeadShakePacket::new);
    public static final Type<HeadShakePacket> TYPE = new Type<>(HexereiUtil.getResource("head_shake"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    int sourceId;
    int duration;

    public HeadShakePacket(Entity entity, int duration) {
        this.sourceId = entity.getId();
        this.duration = duration;
    }
    public HeadShakePacket(FriendlyByteBuf buf) {
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
            if((player.level().getEntity(sourceId)) instanceof OwlEntity owl) {
                owl.headShakeAnimation.start();
                owl.headShakeAnimation.activeTimer = duration;
            }
        }
    }
}
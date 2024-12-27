package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class CrowCawPacket extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, CrowCawPacket> CODEC  = StreamCodec.ofMember(CrowCawPacket::encode, CrowCawPacket::new);
    public static final Type<CrowCawPacket> TYPE = new Type<>(HexereiUtil.getResource("crow_caw"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    int sourceId;

    public CrowCawPacket(Entity entity) {
        this.sourceId = entity.getId();
    }
    public CrowCawPacket(RegistryFriendlyByteBuf buf) {
        this.sourceId = buf.readInt();
    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(sourceId);
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {

        if(player.level().getEntity(sourceId) != null) {
            if((player.level().getEntity(sourceId)) instanceof CrowEntity crow) {
                crow.caw = true;
                crow.cawTimer = 15;
                crow.cawTiltAngle = 80;
            }
        }
    }
}
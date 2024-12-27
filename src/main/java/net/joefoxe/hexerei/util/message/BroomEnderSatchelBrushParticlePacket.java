package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public class BroomEnderSatchelBrushParticlePacket extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, BroomEnderSatchelBrushParticlePacket> CODEC  = StreamCodec.ofMember(BroomEnderSatchelBrushParticlePacket::encode, BroomEnderSatchelBrushParticlePacket::new);
    public static final CustomPacketPayload.Type<BroomEnderSatchelBrushParticlePacket> TYPE = new CustomPacketPayload.Type<>(HexereiUtil.getResource("broom_ender_satchel_particles"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    int sourceId;

    public BroomEnderSatchelBrushParticlePacket(int id) {
        this.sourceId = id;
    }
    public BroomEnderSatchelBrushParticlePacket(RegistryFriendlyByteBuf buf) {
        this.sourceId = buf.readInt();
    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(sourceId);
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        if(minecraft.level.getEntity(sourceId) instanceof BroomEntity broom) {
            broom.transferBrushParticles();
        }
    }
}
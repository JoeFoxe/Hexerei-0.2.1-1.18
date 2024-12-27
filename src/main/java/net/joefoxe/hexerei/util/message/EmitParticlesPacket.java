package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.tileentity.MixingCauldronTile;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public class EmitParticlesPacket extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, EmitParticlesPacket> CODEC  = StreamCodec.ofMember(EmitParticlesPacket::encode, EmitParticlesPacket::new);
    public static final CustomPacketPayload.Type<EmitParticlesPacket> TYPE = new CustomPacketPayload.Type<>(HexereiUtil.getResource("emit_cauldron_particles"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    BlockPos pos;
    int emitParticles;
    boolean spout;

    public EmitParticlesPacket(BlockPos pos, int emitParticles, boolean spout) {
        this.pos = pos;
        this.emitParticles = emitParticles;
        this.spout = spout;
    }

    public EmitParticlesPacket(RegistryFriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readInt(), buffer.readBoolean());
    }

    public static void encode(EmitParticlesPacket object, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(object.pos);
        buffer.writeInt(object.emitParticles);
        buffer.writeBoolean(object.spout);
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {

        if(minecraft.level.getBlockEntity(pos) instanceof MixingCauldronTile mixingCauldronTile) {
            mixingCauldronTile.emitParticles = emitParticles;
            mixingCauldronTile.emitParticleSpout = spout;
            mixingCauldronTile.setChanged();
        }
    }

}
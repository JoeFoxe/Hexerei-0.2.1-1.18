package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.tileentity.SageBurningPlateTile;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public class EmitExtinguishParticlesPacket extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, EmitExtinguishParticlesPacket> CODEC  = StreamCodec.ofMember(EmitExtinguishParticlesPacket::encode, EmitExtinguishParticlesPacket::new);
    public static final Type<EmitExtinguishParticlesPacket> TYPE = new Type<>(HexereiUtil.getResource("emit_extinguish_particles"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    BlockPos pos;

    public EmitExtinguishParticlesPacket(BlockPos pos) {
        this.pos = pos;
    }

    public EmitExtinguishParticlesPacket(RegistryFriendlyByteBuf buf) {
        this(buf.readBlockPos());
    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        if(player.level().getBlockEntity(pos) instanceof SageBurningPlateTile sageBurningPlateTile)
            sageBurningPlateTile.extinguishParticles();
    }
}
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
import net.minecraft.world.item.ItemStack;

public class EatParticlesPacket extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, EatParticlesPacket> CODEC  = StreamCodec.ofMember(EatParticlesPacket::encode, EatParticlesPacket::new);
    public static final Type<EatParticlesPacket> TYPE = new Type<>(HexereiUtil.getResource("eat_particles"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    int sourceId;
    ItemStack stack;

    public EatParticlesPacket(Entity entity, ItemStack stack) {
        this.sourceId = entity.getId();
        this.stack = stack.copy();
    }
    public EatParticlesPacket(RegistryFriendlyByteBuf buf) {
        this.sourceId = buf.readInt();
        this.stack = ItemStack.STREAM_CODEC.decode(buf);
    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(sourceId);
        ItemStack.STREAM_CODEC.encode(buffer, stack);
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        if(player.level().getEntity(sourceId) != null) {
            if((player.level().getEntity(sourceId)) instanceof CrowEntity crow) {
                crow.eatParticles(stack);
            }
            if((player.level().getEntity(sourceId)) instanceof OwlEntity owl) {
                owl.eatParticles(stack);
            }
        }
    }

}
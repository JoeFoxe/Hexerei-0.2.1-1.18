package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class EmotionPacket extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, EmotionPacket> CODEC  = StreamCodec.ofMember(EmotionPacket::encode, EmotionPacket::new);
    public static final CustomPacketPayload.Type<EmotionPacket> TYPE = new CustomPacketPayload.Type<>(HexereiUtil.getResource("owl_emotion"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    int sourceId;
    int packedEmotionScales;

    public EmotionPacket(Entity entity, int packedEmotionScales) {
        this.sourceId = entity.getId();
        this.packedEmotionScales = packedEmotionScales;

    }
    public EmotionPacket(RegistryFriendlyByteBuf buf) {
        this.sourceId = buf.readInt();
        this.packedEmotionScales = buf.readInt();
    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(sourceId);
        buffer.writeInt(packedEmotionScales);
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {

        if((player.level().getEntity(sourceId)) instanceof OwlEntity owl) {
            int packedEmotionScales = this.packedEmotionScales;
            int happiness = (packedEmotionScales >> 16) & 0xFF;
            int distressed = (packedEmotionScales >> 8) & 0xFF;
            int anger = packedEmotionScales & 0xFF;

            owl.emotions = new OwlEntity.Emotions(anger, distressed, happiness);
        }
    }
}
package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.data_components.FluteData;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class CrowFluteClearCrowPerchToServer extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, CrowFluteClearCrowPerchToServer> CODEC  = StreamCodec.ofMember(CrowFluteClearCrowPerchToServer::encode, CrowFluteClearCrowPerchToServer::new);
    public static final Type<CrowFluteClearCrowPerchToServer> TYPE = new Type<>(HexereiUtil.getResource("crow_flute_clear_perch_server"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    ItemStack flute;
    UUID entityId;
    int hand;

    public CrowFluteClearCrowPerchToServer(ItemStack flute, UUID entityId, int hand) {
        this.flute = flute;
        this.entityId = entityId;
        this.hand = hand;
    }
    public CrowFluteClearCrowPerchToServer(RegistryFriendlyByteBuf buf) {
        this.flute = ItemStack.STREAM_CODEC.decode(buf);
        this.entityId = buf.readUUID();
        this.hand = buf.readInt();
    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        ItemStack.STREAM_CODEC.encode(buffer, flute);
        buffer.writeUUID(entityId);
        buffer.writeInt(hand);
    }

    @Override
    public void onServerReceived(MinecraftServer server, ServerPlayer player) {

        if(hand == 0) {
            ItemStack stack = player.level().getPlayerByUUID(entityId).getMainHandItem();
            if (stack.getItem() == flute.getItem()) {

                FluteData fluteData = stack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY);
                for (FluteData.CrowIds crowIds : fluteData.crowList()) {
                    Entity entity = ((ServerLevel) player.level()).getEntity(crowIds.uuid());
                    if (entity instanceof CrowEntity crow) {
                        crow.setPerchPos(null);
                    }
                }
            }
        }
        else
        {
            ItemStack stack = player.level().getPlayerByUUID(entityId).getOffhandItem();
            if (stack.getItem() == flute.getItem()) {

                FluteData fluteData = stack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY);
                for (FluteData.CrowIds crowIds : fluteData.crowList()) {
                    Entity entity = ((ServerLevel) player.level()).getEntity(crowIds.uuid());
                    if (entity instanceof CrowEntity crow) {
                        crow.setPerchPos(null);
                    }
                }
            }
        }
    }

}
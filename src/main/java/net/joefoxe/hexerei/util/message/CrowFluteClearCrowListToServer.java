package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.data_components.FluteData;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.UUID;

public class CrowFluteClearCrowListToServer extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, CrowFluteClearCrowListToServer> CODEC  = StreamCodec.ofMember(CrowFluteClearCrowListToServer::encode, CrowFluteClearCrowListToServer::new);
    public static final Type<CrowFluteClearCrowListToServer> TYPE = new Type<>(HexereiUtil.getResource("crow_flute_clear_list_server"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    ItemStack flute;
    UUID entityId;
    int hand;

    public CrowFluteClearCrowListToServer(ItemStack flute, UUID entityId, int hand) {
        this.flute = flute;
        this.entityId = entityId;
        this.hand = hand;
    }
    public CrowFluteClearCrowListToServer(RegistryFriendlyByteBuf buf) {
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

        if (player.level().getPlayerByUUID(entityId) instanceof Player player1) {
            if(hand == 0) {
                ItemStack stack = player1.getMainHandItem();
                if (stack.getItem() == flute.getItem()) {
                    FluteData fluteData = stack.getOrDefault(ModDataComponents.FLUTE, FluteData.empty());
                    fluteData = new FluteData(fluteData.commandSelected(), fluteData.helpCommandSelected(), fluteData.commandMode(), new ArrayList<>(), fluteData.dyeColor1(), fluteData.dyeColor2());
                    stack.set(ModDataComponents.FLUTE, fluteData);
                    player1.setItemInHand(InteractionHand.MAIN_HAND, stack);
                }
            }
            else {
                ItemStack stack = player1.getOffhandItem();
                if (player1.getOffhandItem().getItem() == flute.getItem()) {
                    FluteData fluteData = stack.getOrDefault(ModDataComponents.FLUTE, FluteData.empty());
                    fluteData = new FluteData(fluteData.commandSelected(), fluteData.helpCommandSelected(), fluteData.commandMode(), new ArrayList<>(), fluteData.dyeColor1(), fluteData.dyeColor2());
                    stack.set(ModDataComponents.FLUTE, fluteData);
                    player1.setItemInHand(InteractionHand.OFF_HAND, stack);
                }
            }
        }
    }
}
package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.data_components.FluteData;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class CrowFluteHelpCommandSyncToServer extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, CrowFluteHelpCommandSyncToServer> CODEC  = StreamCodec.ofMember(CrowFluteHelpCommandSyncToServer::encode, CrowFluteHelpCommandSyncToServer::new);
    public static final Type<CrowFluteHelpCommandSyncToServer> TYPE = new Type<>(HexereiUtil.getResource("crow_flute_help_command_server"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    ItemStack flute;
    int helpCommand;
    UUID entityId;
    int hand;

    public CrowFluteHelpCommandSyncToServer(ItemStack flute, int helpCommand, UUID entityId, int hand) {
        this.flute = flute;
        this.helpCommand = helpCommand;
        this.entityId = entityId;
        this.hand = hand;
    }
    public CrowFluteHelpCommandSyncToServer(RegistryFriendlyByteBuf buf) {
        this.flute = ItemStack.STREAM_CODEC.decode(buf);
        this.helpCommand = buf.readInt();
        this.entityId = buf.readUUID();
        this.hand = buf.readInt();

    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        ItemStack.STREAM_CODEC.encode(buffer, flute);
        buffer.writeInt(helpCommand);
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
                    fluteData = new FluteData(fluteData.commandSelected(), helpCommand, fluteData.commandMode(), fluteData.crowList(), fluteData.dyeColor1(), fluteData.dyeColor2());
                    stack.set(ModDataComponents.FLUTE, fluteData);
                    player1.setItemInHand(InteractionHand.MAIN_HAND, stack);
                }
            }
            else {
                ItemStack stack = player1.getOffhandItem();
                if (player1.getOffhandItem().getItem() == flute.getItem()) {
                    FluteData fluteData = stack.getOrDefault(ModDataComponents.FLUTE, FluteData.empty());
                    fluteData = new FluteData(fluteData.commandSelected(), helpCommand, fluteData.commandMode(), fluteData.crowList(), fluteData.dyeColor1(), fluteData.dyeColor2());
                    stack.set(ModDataComponents.FLUTE, fluteData);
                    player1.setItemInHand(InteractionHand.OFF_HAND, stack);
                }
            }
        }
    }
}
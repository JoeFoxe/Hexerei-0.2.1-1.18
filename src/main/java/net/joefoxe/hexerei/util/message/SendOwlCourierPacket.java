package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.custom.OwlCourierDepot;
import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.joefoxe.hexerei.data.owl.OwlCourierDepotSavedData;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class SendOwlCourierPacket extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, SendOwlCourierPacket> CODEC  = StreamCodec.ofMember(SendOwlCourierPacket::encode, SendOwlCourierPacket::new);
    public static final CustomPacketPayload.Type<SendOwlCourierPacket> TYPE = new CustomPacketPayload.Type<>(HexereiUtil.getResource("owl_courier"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    int sourceId;
    boolean deliverToEntity;
    GlobalPos pos;
    InteractionHand hand;
    UUID entityId; // id of the player to deliver to

    public SendOwlCourierPacket(Entity owl, GlobalPos pos, InteractionHand hand) {
        this.sourceId = owl.getId();
        this.pos = pos;
        this.hand = hand;
        this.deliverToEntity = false;
    }

    public SendOwlCourierPacket(Entity owl, UUID playerUUID, InteractionHand hand) {
        this.sourceId = owl.getId();
        this.entityId = playerUUID;
        this.hand = hand;
        this.deliverToEntity = true;
    }
    public SendOwlCourierPacket(RegistryFriendlyByteBuf buf) {
        this.sourceId = buf.readInt();
        this.deliverToEntity = buf.readBoolean();
        this.hand = buf.readEnum(InteractionHand.class);
        if(this.deliverToEntity){
            this.entityId = buf.readUUID();
        } else {
            this.pos = buf.readGlobalPos();
        }

    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(sourceId);
        buffer.writeBoolean(deliverToEntity);
        buffer.writeEnum(hand);
        if(deliverToEntity){
            buffer.writeUUID(entityId);
        } else {
            buffer.writeGlobalPos(pos);
        }
    }

    @Override
    public void onServerReceived(MinecraftServer server, ServerPlayer player) {

        if (player.level().getEntity(sourceId) instanceof OwlEntity owl){
            if (owl.currentTask.isNone()) {
//                    owl.messagingController.setHasMessage(true);
                if (deliverToEntity) {
                    if (server.getPlayerList().getPlayer(entityId) != null) {
                        ItemStack copyStack = player.getItemInHand(hand).copy();
                        if (copyStack.getItem() == ModItems.COURIER_PACKAGE.get() || copyStack.getItem() == ModItems.COURIER_LETTER.get()) {
                            ServerPlayer player1 = server.getPlayerList().getPlayer(entityId);
                            owl.messagingController.setDestination(player1);
                            copyStack.setCount(1);
                            owl.messagingController.setMessageStack(copyStack);
                            player.getItemInHand(hand).shrink(1);
                            owl.sync();
                        }

                    }
                } else {
                    ServerLevel level = player.level().getServer().getLevel(pos.dimension());
                    if (level != null && level.getBlockState(pos.pos()).getBlock() instanceof OwlCourierDepot) {
                        ItemStack copyStack = player.getItemInHand(hand).copy();
                        if (copyStack.getItem() == ModItems.COURIER_PACKAGE.get() || copyStack.getItem() == ModItems.COURIER_LETTER.get()) {
                            owl.messagingController.setDestination(pos);
                            copyStack.setCount(1);
                            owl.messagingController.setMessageStack(copyStack);
                            player.getItemInHand(hand).shrink(1);
                            owl.sync();
                        }
                    }
                    else {

                        OwlCourierDepotSavedData.get().clearOwlCourierDepot(pos);
                        return;
                    }
                }
                owl.messagingController.start(GlobalPos.of(owl.level().dimension(), owl.blockPosition()));
                owl.currentTask = OwlEntity.OwlTask.DELIVER_MESSAGE;
            } else {
                player.sendSystemMessage(Component.translatable("message.hexerei.owl_already_doing_task", owl.getName()));
            }
        }
    }
}
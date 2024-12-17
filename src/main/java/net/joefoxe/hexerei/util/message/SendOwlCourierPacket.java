package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.custom.OwlCourierDepot;
import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.joefoxe.hexerei.data.owl.OwlCourierDepotSavedData;
import net.joefoxe.hexerei.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.UUID;
import java.util.function.Supplier;

public class SendOwlCourierPacket {

//    final CompoundTag lines;
    int sourceId;
    boolean deliverToEntity;
    GlobalPos pos;
    InteractionHand hand;
    UUID entityId; // id of the player to deliver to

    public SendOwlCourierPacket(Entity owl, GlobalPos pos, InteractionHand hand) {
        this.sourceId = owl.getId();
//        this.lines = lines;
        this.pos = pos;
        this.hand = hand;
        this.deliverToEntity = false;
    }

    public SendOwlCourierPacket(Entity owl, UUID playerUUID, InteractionHand hand) {
        this.sourceId = owl.getId();
//        this.lines = lines;
        this.entityId = playerUUID;
        this.hand = hand;
        this.deliverToEntity = true;
    }
    public SendOwlCourierPacket(FriendlyByteBuf buf) {
        this.sourceId = buf.readInt();
//        this.lines = buf.readNbt();
        this.deliverToEntity = buf.readBoolean();
        this.hand = buf.readEnum(InteractionHand.class);
        if(this.deliverToEntity){
            this.entityId = buf.readUUID();
        } else {
            this.pos = buf.readGlobalPos();
        }

    }

    public static void encode(SendOwlCourierPacket object, FriendlyByteBuf buffer) {
        buffer.writeInt(object.sourceId);
//        buffer.writeNbt(object.lines);
        buffer.writeBoolean(object.deliverToEntity);
        buffer.writeEnum(object.hand);
        if(object.deliverToEntity){
            buffer.writeUUID(object.entityId);
        } else {
            buffer.writeGlobalPos(object.pos);
        }
    }

    public static SendOwlCourierPacket decode(FriendlyByteBuf buffer) {
        return new SendOwlCourierPacket(buffer);
    }

    public static void consume(SendOwlCourierPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world;
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                world = Hexerei.proxy.getLevel();
            }
            else {
                if (ctx.get().getSender() == null) return;
                world = ctx.get().getSender().level();
            }

            ServerPlayer player = ctx.get().getSender();
            if (world.getEntity(packet.sourceId) instanceof OwlEntity owl){
                if (owl.currentTask.isNone() && player != null) {
//                    owl.messagingController.setHasMessage(true);
                    if (packet.deliverToEntity) {
                        if (ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(packet.entityId) != null) {
                            ItemStack copyStack = player.getItemInHand(packet.hand).copy();
                            if (copyStack.getItem() == ModItems.COURIER_PACKAGE.get() || copyStack.getItem() == ModItems.COURIER_LETTER.get()) {
                                ServerPlayer player1 = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(packet.entityId);
                                owl.messagingController.setDestination(player1);
                                copyStack.setCount(1);
                                owl.messagingController.setMessageStack(copyStack);
                                player.getItemInHand(packet.hand).shrink(1);
                                owl.sync();
                            }

                        }
                    } else {
                        ServerLevel level = world.getServer().getLevel(packet.pos.dimension());
                        if (level != null && level.getBlockState(packet.pos.pos()).getBlock() instanceof OwlCourierDepot) {
                            ItemStack copyStack = player.getItemInHand(packet.hand).copy();
                            if (copyStack.getItem() == ModItems.COURIER_PACKAGE.get() || copyStack.getItem() == ModItems.COURIER_LETTER.get()) {
                                owl.messagingController.setDestination(packet.pos);
                                copyStack.setCount(1);
                                owl.messagingController.setMessageStack(copyStack);
                                player.getItemInHand(packet.hand).shrink(1);
                                owl.sync();
                            }
                        }
                        else {


                            OwlCourierDepotSavedData.get().clearOwlCourierDepot(packet.pos);
//                            System.out.println("Missing depot at - " + packet.pos);
                            ctx.get().setPacketHandled(true);
                            return;
                        }
                    }
                    owl.messagingController.start(GlobalPos.of(owl.level().dimension(), owl.blockPosition()));
                    owl.currentTask = OwlEntity.OwlTask.DELIVER_MESSAGE;
                } else {
                    if (player != null)
                        player.sendSystemMessage(Component.translatable("message.hexerei.owl_already_doing_task", owl.getName()));
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;


public class BrowAnimPacket extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, BrowAnimPacket> CODEC  = StreamCodec.ofMember(BrowAnimPacket::encode, BrowAnimPacket::new);
    public static final Type<BrowAnimPacket> TYPE = new Type<>(HexereiUtil.getResource("brow_anim"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    int sourceId;
    OwlEntity.BrowAnim browAnim;
    int duration;
    boolean happyAnim;

    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(sourceId);
        buffer.writeEnum(browAnim);
        buffer.writeInt(duration);
        buffer.writeBoolean(happyAnim);
    }

    public BrowAnimPacket(Entity entity, OwlEntity.BrowAnim browAnim, int duration) {
        this.sourceId = entity.getId();
        this.browAnim = browAnim;
        this.duration = duration;
        this.happyAnim = false;

    }

    public BrowAnimPacket(Entity entity, OwlEntity.BrowAnim browAnim, int duration, boolean happyAnim) {
        this.sourceId = entity.getId();
        this.browAnim = browAnim;
        this.duration = duration;
        this.happyAnim = happyAnim;

    }
    public BrowAnimPacket(FriendlyByteBuf buf) {
        this.sourceId = buf.readInt();
        this.browAnim = buf.readEnum(OwlEntity.BrowAnim.class);
        this.duration = buf.readInt();
        this.happyAnim = buf.readBoolean();
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {

        if((minecraft.level.getEntity(sourceId)) instanceof OwlEntity owl) {
            if (happyAnim) {
                owl.browHappyAnimation.start();
                owl.browHappyAnimation.activeTimer = duration;
                owl.browHappyAnimation.setBrowAnim(browAnim);
            } else {
                owl.browAnimation.start();
                owl.browAnimation.activeTimer = duration;
                owl.browAnimation.setBrowAnim(browAnim);
            }
        }
    }
}
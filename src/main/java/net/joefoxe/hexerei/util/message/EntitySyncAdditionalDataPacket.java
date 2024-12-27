package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class EntitySyncAdditionalDataPacket extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, EntitySyncAdditionalDataPacket> CODEC  = StreamCodec.ofMember(EntitySyncAdditionalDataPacket::encode, EntitySyncAdditionalDataPacket::new);
    public static final Type<EntitySyncAdditionalDataPacket> TYPE = new Type<>(HexereiUtil.getResource("entity_sync_additional"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    int sourceId;
    CompoundTag tag;

    public EntitySyncAdditionalDataPacket(Entity entity, CompoundTag tag) {
        this.sourceId = entity.getId();
        this.tag = tag;
    }
    public EntitySyncAdditionalDataPacket(RegistryFriendlyByteBuf buf) {
        this.sourceId = buf.readInt();
        this.tag = buf.readNbt();
    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(sourceId);
        buffer.writeNbt(tag);
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {

        if(player.level().getEntity(sourceId) instanceof CrowEntity crow) {
            crow.readAdditionalSaveDataNoSuper(tag);
        }
        if(player.level().getEntity(sourceId) instanceof OwlEntity owl) {
            owl.readAdditionalSaveDataNoSuper(tag);
        }
    }
}
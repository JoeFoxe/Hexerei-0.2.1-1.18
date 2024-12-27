package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.ArrayList;
import java.util.List;

public class CrowWhitelistSyncToServer extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, CrowWhitelistSyncToServer> CODEC  = StreamCodec.ofMember(CrowWhitelistSyncToServer::encode, CrowWhitelistSyncToServer::new);
    public static final Type<CrowWhitelistSyncToServer> TYPE = new Type<>(HexereiUtil.getResource("crow_whitelist_sync_server"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    int sourceId;
    List<ResourceLocation> whitelist;

    public CrowWhitelistSyncToServer(Entity entity, List<Block> whitelist) {
        List<ResourceLocation> list = new ArrayList<>();
        this.sourceId = entity.getId();
        for (Block block : whitelist) {
            list.add(BuiltInRegistries.BLOCK.getKey(block));
        }
        this.whitelist = list;
    }
    public CrowWhitelistSyncToServer(RegistryFriendlyByteBuf buf) {
        this.sourceId = buf.readInt();
        List<ResourceLocation> list = new ArrayList<>();
        int size = buf.readInt();
        for(int i = 0; i < size; i++){
            list.add(buf.readResourceLocation());
        }
        this.whitelist = list;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(sourceId);
        buffer.writeInt(whitelist.size());
        for (ResourceLocation resourceLocation : whitelist) {
            buffer.writeResourceLocation(resourceLocation);
        }
    }

    @Override
    public void onServerReceived(MinecraftServer server, ServerPlayer player) {

        if(player.level().getEntity(sourceId) instanceof CrowEntity crowEntity) {
            List<Block> blockList = new ArrayList<>();
            for (ResourceLocation resourceLocation : whitelist) {
                blockList.add(BuiltInRegistries.BLOCK.get(resourceLocation));
            }
            crowEntity.harvestWhitelist = blockList;
        }
    }
}
package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

import java.util.ArrayList;
import java.util.Collection;

public class AskForMapDataPacket extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, AskForMapDataPacket> CODEC  = StreamCodec.ofMember(AskForMapDataPacket::encode, AskForMapDataPacket::new);
    public static final Type<AskForMapDataPacket> TYPE = new Type<>(HexereiUtil.getResource("map_data_server"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    ItemStack stack;

    public AskForMapDataPacket(ItemStack stack) {
        this.stack = stack;
    }
    public AskForMapDataPacket(RegistryFriendlyByteBuf buf) {
        this.stack = ItemStack.STREAM_CODEC.decode(buf);
    }

    public void encode(RegistryFriendlyByteBuf buffer) {
        ItemStack.STREAM_CODEC.encode(buffer, stack);
    }

    @Override
    public void onServerReceived(MinecraftServer server, ServerPlayer player) {
        HoldingPlayer holdingPlayer = HoldingPlayer.create(player);
        MapItemSavedData.MapPatch mapitemsaveddata$mappatch;
        MapItemSavedData mapitemsaveddata = MapItem.getSavedData(stack, player.level());
        if(mapitemsaveddata != null){
            MapId mapId = stack.getOrDefault(DataComponents.MAP_ID, new MapId(0));
            mapitemsaveddata$mappatch = holdingPlayer.createPatch(mapitemsaveddata);
            Collection<MapDecoration> collection = new ArrayList<>();
            mapitemsaveddata.getDecorations().forEach(collection::add);
            MapDataPacket mapDataPacket = new MapDataPacket(mapId, mapitemsaveddata.scale, mapitemsaveddata.locked, collection, mapitemsaveddata$mappatch);
            HexereiPacketHandler.sendToAllPlayers(mapDataPacket, server);
        }
    }


    public static class HoldingPlayer {
        public final Player player;
        private boolean dirtyData = true;
        /** The lowest dirty x value */
        private int minDirtyX;
        /** The lowest dirty z value */
        private int minDirtyY;
        /** The highest dirty x value */
        private int maxDirtyX = 127;
        /** The highest dirty z value */
        private int maxDirtyY = 127;
        private boolean dirtyDecorations = true;
        private int tick;
        public int step;

        HoldingPlayer(Player pPlayer) {
            this.player = pPlayer;
        }

        public static HoldingPlayer create(Player pPlayer){
            return new HoldingPlayer(pPlayer);
        }

        private MapItemSavedData.MapPatch createPatch(MapItemSavedData mapItemSavedData) {
            int i = this.minDirtyX;
            int j = this.minDirtyY;
            int k = this.maxDirtyX + 1 - this.minDirtyX;
            int l = this.maxDirtyY + 1 - this.minDirtyY;
            byte[] abyte = new byte[k * l];

            for(int i1 = 0; i1 < k; ++i1) {
                for(int j1 = 0; j1 < l; ++j1) {
                    abyte[i1 + j1 * k] = mapItemSavedData.colors[i + i1 + (j + j1) * 128];
                }
            }

            return new MapItemSavedData.MapPatch(i, j, k, l, abyte);
        }


        void markColorsDirty(int pX, int pZ) {
            if (this.dirtyData) {
                this.minDirtyX = Math.min(this.minDirtyX, pX);
                this.minDirtyY = Math.min(this.minDirtyY, pZ);
                this.maxDirtyX = Math.max(this.maxDirtyX, pX);
                this.maxDirtyY = Math.max(this.maxDirtyY, pZ);
            } else {
                this.dirtyData = true;
                this.minDirtyX = pX;
                this.minDirtyY = pZ;
                this.maxDirtyX = pX;
                this.maxDirtyY = pZ;
            }

        }

        private void markDecorationsDirty() {
            this.dirtyDecorations = true;
        }
    }

    public static class MapPatch {
        public final int startX;
        public final int startY;
        public final int width;
        public final int height;
        public final byte[] mapColors;

        public MapPatch(int pStartX, int pStartY, int pWidth, int pHeight, byte[] pMapColors) {
            this.startX = pStartX;
            this.startY = pStartY;
            this.width = pWidth;
            this.height = pHeight;
            this.mapColors = pMapColors;
        }

        public void applyToMap(MapItemSavedData pSavedData) {
            for(int i = 0; i < this.width; ++i) {
                for(int j = 0; j < this.height; ++j) {
                    pSavedData.setColor(this.startX + i, this.startY + j, this.mapColors[i + j * this.width]);
                }
            }

        }
    }
}
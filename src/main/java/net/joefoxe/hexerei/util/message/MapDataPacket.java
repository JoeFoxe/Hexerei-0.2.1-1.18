package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

import javax.annotation.Nullable;
import java.util.Collection;

public class MapDataPacket extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, AskForMapDataPacket> CODEC  = StreamCodec.ofMember(AskForMapDataPacket::encode, AskForMapDataPacket::new);
    public static final Type<AskForMapDataPacket> TYPE = new Type<>(HexereiUtil.getResource("map_data"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


    private final MapId mapId;
    private final byte scale;
    private final boolean locked;
//    @Nullable
//    private final List<MapDecoration> decorations;
//    @Nullable
//    private final MapItemSavedData.MapPatch colorPatch;


    public MapDataPacket(MapId pMapId, byte pScale, boolean pLocked, @Nullable Collection<MapDecoration> pDecorations, @Nullable MapItemSavedData.MapPatch pColorPatch) {
        this.mapId = pMapId;
        this.scale = pScale;
        this.locked = pLocked;
//        this.decorations = pDecorations != null ? Lists.newArrayList(pDecorations) : null;
//        this.colorPatch = pColorPatch;
    }
    public MapDataPacket(RegistryFriendlyByteBuf pBuffer) {
        this.mapId = MapId.STREAM_CODEC.decode(pBuffer);
        this.scale = pBuffer.readByte();
        this.locked = pBuffer.readBoolean();
//        this.decorations = pBuffer.readNullable((p_237731_) -> {
//            return p_237731_.readList((p_178981_) -> {
//                MapDecoration.Type mapdecoration$type = p_178981_.readEnum(MapDecoration.Type.class);
//                byte b0 = p_178981_.readByte();
//                byte b1 = p_178981_.readByte();
//                byte b2 = (byte)(p_178981_.readByte() & 15);
//                Component component = p_178981_.readNullable(FriendlyByteBuf::readComponent);
//                return new MapDecoration(mapdecoration$type, b0, b1, b2, component);
//            });
//        });
//        int i = pBuffer.readUnsignedByte();
//        if (i > 0) {
//            int j = pBuffer.readUnsignedByte();
//            int k = pBuffer.readUnsignedByte();
//            int l = pBuffer.readUnsignedByte();
//            byte[] abyte = pBuffer.readByteArray();
//            this.colorPatch = new MapItemSavedData.MapPatch(k, l, i, j, abyte);
//        } else {
//            this.colorPatch = null;
//        }

    }

    public void encode(RegistryFriendlyByteBuf pBuffer) {
        MapId.STREAM_CODEC.encode(pBuffer, mapId);
        pBuffer.writeByte(scale);
        pBuffer.writeBoolean(locked);
//        pBuffer.writeNullable(decorations, (p_237728_, p_237729_) -> {
//            p_237728_.writeCollection(p_237729_, (p_237725_, p_237726_) -> {
//                p_237725_.writeEnum(p_237726_.getType());
//                p_237725_.writeByte(p_237726_.getX());
//                p_237725_.writeByte(p_237726_.getY());
//                p_237725_.writeByte(p_237726_.getRot() & 15);
//                p_237725_.writeNullable(p_237726_.getName(), FriendlyByteBuf::writeComponent);
//            });
//        });
//        if (colorPatch != null) {
//            pBuffer.writeByte(colorPatch.width);
//            pBuffer.writeByte(colorPatch.height);
//            pBuffer.writeByte(colorPatch.startX);
//            pBuffer.writeByte(colorPatch.startY);
//            pBuffer.writeByteArray(colorPatch.mapColors);
//        } else {
//            pBuffer.writeByte(0);
//        }

    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        MapRenderer maprenderer = minecraft.gameRenderer.getMapRenderer();
        MapItemSavedData mapitemsaveddata = minecraft.level.getMapData(mapId);
        if (mapitemsaveddata == null) {
            mapitemsaveddata = MapItemSavedData.createForClient(getScale(), isLocked(), minecraft.level.dimension());
            minecraft.level.setMapData(mapId, mapitemsaveddata);
        }

//        applyToMap(mapitemsaveddata);
        maprenderer.update(mapId, mapitemsaveddata);
    }

    /**
     * Sets new MapData from the packet to given MapData param
     */
//    public void applyToMap(MapItemSavedData pMapdata) {
//        if (this.decorations != null) {
//            pMapdata.addClientSideDecorations(this.decorations);
//        }
//
//        if (this.colorPatch != null) {
//            this.colorPatch.applyToMap(pMapdata);
//        }
//
//    }

    public byte getScale() {
        return this.scale;
    }

    public boolean isLocked() {
        return this.locked;
    }



}
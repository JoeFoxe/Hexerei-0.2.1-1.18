package net.joefoxe.hexerei.tileentity;

import com.mojang.logging.LogUtils;
import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.message.TESyncPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.annotation.Nullable;

public class CourierLetterTile extends BlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();

//    private String[] messages;
    private OwlEntity.MessageText text;
    private boolean sealed;


    public CourierLetterTile(BlockEntityType<?> tileEntityTypeIn, BlockPos blockPos, BlockState blockState) {
        super(tileEntityTypeIn, blockPos, blockState);
        this.text = new OwlEntity.MessageText();
//        this.messages = IntStream.range(0, 12).mapToObj((p_277214_) -> this.text.getMessage(p_277214_)).map(Component::getString).toArray(String[]::new);
        this.sealed = false;
    }

    public CourierLetterTile(BlockPos blockPos, BlockState blockState) {
        this(ModTileEntities.COURIER_LETTER_TILE.get(),blockPos, blockState);
    }

    private OwlEntity.MessageText loadLines(OwlEntity.MessageText pText) {
        for(int i = 0; i < 12; ++i) {
            Component component = pText.getMessage(i);
            pText = pText.setMessage(i, component);
        }

        return pText;
    }

    public boolean interact(){

        return false;
    }

    @Override
    public BlockEntityType<?> getType() {
        return super.getType();
    }

    @Override
    public void requestModelDataUpdate() {
        super.requestModelDataUpdate();
    }

    @NotNull
    @Override
    public ModelData getModelData() {
        return super.getModelData();
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

//    @Override
    public CompoundTag save(CompoundTag pTag, HolderLookup.Provider registries) {
        saveAdditional(pTag, registries);

        return pTag;
    }

    public CompoundTag saveData(CompoundTag pTag, HolderLookup.Provider registries) {
        return save(pTag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.loadFromTag(tag);
    }

    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider registries) {
        super.saveAdditional(pTag, registries);
        if (this.sealed)
            pTag.putBoolean("Sealed", this.sealed);
        OwlEntity.MessageText.DIRECT_CODEC.encodeStart(NbtOps.INSTANCE, this.text).resultOrPartial(LOGGER::error).ifPresent((tag) -> {
            pTag.put("Message", tag);
        });

    }

    public void loadFromTag(CompoundTag pTag) {
        if (pTag.contains("Message")) {
            OwlEntity.MessageText.DIRECT_CODEC.parse(NbtOps.INSTANCE, pTag.getCompound("Message")).resultOrPartial(LOGGER::error).ifPresent((message) -> {
                this.text = this.loadLines(message);
            });
        }
        if (pTag.contains("Sealed")) {
            this.sealed = pTag.getBoolean("Sealed");
        }
    }


    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.save(new CompoundTag(), registries);
    }

    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {

        return ClientboundBlockEntityDataPacket.create(this, (tag, registryAccess) -> this.getUpdateTag(registryAccess));
    }

    public void sync() {
        setChanged();

        if(level != null){
            if (!level.isClientSide) {
                CompoundTag tag = new CompoundTag();
                this.saveAdditional(tag, level.registryAccess());
                HexereiPacketHandler.sendToNearbyClient(level, worldPosition, new TESyncPacket(worldPosition, tag));
            }

            if (this.level != null)
                this.level.sendBlockUpdated(this.worldPosition, this.level.getBlockState(this.worldPosition), this.level.getBlockState(this.worldPosition),
                        Block.UPDATE_CLIENTS);
        }
    }


//    @Override
    public void tick() {
//        if(level.isClientSide)
//            return;

    }
}

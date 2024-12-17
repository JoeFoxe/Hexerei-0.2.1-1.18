package net.joefoxe.hexerei.tileentity;

import com.mojang.logging.LogUtils;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.custom.CourierPackage;
import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.joefoxe.hexerei.config.HexConfig;
import net.joefoxe.hexerei.container.CofferContainer;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.CourierLetterUpdatePacket;
import net.joefoxe.hexerei.util.message.TESyncPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.stream.IntStream;

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
    public AABB getRenderBoundingBox() {
        return super.getRenderBoundingBox().inflate(5, 5, 5);
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
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
    }

    @Override
    public CompoundTag serializeNBT() {
        return super.serializeNBT();
    }

//    @Override
    public CompoundTag save(CompoundTag pTag) {
        saveAdditional(pTag);

        return pTag;
    }

    public CompoundTag saveData(CompoundTag pTag) {
        return save(pTag);
    }


    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.loadFromTag(pTag);
    }

    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
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
    public CompoundTag getUpdateTag()
    {
        return this.save(new CompoundTag());
    }

    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {

        return ClientboundBlockEntityDataPacket.create(this, (tag) -> this.getUpdateTag());
    }

    @Override
    public void onDataPacket(final Connection net, final ClientboundBlockEntityDataPacket pkt)
    {
        this.deserializeNBT(pkt.getTag());
    }

    public void sync() {
        setChanged();

        if(level != null){
            if (!level.isClientSide)
                HexereiPacketHandler.instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new TESyncPacket(worldPosition, save(new CompoundTag())));

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

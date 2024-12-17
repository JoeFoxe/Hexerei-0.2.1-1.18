package net.joefoxe.hexerei.tileentity;

import net.joefoxe.hexerei.block.custom.Candle;
import net.joefoxe.hexerei.data.candle.AbstractCandleEffect;
import net.joefoxe.hexerei.data.candle.BonemealingCandleEffect;
import net.joefoxe.hexerei.data.candle.CandleData;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.TESyncPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.Random;

import static net.joefoxe.hexerei.util.HexereiUtil.moveTo;

public class CandleTile extends BlockEntity {

    public NonNullList<CandleData> candles = NonNullList.withSize(4, new CandleData());

    public boolean litStateOld;
    public int redstoneAnalogSignal;
    public int redstoneBases;
    private boolean startupFlag;

    public Component customName;

    public int tickCount = 0;

    public CandleTile(BlockEntityType<?> tileEntityTypeIn, BlockPos blockPos, BlockState blockState) {
        super(tileEntityTypeIn, blockPos, blockState);
        candles.replaceAll(ignored -> new CandleData());
        startupFlag = false;
        litStateOld = false;
    }

    public CandleTile(BlockPos blockPos, BlockState blockState) {
        this(ModTileEntities.CANDLE_TILE.get(),blockPos, blockState);
    }

    @Override
    public void load(CompoundTag nbt) {


        if (nbt.contains("candle0", Tag.TAG_COMPOUND)){
            if (nbt.contains("candle0")) {
                candles.get(0).load(nbt.getCompound("candle0"));
            }
            if (nbt.contains("candle1")) {
                candles.get(1).load(nbt.getCompound("candle1"));
            }
            if (nbt.contains("candle2")) {
                candles.get(2).load(nbt.getCompound("candle2"));
            }
            if (nbt.contains("candle3")) {
                candles.get(3).load(nbt.getCompound("candle3"));
            }
        }
//        for(int i = 0; i < 4; i++){
//            CandleData candleData = candles.get(i);
//            if (candleData.returnToBlock) {
//                setOffsetPos(i);
////                candleData.moveInstantlyToTarget();
//            }
//        }
        setOffsetPos(true);
        super.load(nbt);

    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        compound.putInt("effectCooldown", candles.get(0).cooldown);
        compound.put("candle0", candles.get(0).save());
        compound.put("candle1", candles.get(1).save());
        compound.put("candle2", candles.get(2).save());
        compound.put("candle3", candles.get(3).save());
    }

//    @Override
    public CompoundTag save(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("effectCooldown", candles.get(0).cooldown);
        tag.put("candle0", candles.get(0).save());
        tag.put("candle1", candles.get(1).save());
        tag.put("candle2", candles.get(2).save());
        tag.put("candle3", candles.get(3).save());
        return tag;
    }

    public int getNumberOfCandles() {
        int num = 0;
        for (CandleData candleData : candles) {
            if (candleData.hasCandle)
                num++;
        }
        return num;
    }

    public Component getCustomName() {
        return getCustomName(0);
    }
    public Component getCustomName(int slot) {
        return this.candles.get(slot).customName;
    }
    public int getDyeColor(int slot) {
        return this.candles.get(slot).dyeColor;
    }

    public int getDyeColor() {
        return getDyeColor(0);
    }

    public boolean hasCustomName() {
        return customName != null;
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


    public static double getDistanceToEntity(Entity entity, BlockPos pos) {
        double deltaX = entity.getX() - pos.getX();
        double deltaY = entity.getY() - pos.getY();
        double deltaZ = entity.getZ() - pos.getZ();

        return Math.sqrt((deltaX * deltaX) + (deltaY * deltaY) + (deltaZ * deltaZ));
    }


    @Override
    public AABB getRenderBoundingBox() {
        return super.getRenderBoundingBox().inflate(25, 25, 25);
    }

    public void sync() {

        if(level != null){
            if (!level.isClientSide)
                HexereiPacketHandler.instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new TESyncPacket(worldPosition, save(new CompoundTag())));

            if (this.level != null)
                this.level.sendBlockUpdated(this.worldPosition, this.level.getBlockState(this.worldPosition), this.level.getBlockState(this.worldPosition),
                        Block.UPDATE_CLIENTS);
        }
    }


    public void setDyeColor(int dyeColor){
        this.candles.get(0).dyeColor = dyeColor;
    }
    public void setHeight(int height){
        this.candles.get(0).height = height;
    }
    public void setDyeColor(int candle, int dyeColor){
        this.candles.get(Math.max(0, Math.min(candle, 3))).dyeColor = dyeColor;
    }
    public void setHeight(int candle, int height){
        this.candles.get(Math.max(0, Math.min(candle, 3))).height = height;
    }


    @Override
    public void setChanged() {
        super.setChanged();
        sync();
    }

    public int updateAnalog(){
        int temp = 0;
        int level_of_candles = 0;
        for (int i = 0; i < 4; i++)
            if (candles.get(i).hasCandle)
                level_of_candles += candles.get(i).height;

        float candles = level_of_candles;
        float max = 28;
        float percent = (candles / max);
        temp += (int) Math.ceil(percent * 15);

        if(this.redstoneAnalogSignal != temp) {

            this.redstoneAnalogSignal = temp;
            for(Direction direction : Direction.values()) {
                level.updateNeighborsAt(getBlockPos().relative(direction), this.getBlockState().getBlock());
            }
        }


        if (this.level != null)
            this.level.sendBlockUpdated(this.worldPosition, this.level.getBlockState(this.worldPosition), this.level.getBlockState(this.worldPosition),
                    Block.UPDATE_CLIENTS);

        return temp;

    }




    public void entityInside(Entity entity) {
        BlockPos blockpos = this.getBlockPos();
        if (entity instanceof Projectile projectile) {
            if (Shapes.joinIsNotEmpty(Shapes.create(entity.getBoundingBox().move(-blockpos.getX(), -blockpos.getY(), -blockpos.getZ())), Candle.getShape(getBlockState()), BooleanOp.AND)) {
                if (projectile.isOnFire() && level != null) {
                    if (candles.get(0).hasCandle)
                        candles.get(0).lit = true;
                    if (candles.get(1).hasCandle)
                        candles.get(1).lit = true;
                    if (candles.get(2).hasCandle)
                        candles.get(2).lit = true;
                    if (candles.get(3).hasCandle)
                        candles.get(3).lit = true;
                }
            }
        }
    }


//    @Override
    public void tick() {
        Random random = new Random();

        int candlesLit = 0;

        this.tickCount++;

        for(CandleData candleData : candles) {
            candleData.setOldPos();
            candleData.move();
            if (candleData.getEffect() != null)
                candleData.getEffect().tick(level, this, candleData);
            if(candleData.lit)
                candlesLit++;
        }

        BlockState state = level.getBlockState(worldPosition);
        if(state.hasProperty(Candle.CANDLES_LIT))
            level.setBlock(worldPosition, state.setValue(Candle.CANDLES_LIT, candlesLit).setValue(Candle.LIT, candlesLit > 0), 3);


        {
            int temp = 0;
            int level_of_candles = 0;
            for (int i = 0; i < 4; i++)
                if (candles.get(i).hasCandle)
                    level_of_candles += candles.get(i).height;

            float candles = level_of_candles;
            float max = 28;
            float percent = (candles / max);
            temp += (int) Math.ceil(percent * 15);

            if(this.redstoneAnalogSignal != temp) {

                this.redstoneAnalogSignal = temp;
                for(Direction direction : Direction.values()) {
                    level.updateNeighborsAt(getBlockPos().relative(direction), this.getBlockState().getBlock());
                }
            }
        }


        int hasRedstoneBase = 0;
        for(int i = 0; i < 4; i++){
            if (candles.get(i).base.layer != null && candles.get(i).base.layer.toString().equals("minecraft:redstone_block")) {
                hasRedstoneBase += 1;
            }
        }
        if(this.redstoneBases != hasRedstoneBase) {
            this.redstoneBases = hasRedstoneBase;
            float percent = hasRedstoneBase / 4f;
            int redstoneValue = (int) Math.ceil(percent * 15);
            level.setBlock(worldPosition, state.setValue(Candle.POWER, redstoneValue), 3);
            for (Direction direction : Direction.values()) {
                level.updateNeighborsAt(getBlockPos().relative(direction), getBlockState().getBlock());
            }
        }



        if(!startupFlag) {
            if(!getBlockState().getBlock().asItem().equals(ModItems.CANDLE.get())){
                candles.get(0).height = 7;
                candles.get(0).hasCandle = true;
            }
            candles.get(0).hasCandle = true;
            candles.get(0).meltTimer = CandleData.meltTimerMAX;
            startupFlag = true;

            for(int i = 0; i < 4; i++){
                CandleData candleData = candles.get(i);
                if (candleData.returnToBlock) {
//                    setOffsetPos(i);
                    candleData.moveInstantlyToTarget();
                }
            }
        }

        setOffsetPos();

        if (level.isClientSide) {
            for (CandleData candleData : candles) {
                if (candleData.hasCandle) {
                    if (candleData.lit) {
                        if (random.nextInt(40) == 0 && candleData.getEffect() != null && candleData.getEffect().getParticleType() != null)
                            level.addParticle(candleData.getEffect().getParticleType() != null ? candleData.getEffect().getParticleType() : ParticleTypes.FLAME, worldPosition.getX() + 0.5f + candleData.x, worldPosition.getY() + 3f / 16f + (float) candleData.height / 16f + candleData.y + candleData.baseHeight / 16f, worldPosition.getZ() + 0.5f + candleData.z, (random.nextDouble() - 0.5d) / 100d, (random.nextDouble() + 0.5d) * 0.015d, (random.nextDouble() - 0.5d) / 100d);
                        if (random.nextInt(10) == 0)
                            level.addParticle(ParticleTypes.FLAME, worldPosition.getX() + 0.5f + candleData.x, worldPosition.getY() + 3f / 16f + (float) candleData.height / 16f + candleData.y + candleData.baseHeight / 16f, worldPosition.getZ() + 0.5f + candleData.z, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.015d, (random.nextDouble() - 0.5d) / 50d);
                        if (random.nextInt(10) == 0)
                            level.addParticle(ParticleTypes.SMOKE, worldPosition.getX() + 0.5f + candleData.x, worldPosition.getY() + 3f / 16f + (float) candleData.height / 16f + candleData.y + candleData.baseHeight / 16f, worldPosition.getZ() + 0.5f + candleData.z, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.045d, (random.nextDouble() - 0.5d) / 50d);

                    }
                }
            }
        } else {
            boolean shouldSync = false;
            for(CandleData candleData : candles) {
                if(candleData.hasCandle)
                {
                    if(candleData.lit) {
                        candleData.meltTimer -= candleData.getMeltingSpeedMultiplier();
                        if (candleData.meltTimer <= 0) {
                            candleData.meltTimer = CandleData.meltTimerMAX;
                            candleData.height--;

                            if (candleData.height <= 0) {
                                candleData.hasCandle = false;
                                updateCandleSlots();
                                BlockState blockstate = this.getLevel().getBlockState(this.getBlockPos());
                                if (!level.isClientSide())
                                    this.getLevel().setBlock(this.getBlockPos(), this.getBlockState().setValue(Candle.CANDLES, Math.max(1, blockstate.getValue(Candle.CANDLES) - 1)), 1);
                                level.playSound(null, worldPosition, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 1.0F);

                            }
                            shouldSync = true;
                        }
                    }
                }
            }
            if (shouldSync)
                sync();
            //change to a packet that just updates the candles and then has a boolean if the candle positions got updated (use the same way as moving the other data but only client side do that)
        }

        for (CandleData candleData : candles) {
            if (tickCount - candleData.returnToBlockLastTick > 10)
                candleData.returnToBlock = true;
        }

        if (candles.stream().allMatch((candleData -> !candleData.hasCandle)))
            if(this.getLevel() != null)
                this.getLevel().destroyBlock(this.getBlockPos(), false);


        litStateOld = getBlockState().getValue(Candle.LIT);

    }

    public void setOffsetPos(int index) {
        switch (index) {
            case 0: {
                if(candles.get(0).hasCandle)
                {
                    float xOffset = 0;
                    float zOffset = 0;

                    if (getNumberOfCandles() == 4) {
                        if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
                            xOffset = 3f / 16f;
                            zOffset = 3f / 16f;
                        }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
                            xOffset = -2f / 16f;
                            zOffset = -2f / 16f;
                        }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
                            xOffset = -2f / 16f;
                            zOffset = 3f / 16f;
                        }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
                            xOffset = 2f / 16f;
                            zOffset = -3f / 16f;
                        }
                    }
                    else if (getNumberOfCandles() == 3) {
                        if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
                            xOffset = -1f / 16f;
                            zOffset = 3f / 16f;
                        }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
                            xOffset = 1f / 16f;
                            zOffset = -3f / 16f;
                        }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
                            xOffset = -3f / 16f;
                            zOffset = -1f / 16f;
                        }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
                            xOffset = 3f / 16f;
                            zOffset = 1f / 16f;
                        }
                    }
                    else if (getNumberOfCandles() == 2) {

                        if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
                            xOffset = 3f / 16f;
                            zOffset = -2f / 16f;
                        }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
                            xOffset = -3f / 16f;
                            zOffset = 2f / 16f;
                        }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
                            xOffset = 2f / 16f;
                            zOffset = 3f / 16f;
                        }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
                            xOffset = -2f / 16f;
                            zOffset = -3f / 16f;
                        }
                    }
                    else if (getNumberOfCandles() == 1) {
                        xOffset = 0;
                        zOffset = 0;
                    }

                    candles.get(0).xTarget = xOffset;
                    candles.get(0).yTarget = 0;
                    candles.get(0).zTarget = zOffset;
                }
            }
            case 1: {
                if(candles.get(1).hasCandle)
                {

                    float xOffset = 0;
                    float zOffset = 0;

                    if (getNumberOfCandles() == 4) {
                        if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
                            xOffset = -2f / 16f;
                            zOffset = -3f / 16f;
                        }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
                            xOffset = 3f / 16f;
                            zOffset = 3f / 16f;
                        }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
                            xOffset = 3f / 16f;
                            zOffset = -2f / 16f;
                        }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
                            xOffset = -3f / 16f;
                            zOffset = 2f / 16f;
                        }
                    }
                    else if (getNumberOfCandles() == 3) {
                        if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
                            xOffset = 3f / 16f;
                            zOffset = 1f / 16f;
                        }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
                            xOffset = -3f / 16f;
                            zOffset = -1f / 16f;
                        }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
                            xOffset = -1f / 16f;
                            zOffset = 3f / 16f;
                        }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
                            xOffset = 1f / 16f;
                            zOffset = -3f / 16f;
                        }
                    }
                    else if (getNumberOfCandles() == 2) {

                        if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
                            xOffset = -3f / 16f;
                            zOffset = 3f / 16f;
                        }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
                            xOffset = 3f / 16f;
                            zOffset = -3f / 16f;
                        }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
                            xOffset = -3f / 16f;
                            zOffset = -3f / 16f;
                        }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
                            xOffset = 3f / 16f;
                            zOffset = 3f / 16f;
                        }
                    }
                    else if (getNumberOfCandles() == 1) {
                        xOffset = 0;
                        zOffset = 0;
                    }

                    candles.get(1).xTarget = xOffset;
                    candles.get(1).yTarget = 0;
                    candles.get(1).zTarget = zOffset;
                }
            }
            case 2: {
                if(candles.get(2).hasCandle)
                {

                    float xOffset = 0;
                    float zOffset = 0;


                    if (getNumberOfCandles() == 4) {
                        if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
                            xOffset = -2f / 16f;
                            zOffset = 2f / 16f;
                        }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
                            xOffset = 3f / 16f;
                            zOffset = -2f / 16f;
                        }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
                            xOffset = 2f / 16f;
                            zOffset = 2f / 16f;
                        }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
                            xOffset = -2f / 16f;
                            zOffset = -2f / 16f;
                        }
                    }
                    else if (getNumberOfCandles() == 3) {
                        if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
                            xOffset = -2f / 16f;
                            zOffset = -3f / 16f;
                        }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
                            xOffset = 2f / 16f;
                            zOffset = 3f / 16f;
                        }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
                            xOffset = 3f / 16f;
                            zOffset = -2f / 16f;
                        }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
                            xOffset = -3f / 16f;
                            zOffset = 2f / 16f;
                        }
                    }

                    candles.get(2).xTarget = xOffset;
                    candles.get(2).yTarget = 0;
                    candles.get(2).zTarget = zOffset;
                }
            }
            case 3: {

                if(candles.get(3).hasCandle)
                {

                    float xOffset = 0;
                    float zOffset = 0;


                    if (getNumberOfCandles() == 4) {
                        if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
                            xOffset = 3f / 16f;
                            zOffset = -2f / 16f;
                        }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
                            xOffset = -3f / 16f;
                            zOffset = 2f / 16f;
                        }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
                            xOffset = -2f / 16f;
                            zOffset = -3f / 16f;
                        }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
                            xOffset = 2f / 16f;
                            zOffset = 3f / 16f;
                        }
                    }

                    candles.get(3).xTarget = xOffset;
                    candles.get(3).yTarget = 0;
                    candles.get(3).zTarget = zOffset;
                }
            }
        }
    }
    public void setOffsetPos(){
        setOffsetPos(false);
    }
    public void setOffsetPos(boolean force){
        if(candles.get(0).hasCandle)
        {
            float xOffset = 0;
            float zOffset = 0;

            if (getNumberOfCandles() == 4) {
                if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
                    xOffset = 3f / 16f;
                    zOffset = 3f / 16f;
                }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
                    xOffset = -2f / 16f;
                    zOffset = -2f / 16f;
                }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
                    xOffset = -2f / 16f;
                    zOffset = 3f / 16f;
                }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
                    xOffset = 2f / 16f;
                    zOffset = -3f / 16f;
                }
            }
            else if (getNumberOfCandles() == 3) {
                if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
                    xOffset = -1f / 16f;
                    zOffset = 3f / 16f;
                }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
                    xOffset = 1f / 16f;
                    zOffset = -3f / 16f;
                }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
                    xOffset = -3f / 16f;
                    zOffset = -1f / 16f;
                }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
                    xOffset = 3f / 16f;
                    zOffset = 1f / 16f;
                }
            }
            else if (getNumberOfCandles() == 2) {

                if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
                    xOffset = 3f / 16f;
                    zOffset = -2f / 16f;
                }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
                    xOffset = -3f / 16f;
                    zOffset = 2f / 16f;
                }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
                    xOffset = 2f / 16f;
                    zOffset = 3f / 16f;
                }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
                    xOffset = -2f / 16f;
                    zOffset = -3f / 16f;
                }
            }
            else if (getNumberOfCandles() == 1) {
                xOffset = 0;
                zOffset = 0;
            }

            if (candles.get(0).returnToBlock || force) {
                candles.get(0).xTarget = xOffset;
                candles.get(0).yTarget = 0;
                candles.get(0).zTarget = zOffset;
            }
        }
        if(candles.get(1).hasCandle)
        {

            float xOffset = 0;
            float zOffset = 0;

            if (getNumberOfCandles() == 4) {
                if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
                    xOffset = -2f / 16f;
                    zOffset = -3f / 16f;
                }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
                    xOffset = 3f / 16f;
                    zOffset = 3f / 16f;
                }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
                    xOffset = 3f / 16f;
                    zOffset = -2f / 16f;
                }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
                    xOffset = -3f / 16f;
                    zOffset = 2f / 16f;
                }
            }
            else if (getNumberOfCandles() == 3) {
                if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
                    xOffset = 3f / 16f;
                    zOffset = 1f / 16f;
                }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
                    xOffset = -3f / 16f;
                    zOffset = -1f / 16f;
                }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
                    xOffset = -1f / 16f;
                    zOffset = 3f / 16f;
                }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
                    xOffset = 1f / 16f;
                    zOffset = -3f / 16f;
                }
            }
            else if (getNumberOfCandles() == 2) {

                if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
                    xOffset = -3f / 16f;
                    zOffset = 3f / 16f;
                }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
                    xOffset = 3f / 16f;
                    zOffset = -3f / 16f;
                }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
                    xOffset = -3f / 16f;
                    zOffset = -3f / 16f;
                }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
                    xOffset = 3f / 16f;
                    zOffset = 3f / 16f;
                }
            }
            else if (getNumberOfCandles() == 1) {
                xOffset = 0;
                zOffset = 0;
            }

            if (candles.get(1).returnToBlock || force) {
                candles.get(1).xTarget = xOffset;
                candles.get(1).yTarget = 0;
                candles.get(1).zTarget = zOffset;
            }
        }
        if(candles.get(2).hasCandle)
        {

            float xOffset = 0;
            float zOffset = 0;


            if (getNumberOfCandles() == 4) {
                if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
                    xOffset = -2f / 16f;
                    zOffset = 2f / 16f;
                }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
                    xOffset = 3f / 16f;
                    zOffset = -2f / 16f;
                }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
                    xOffset = 2f / 16f;
                    zOffset = 2f / 16f;
                }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
                    xOffset = -2f / 16f;
                    zOffset = -2f / 16f;
                }
            }
            else if (getNumberOfCandles() == 3) {
                if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
                    xOffset = -2f / 16f;
                    zOffset = -3f / 16f;
                }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
                    xOffset = 2f / 16f;
                    zOffset = 3f / 16f;
                }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
                    xOffset = 3f / 16f;
                    zOffset = -2f / 16f;
                }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
                    xOffset = -3f / 16f;
                    zOffset = 2f / 16f;
                }
            }

            if (candles.get(2).returnToBlock || force) {
                candles.get(2).xTarget = xOffset;
                candles.get(2).yTarget = 0;
                candles.get(2).zTarget = zOffset;
            }
        }
        if(candles.get(3).hasCandle)
        {

            float xOffset = 0;
            float zOffset = 0;


            if (getNumberOfCandles() == 4) {
                if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
                    xOffset = 3f / 16f;
                    zOffset = -2f / 16f;
                }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
                    xOffset = -3f / 16f;
                    zOffset = 2f / 16f;
                }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
                    xOffset = -2f / 16f;
                    zOffset = -3f / 16f;
                }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
                    xOffset = 2f / 16f;
                    zOffset = 3f / 16f;
                }
            }

            if (candles.get(3).returnToBlock || force) {
                candles.get(3).xTarget = xOffset;
                candles.get(3).yTarget = 0;
                candles.get(3).zTarget = zOffset;
            }
        }
    }

    public void updateCandleSlots() {
        if(!candles.get(0).hasCandle)
            updateCandleSlot(0);
        if(!candles.get(1).hasCandle)
            updateCandleSlot(1);
        if(!candles.get(2).hasCandle)
            updateCandleSlot(2);
    }

    public void updateCandleSlot(int slot){
        CandleData newData = new CandleData();
        newData.load(candles.get(slot + 1).save());

        candles.set(slot, newData);
        candles.set(slot + 1, new CandleData());
    }


}

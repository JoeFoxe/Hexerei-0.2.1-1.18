package net.joefoxe.hexerei.item.custom;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.ModEntityTypes;
import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.config.ModKeyBindings;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.util.CachedMap;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class BroomItem extends BroomStickItem {
    private static final Predicate<Entity> field_219989_a = EntitySelector.NO_SPECTATORS.and(Entity::canBeCollidedWith);
    private final String type;

    private final CachedMap<ItemStack, BroomEntity> cachedBroom;

    public static final Comparator<ItemStack> ITEM_COMPARATOR = (item1, item2) -> {
        int cmp = item2.getItem().hashCode() - item1.getItem().hashCode();
        if (cmp != 0) {
            return cmp;
        }
        cmp = item2.getDamageValue() - item1.getDamageValue();
        if (cmp != 0) {
            return cmp;
        }
//        CompoundTag c1 = item1.getTag();
//        CompoundTag c2 = item2.getTag();
//
//        if (c1 == null && c2 == null) {
//            return 0;
//        } else if (c1 == null) {
//            return 1;
//        } else if (c2 == null) {
//            return -1;
//        }

        return item1.getComponents().hashCode() - item2.getComponents().hashCode();
    };

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(30) {};
    }


    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        ItemStackHandler handler = createHandler();

        handler.deserializeNBT(Hexerei.proxy.getLevel().registryAccess(), stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getCompound("Inventory"));

        return Optional.of(new BroomItem.BroomItemToolTip(handler, stack));
    }

    public record BroomItemToolTip(ItemStackHandler handler, ItemStack self) implements TooltipComponent {
    }


    public BroomItem(String broomType, Item.Properties properties) {
        super(properties);
        this.type = broomType;
        cachedBroom = new CachedMap<>(10_000, ITEM_COMPARATOR);
    }

    //override in item creation to change the offset for brooms that have a different brush placement
    public Vec3 getBrushOffset(){
        return new Vec3(0, 0, 0);
    }
    public Vec3 getSatchelOffset(){
        return new Vec3(0, 0, 0);
    }
    public Vec3 getTipOffset(){
        return new Vec3(0, 0, 0);
    }

    public static UUID getUUID(ItemStack stack) {

        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (tag.contains("broomUUID")) {
            return tag.getUUID("broomUUID");
        }
        UUID newUUID = UUID.randomUUID();
        tag.putUUID("broomUUID", newUUID);
        return newUUID;
    }


    public BroomEntity getBroom(Level world, ItemStack stack) {

        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        BroomEntity broom = new BroomEntity(ModEntityTypes.BROOM.get(), world);
        if(tag.contains("floatMode"))
            broom.itemHandler.deserializeNBT(world.registryAccess(), tag.getCompound("Inventory"));
        else
            broom.itemHandler.setStackInSlot(2, new ItemStack(ModItems.BROOM_BRUSH.get()));
        if(stack.getItem() instanceof BroomItem broomItem)
            broom.setBroomType(broomItem.type);
        broom.isItem = true;
        broom.selfItem = stack.copy();

        if (stack.get(DataComponents.CUSTOM_NAME) != null) {
            broom.setCustomName(stack.getHoverName());
        }

        return broom;
    }


    public BroomEntity getBroomFast(Level world, ItemStack stack) {
        return cachedBroom.get(stack, () -> getBroom(world, stack));
    }

    /**
     * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
     * {@link #use}.
     */
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        HitResult raytraceresult = getPlayerPOVHitResult(worldIn, playerIn, ClipContext.Fluid.ANY);
        if (raytraceresult.getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(itemstack);
        } else {
            Vec3 vector3d = playerIn.getLookAngle();
            double d0 = 5.0D;
            List<Entity> list = worldIn.getEntities(playerIn, playerIn.getBoundingBox().expandTowards(vector3d.scale(5.0D)).inflate(1.0D), field_219989_a);
            if (!list.isEmpty()) {
                Vec3 vector3d1 = playerIn.getEyePosition(1.0F);

                for(Entity entity : list) {
                    AABB axisalignedbb = entity.getBoundingBox().inflate(entity.getPickRadius());
                    if (axisalignedbb.contains(vector3d1)) {
                        return InteractionResultHolder.pass(itemstack);
                    }
                }
            }

            if (raytraceresult.getType() == HitResult.Type.BLOCK) {
                BroomEntity broom = new BroomEntity(worldIn, raytraceresult.getLocation().x, raytraceresult.getLocation().y, raytraceresult.getLocation().z);
                if(itemstack.getItem() instanceof BroomItem broomItem)
                    broom.setBroomType(broomItem.type);
                broom.broomUUID = BroomItem.getUUID(itemstack);
                broom.setYRot(playerIn.getYRot());
                CompoundTag tag = itemstack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
                broom.itemHandler.deserializeNBT(worldIn.registryAccess(), tag.getCompound("Inventory"));
                if(!tag.contains("floatMode")) {
                    broom.itemHandler.setStackInSlot(2, new ItemStack(ModItems.BROOM_BRUSH.get()));
                    broom.sync();
                }
                broom.floatMode = (tag.getBoolean("floatMode"));

                broom.setCustomName(itemstack.getHoverName());

                if (!worldIn.noCollision(broom, broom.getBoundingBox().inflate(-0.1D))) {
                    return InteractionResultHolder.fail(itemstack);
                } else {
                    if (!worldIn.isClientSide) {

                        worldIn.addFreshEntity(broom);
                        if (!playerIn.getAbilities().instabuild) {
                            itemstack.shrink(1);
                        }
                    }

                    playerIn.awardStat(Stats.ITEM_USED.get(this));
                    return InteractionResultHolder.sidedSuccess(itemstack, worldIn.isClientSide());
                }
            } else {
                return InteractionResultHolder.pass(itemstack);
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {

        if(Screen.hasShiftDown()) {

            tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_shift_2", Component.translatable(ModKeyBindings.broomDescend.getKey().getName()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xCCCC00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_shift_3").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_shift_4").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            if(stack.is(ModItems.MAHOGANY_BROOM.get())) {
                tooltipComponents.add(Component.translatable(""));
                tooltipComponents.add(Component.translatable("tooltip.hexerei.mahogany_broom_shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                tooltipComponents.add(Component.translatable("tooltip.hexerei.mahogany_broom_shift_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
            else {
                tooltipComponents.add(Component.translatable(""));
                tooltipComponents.add(Component.translatable("tooltip.hexerei.willow_broom_shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
                tooltipComponents.add(Component.translatable("tooltip.hexerei.willow_broom_shift_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
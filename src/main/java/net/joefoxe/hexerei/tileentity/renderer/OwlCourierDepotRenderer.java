package net.joefoxe.hexerei.tileentity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.OwlCourierDepot;
import net.joefoxe.hexerei.block.custom.OwlCourierDepotWall;
import net.joefoxe.hexerei.data.owl.ClientOwlCourierDepotData;
import net.joefoxe.hexerei.data.owl.OwlCourierDepotData;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.tileentity.OwlCourierDepotTile;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FastColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

import java.util.List;
import java.util.Random;

public class OwlCourierDepotRenderer implements BlockEntityRenderer<OwlCourierDepotTile> {

    private final Font font;

    private final Minecraft minecraft = Minecraft.getInstance();
    private final ItemRenderer itemRenderer = minecraft.getItemRenderer();
    private final ItemModelShaper shaper = itemRenderer.getItemModelShaper();

    public OwlCourierDepotRenderer() {
        super();
        this.font = Minecraft.getInstance().font;
    }

    @Override
    public void render(OwlCourierDepotTile tileEntityIn, float partialTicks, PoseStack matrixStackIn,
                       MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {


        BlockState state = tileEntityIn.getLevel().getBlockState(tileEntityIn.getBlockPos());
        if(!state.hasBlockEntity() || !(tileEntityIn.getLevel().getBlockEntity(tileEntityIn.getBlockPos()) instanceof OwlCourierDepotTile owlCourierDepotTile))
            return;

        int col = 0x464F56;
        int colr = (int)((double) FastColor.ARGB32.red(col) * 0.4D);
        int colg = (int)((double) FastColor.ARGB32.green(col) * 0.4D);
        int colb = (int)((double) FastColor.ARGB32.blue(col) * 0.4D);
        int i1 = FastColor.ARGB32.color( 0, colr, colg, colb);

        if (state.getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
            matrixStackIn.translate(8D / 16D, 5.75D / 16D, 13.75D / 16D);
        } else if (state.getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
            matrixStackIn.translate(8D / 16D, 5.75D / 16D, 1 - 13.75D / 16D);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(180));
        } else if (state.getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
            matrixStackIn.translate(1 - 13.75D / 16D, 5.75D / 16D, 8D / 16);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(270));
        } else if (state.getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
            matrixStackIn.translate(13.75D / 16D, 5.75D / 16D, 8D / 16);
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(90));
        }


        if (!(state.getBlock() instanceof OwlCourierDepotWall))
            matrixStackIn.translate(0D / 16D, -5D / 16D, 0D / 16);



        if (ClientOwlCourierDepotData.getDepots().containsKey(GlobalPos.of(tileEntityIn.getLevel().dimension(), tileEntityIn.getBlockPos()))) {
            OwlCourierDepotData depotData = ClientOwlCourierDepotData.getDepots().get(GlobalPos.of(tileEntityIn.getLevel().dimension(), tileEntityIn.getBlockPos()));

            int packages = 0;
            int letters = 0;
            for (ItemStack stack : depotData.items) {
                if (stack.getItem() == ModItems.COURIER_PACKAGE.get()) {
                    packages += 1;
                }
                if (stack.getItem() == ModItems.COURIER_LETTER.get()) {
                    letters += 1;
                }
            }

            Random random = new Random(4200);

            float packageOffset = 0.5f;
            for (int i = 0; i < packages; i++) {
                matrixStackIn.pushPose();
                matrixStackIn.translate(-6.3D / 16D, (packageOffset) / 16D, -12.2D / 16);
                matrixStackIn.scale(0.8f, 0.8f, 0.8f);
                matrixStackIn.translate(8D / 16D, 0D / 16D, 8D / 16);
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(random.nextInt(360) * i));
                matrixStackIn.translate(-8D / 16D, 0D / 16D, -8D / 16);
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.COURIER_PACKAGE.get().defaultBlockState());
                matrixStackIn.popPose();
                packageOffset += 5.7f;
            }

            packageOffset += 0.1f;

            for (int i = 0; i < letters; i++) {
                matrixStackIn.pushPose();
                matrixStackIn.translate((-6.3D - random.nextFloat(0.8f * (letters - i)) + 0.4f * (letters - i)) / 16D, (packageOffset) / 16D, (-12.2D - random.nextFloat(0.8f * (letters - i)) + 0.4f * (letters - i)) / 16);
                matrixStackIn.scale(0.8f, 0.8f, 0.8f);
                matrixStackIn.translate(8D / 16D, 0D / 16D, 8D / 16);
                matrixStackIn.mulPose(Axis.YP.rotationDegrees(random.nextInt(360) * i));
                matrixStackIn.translate(-8D / 16D, 0D / 16D, -8D / 16);
                renderBlock(matrixStackIn, bufferIn, combinedLightIn, ModBlocks.COURIER_LETTER.get().defaultBlockState());
                matrixStackIn.popPose();
                packageOffset += 0.2f;
            }

        }


        if (state.getBlock() instanceof OwlCourierDepotWall)
            matrixStackIn.translate(0D / 16D, -1D / 16D, 0D / 16);
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(-22));
        matrixStackIn.translate(0D / 16D, 1D / 16D, 0D / 16);
        matrixStackIn.scale(0.00694445f, -0.00694445f, 0.00694445f);



        Component component = (Component.literal("")).withStyle(Style.EMPTY.withColor(0xAAAAAA)); //tileEntityIn.customName
        if(owlCourierDepotTile.hasCustomName() && owlCourierDepotTile.getCustomName().getString().length() > 0)
            component = owlCourierDepotTile.getCustomName();

        List<FormattedCharSequence> list = Minecraft.getInstance().font.split(component, 512);
        if(list.size() > 0){
            int width = minecraft.font.width(list.get(0));
            float lineHeight = minecraft.font.lineHeight / 2f;
            if (width > 70) {
                float percent = width / 70f;
                matrixStackIn.pushPose();
                matrixStackIn.scale(1 / percent, 1 / percent, 1 / percent);
//            minecraft.font.drawInBatch(list.get(0), 7 * percent, (5f + lineHeight) * percent - 4.5f, 0xFF404040, false, matrixStackIn.last().pose(), bufferIn, Font.DisplayMode.NORMAL, 0, 15728880);
                Minecraft.getInstance().font.drawInBatch8xOutline(list.get(0), -width / 2f, lineHeight * percent, i1, 0x222222, matrixStackIn.last().pose(), bufferIn, combinedLightIn);
                matrixStackIn.popPose();

            } else {
//            minecraft.font.drawInBatch(list.get(0), 7, 5f + lineHeight - 4.5f, 0xFF404040, false, matrixStackIn.last().pose(), bufferIn, Font.DisplayMode.NORMAL, 0, 15728880);
                Minecraft.getInstance().font.drawInBatch8xOutline(list.get(0), -width / 2f, lineHeight, i1, 0x222222, matrixStackIn.last().pose(), bufferIn, combinedLightIn);
            }
        }



//        FormattedCharSequence ireorderingprocessor = tileEntityIn.reorderText(0, (p_243502_1_) -> {
//            List<FormattedCharSequence> list = font.split(p_243502_1_, 90);
//            return list.isEmpty() ? FormattedCharSequence.EMPTY : list.get(0);
//        });
//        if (ireorderingprocessor != null) {
//            float f3 = (float)(-font.width(ireorderingprocessor) / 2);
//            font.drawInBatch(ireorderingprocessor, f3, 0, i1, false, matrixStackIn.last().pose(), bufferIn, false, 0, combinedLightIn);
//        }

    }



    private void renderItem(ItemStack stack, Level level, PoseStack matrixStackIn, MultiBufferSource bufferIn,
                            int combinedLightIn) {
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, combinedLightIn,
                OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, level, 1);
    }



    private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state) {
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);

    }

    private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn, BlockState state, RenderType renderType, int color) {
        renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, ModelData.EMPTY, renderType, color);

    }

    public void renderSingleBlock(BlockState p_110913_, PoseStack p_110914_, MultiBufferSource p_110915_, int p_110916_, int p_110917_, ModelData modelData, RenderType renderType, int color) {
        RenderShape rendershape = p_110913_.getRenderShape();
        if (rendershape != RenderShape.INVISIBLE) {
            switch (rendershape) {
                case MODEL -> {
                    BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
                    BakedModel bakedmodel = dispatcher.getBlockModel(p_110913_);
                    int i = color;
                    float f = (float) (i >> 16 & 255) / 255.0F;
                    float f1 = (float) (i >> 8 & 255) / 255.0F;
                    float f2 = (float) (i & 255) / 255.0F;
                    for (RenderType rt : bakedmodel.getRenderTypes(p_110913_, RandomSource.create(42), modelData))
                        dispatcher.getModelRenderer().renderModel(p_110914_.last(), p_110915_.getBuffer(renderType != null ? renderType : net.minecraftforge.client.RenderTypeHelper.getEntityRenderType(rt, false)), p_110913_, bakedmodel, f, f1, f2, p_110916_, p_110917_, modelData, rt);
                }
                case ENTITYBLOCK_ANIMATED -> {
                    ItemStack stack = new ItemStack(p_110913_.getBlock());
                    net.minecraftforge.client.extensions.common.IClientItemExtensions.of(stack).getCustomRenderer().renderByItem(stack, ItemDisplayContext.NONE, p_110914_, p_110915_, p_110916_, p_110917_);
                }
            }

        }
    }


}

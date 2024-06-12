package net.joefoxe.hexerei.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.custom.PickableDoublePlant;
import net.joefoxe.hexerei.events.CrowWhitelistEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class CrowWhitelistRenderer implements IGuiOverlay {
    private static final ResourceLocation GUI = new ResourceLocation(Hexerei.MOD_ID,
            "textures/gui/crow_gui.png");
    @Override //(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight)
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        PoseStack poseStack = guiGraphics.pose();
        if(CrowWhitelistEvent.whiteListingCrow != null) {
            gui.setupOverlayRenderState(true, false);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, GUI);
            guiGraphics.blit(GUI, screenWidth / 2 - 9, screenHeight - 42, 238, 178, 18, 18, 256, 256);

            InventoryScreen.renderEntityInInventoryFollowsAngle(guiGraphics, screenWidth / 2, screenHeight - 78, 40, (float)Math.toRadians(-50), (float)Math.toRadians(10), CrowWhitelistEvent.whiteListingCrow);

            if(!CrowWhitelistEvent.whiteListingCrow.harvestWhitelist.isEmpty()){
                RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                poseStack.pushPose();
                poseStack.translate(screenWidth / 2f - 14 - ((CrowWhitelistEvent.whiteListingCrow.harvestWhitelist.size() - 1) / 2f * 21), screenHeight - 40, 100.0F);
                poseStack.translate(8.0F, -8.0F, 0.0F);
                poseStack.scale(12.0F, 12.0F, 12.0F);
                poseStack.mulPoseMatrix(new Matrix4f().scale(1, -1, 1));
                Vec3 rotationOffset = new Vec3(0.5f, 0, 0.5f);

                MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

                Lighting.setupFor3DItems();
                poseStack.last().normal().rotate(Axis.YP.rotationDegrees((float) -90));
                for(int itor = 0; itor < CrowWhitelistEvent.whiteListingCrow.harvestWhitelist.size(); itor++){
                    poseStack.pushPose();
                    poseStack.translate(itor * 1.7f, Math.sin((Hexerei.getClientTicks() + itor * 30) / 30) / 4, 0.0F);

                    float zRot = 0;
                    float xRot = 20;
                    float yRot = 30 + (Hexerei.getClientTicks()) + itor * 30;

                    poseStack.translate(rotationOffset.x, rotationOffset.y, rotationOffset.z);
                    poseStack.mulPose(Axis.ZP.rotationDegrees(zRot));
                    poseStack.mulPose(Axis.XP.rotationDegrees(xRot));
                    poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
                    poseStack.translate(-rotationOffset.x, -rotationOffset.y, -rotationOffset.z);

                    BlockState state = CrowWhitelistEvent.whiteListingCrow.harvestWhitelist.get(itor).defaultBlockState();
                    if (state.hasProperty(BlockStateProperties.AGE_1))
                        state = state.setValue(BlockStateProperties.AGE_1, Mth.clamp((int) (((Math.sin((Hexerei.getClientTicks() + itor * 30) / 30) + 1) / 2) * 2), 0, 1));
                    else if (state.hasProperty(BlockStateProperties.AGE_2))
                        state = state.setValue(BlockStateProperties.AGE_2, Mth.clamp((int) (((Math.sin((Hexerei.getClientTicks() + itor * 30) / 30) + 1) / 2) * 3), 0, 2));
                    else if (state.hasProperty(BlockStateProperties.AGE_3))
                        state = state.setValue(BlockStateProperties.AGE_3, Mth.clamp((int) (((Math.sin((Hexerei.getClientTicks() + itor * 30) / 30) + 1) / 2) * 4), 0, 3));
                    else if (state.hasProperty(BlockStateProperties.AGE_4))
                        state = state.setValue(BlockStateProperties.AGE_4, Mth.clamp((int)(((Math.sin((Hexerei.getClientTicks() + itor * 30) / 30) + 1) / 2) * 5), 0, 4));
                    else if (state.hasProperty(BlockStateProperties.AGE_5))
                        state = state.setValue(BlockStateProperties.AGE_5, Mth.clamp((int)(((Math.sin((Hexerei.getClientTicks() + itor * 30) / 30) + 1) / 2) * 6), 0, 5));
                    else if (state.hasProperty(BlockStateProperties.AGE_7))
                        state = state.setValue(BlockStateProperties.AGE_7, Mth.clamp((int)(((Math.sin((Hexerei.getClientTicks() + itor * 30) / 30) + 1) / 2) * 8), 0, 7));
                    if(state.hasProperty(BlockStateProperties.BERRIES))
                        state = state.setValue(BlockStateProperties.BERRIES, true);
                    renderBlock(poseStack, buffer, LightTexture.FULL_BRIGHT, state, 0xFFFFFFFF);
                    if(state.hasProperty(PickableDoublePlant.HALF)){
                        poseStack.pushPose();
                        poseStack.translate(0F, 1, 0.0F);
                        state = state.setValue(PickableDoublePlant.HALF, DoubleBlockHalf.UPPER);
                        renderBlock(poseStack, buffer, LightTexture.FULL_BRIGHT, state, 0xFFFFFFFF);
                        poseStack.popPose();
                    }
                    poseStack.popPose();
                }
                buffer.endBatch();
                poseStack.popPose();
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void renderBlock(PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLightIn, BlockState state, int color) {
        renderSingleBlock(state, matrixStack, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, color);

    }

    @OnlyIn(Dist.CLIENT)
    public void renderSingleBlock(BlockState p_110913_, PoseStack poseStack, MultiBufferSource p_110915_, int p_110916_, int p_110917_, ModelData modelData, int color) {
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
                    dispatcher.getModelRenderer().renderModel(poseStack.last(), p_110915_.getBuffer(ItemBlockRenderTypes.getRenderType(p_110913_, false)), p_110913_, bakedmodel, f, f1, f2, p_110916_, p_110917_, modelData, null);
                }
                case ENTITYBLOCK_ANIMATED -> {
                    ItemStack stack = new ItemStack(p_110913_.getBlock());
                    poseStack.translate(0.2, -0.1, -0.1);
                    IClientItemExtensions.of(stack.getItem()).getCustomRenderer().renderByItem(stack, ItemDisplayContext.NONE, poseStack, p_110915_, p_110916_, p_110917_);
                }
            }

        }
    }
}
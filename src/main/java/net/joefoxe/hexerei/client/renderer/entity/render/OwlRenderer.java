package net.joefoxe.hexerei.client.renderer.entity.render;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.client.renderer.entity.custom.OwlEntity;
import net.joefoxe.hexerei.client.renderer.entity.model.OwlModel;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.custom.BroomItem;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.ModelData;

import java.util.Map;

public class OwlRenderer extends MobRenderer<OwlEntity, OwlModel<OwlEntity>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(Hexerei.MOD_ID, "textures/entity/owl.png");

    private static final Map<OwlVariant, ResourceLocation> LOCATION_BY_VARIANT = Util.make(Maps.newEnumMap(OwlVariant.class), (p_114874_) -> {
        p_114874_.put(OwlVariant.GREAT_HORNED, new ResourceLocation(Hexerei.MOD_ID, "textures/entity/owl.png"));
        p_114874_.put(OwlVariant.BARN, new ResourceLocation(Hexerei.MOD_ID, "textures/entity/owl_barn.png"));
        p_114874_.put(OwlVariant.BARRED, new ResourceLocation(Hexerei.MOD_ID, "textures/entity/owl_barred.png"));
        p_114874_.put(OwlVariant.SNOWY, new ResourceLocation(Hexerei.MOD_ID, "textures/entity/owl_snowy.png"));
    });
    public OwlRenderer(Context erm) {

        super(erm, new OwlModel<>(erm.bakeLayer(OwlModel.LAYER_LOCATION)), 0.25f);

        this.addLayer(new LayerOwlItem(this));
        this.addLayer(new LayerOwlCollar(this));
        this.addLayer(new LayerOwlHelmet(this, erm));
    }


    @Override
    public ResourceLocation getTextureLocation(OwlEntity pEntity) {
        return LOCATION_BY_VARIANT.get(pEntity.getVariant());
    }

    @Override
    public void render(OwlEntity crowEntity, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        super.render(crowEntity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    protected void scale(OwlEntity entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
        float f = 1;
        if (entitylivingbaseIn.isBaby()) {
            f *= 0.5f;
            this.shadowRadius = 0.125F;
        } else {
            this.shadowRadius = 0.25F;
        }

        matrixStackIn.scale(f, f, f);
    }

    public static class LayerOwlItem extends RenderLayer<OwlEntity, OwlModel<OwlEntity>> {

        public LayerOwlItem(OwlRenderer render) {
            super(render);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, OwlEntity owl, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            ItemStack itemstack = owl.getItem(1);
            matrixStackIn.pushPose();
            translateToHand(matrixStackIn);
            matrixStackIn.translate(0, 0.075F, 0.05);
            if(itemstack.getItem() instanceof BroomItem)
                matrixStackIn.translate(0.1f, 0.12f, 0.06F);
            if(owl.isBaby()){
                matrixStackIn.scale(0.75F, 0.75F, 0.75F);
            }
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(-2.5F));
            if(itemstack.getItem() instanceof BroomItem)
                matrixStackIn.mulPose(Axis.XP.rotationDegrees(-90F));
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(-35F));
            matrixStackIn.scale(0.75F, 0.75F, 0.75F);
            ItemStack stack = itemstack.copy();

            Minecraft.getInstance().gameRenderer.itemInHandRenderer.renderItem(owl, stack, ItemDisplayContext.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
            matrixStackIn.popPose();

            if (owl.messagingController.hasDelivery() && !owl.messagingController.getMessageStack().isEmpty()) {
                if (owl.messagingController.getMessageStack().getItem() == ModItems.COURIER_LETTER.get()){
                    matrixStackIn.pushPose();
                    translateToFeet(matrixStackIn);
                    matrixStackIn.translate(0f, 0.1f, 0f);
                    matrixStackIn.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, owl.itemHeldSwingLast, owl.itemHeldSwing) * 1.5f));
                    matrixStackIn.translate(-0.42f, 0.0f, -0.7f);
                    matrixStackIn.scale(1, 1, 1);

                    renderBlock(matrixStackIn, bufferIn, packedLightIn, ModBlocks.COURIER_LETTER.get().defaultBlockState());
                    matrixStackIn.popPose();
                }

                if (owl.messagingController.getMessageStack().getItem() == ModItems.COURIER_PACKAGE.get()) {
                    matrixStackIn.pushPose();
                    translateToFeet(matrixStackIn);
                    matrixStackIn.mulPose(Axis.XP.rotationDegrees(180));
                    matrixStackIn.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, owl.itemHeldSwingLast, owl.itemHeldSwing)));
                    matrixStackIn.translate(0.57f, 0.35f, -0.175f);
                    matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180));
                    matrixStackIn.scale(1, 1, 1);

                    renderBlock(matrixStackIn, bufferIn, packedLightIn, ModBlocks.COURIER_PACKAGE.get().defaultBlockState());
                    matrixStackIn.popPose();
                }
            }
//            }
        }

        private void renderBlock(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, BlockState state) {
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);

        }

        protected void translateToHand(PoseStack matrixStack) {
            this.getParentModel().owl.translateAndRotate(matrixStack);
            this.getParentModel().owl.getChild("head").translateAndRotate(matrixStack);
            this.getParentModel().owl.getChild("head").getChild("beak").translateAndRotate(matrixStack);

        }

        protected void translateToFeet(PoseStack matrixStack) {
            this.getParentModel().owl.translateAndRotate(matrixStack);
            this.getParentModel().owl.getChild("rightLeg").translateAndRotate(matrixStack);

        }
    }


    @OnlyIn(Dist.CLIENT)
    public static class LayerOwlCollar extends RenderLayer<OwlEntity, OwlModel<OwlEntity>> {
        private static final ResourceLocation OWL_DYE_LOCATION = new ResourceLocation(Hexerei.MOD_ID, "textures/entity/owl_dye.png");

        public LayerOwlCollar(RenderLayerParent<OwlEntity, OwlModel<OwlEntity>> p_117707_) {
            super(p_117707_);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, OwlEntity entity, float p_117724_, float p_117725_, float p_117726_, float p_117727_, float p_117728_, float p_117729_) {
            if (entity.isTame() && !entity.isInvisible() && (entity.getDyeColorId() != -1 || entity.getName().getString().equals("jeb_") || entity.getName().getString().equals("joe_"))) {
                float[] afloat = entity.getDyeColor().getTextureDiffuseColors();
                VertexConsumer vertexConsumer = ItemRenderer.getFoilBuffer(bufferIn, RenderType.entityCutoutNoCull(OWL_DYE_LOCATION), false, false);
                this.getParentModel().renderToBuffer(matrixStackIn, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, afloat[0], afloat[1], afloat[2], 1.0F);

            }
        }
    }


    public static class LayerOwlHelmet extends RenderLayer<OwlEntity, OwlModel<OwlEntity>>{

        private final RenderLayerParent<OwlEntity, OwlModel<OwlEntity>>renderer;
        private final HumanoidModel<?> defaultBipedModel;

        public LayerOwlHelmet(OwlRenderer renderer, Context renderManagerIn){
            super(renderer);
            this.renderer = renderer;
            defaultBipedModel = new HumanoidModel<>(renderManagerIn.bakeLayer(ModelLayers.ARMOR_STAND_OUTER_ARMOR));

        }

        @Override
        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, OwlEntity crow, float p_117353_, float p_117354_, float p_117355_, float p_117356_, float p_117357_, float p_117358_) {

            matrixStackIn.pushPose();
            ItemStack itemstack = crow.itemHandler.getStackInSlot(0);
            if (itemstack.getItem() instanceof ArmorItem armoritem) {

                HumanoidModel<?> a = defaultBipedModel;
                a = getArmorModelHook(crow, itemstack, EquipmentSlot.HEAD, a);
                boolean notAVanillaModel = a != defaultBipedModel;
                this.setModelSlotVisible(a, EquipmentSlot.HEAD);
                translateToHead(matrixStackIn);

                matrixStackIn.scale(0.65F, 0.65F, 0.65F);
                matrixStackIn.translate(0f,  0.2F, -0.05F);
                boolean flag1 = itemstack.hasFoil();
                if (armoritem instanceof DyeableLeatherItem) { // Allow this for anything, not only cloth
                    int i = ((DyeableLeatherItem) armoritem).getColor(itemstack);
                    float f = (float) (i >> 16 & 255) / 255.0F;
                    float f1 = (float) (i >> 8 & 255) / 255.0F;
                    float f2 = (float) (i & 255) / 255.0F;
                    renderHelmet(crow, matrixStackIn, bufferIn, packedLightIn, flag1, a, f, f1, f2, getArmorResource(crow, itemstack, EquipmentSlot.HEAD, null), notAVanillaModel);
                    renderHelmet(crow, matrixStackIn, bufferIn, packedLightIn, flag1, a, 1.0F, 1.0F, 1.0F, getArmorResource(crow, itemstack, EquipmentSlot.HEAD, "overlay"), notAVanillaModel);
                } else {
                    renderHelmet(crow, matrixStackIn, bufferIn, packedLightIn, flag1, a, 1.0F, 1.0F, 1.0F, getArmorResource(crow, itemstack, EquipmentSlot.HEAD, null), notAVanillaModel);
                }
            }
            else if((Block.byItem(itemstack.getItem())) instanceof AbstractSkullBlock)
            {
                translateToHand(matrixStackIn);
                matrixStackIn.scale(0.45F, 0.45F, 0.45F);
                matrixStackIn.translate(0f, -0.25F, -0.2F);
                matrixStackIn.mulPose(Axis.ZP.rotationDegrees(180));
                renderItem(itemstack, crow.level(), matrixStackIn, bufferIn, packedLightIn);
            }

            matrixStackIn.popPose();



        }

        private void renderItem(ItemStack stack, Level level, PoseStack matrixStackIn, MultiBufferSource bufferIn,
                                int combinedLightIn) {
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, combinedLightIn,
                    OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn, level, 1);
        }

        private static final Map<String, ResourceLocation> ARMOR_TEXTURE_RES_MAP = Maps.newHashMap();

        private void translateToHead(PoseStack matrixStackIn) {
            translateToChest(matrixStackIn);
            this.renderer.getModel().head.translateAndRotate(matrixStackIn);
        }

        private void translateToChest(PoseStack matrixStackIn) {
            this.renderer.getModel().owl.translateAndRotate(matrixStackIn);
        }

        private void renderHelmet(OwlEntity entity, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, boolean glintIn, HumanoidModel modelIn, float red, float green, float blue, ResourceLocation armorResource, boolean notAVanillaModel) {
            VertexConsumer ivertexbuilder = ItemRenderer.getFoilBuffer(bufferIn, RenderType.entityCutoutNoCull(armorResource), false, glintIn);
            renderer.getModel().copyPropertiesTo(modelIn);
            modelIn.head.xRot = 0F;
            modelIn.head.yRot = 0F;
            modelIn.head.zRot = 0F;
            modelIn.hat.xRot = 0F;
            modelIn.hat.yRot = 0F;
            modelIn.hat.zRot = 0F;
            modelIn.head.x = 0F;
            modelIn.head.y = 0F;
            modelIn.head.z = 0F;
            modelIn.hat.x = 0F;
            modelIn.hat.y = 0F;
            modelIn.hat.z = 0F;
            modelIn.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);

        }

        public static ResourceLocation getArmorResource(Entity entity, ItemStack stack, EquipmentSlot slot, @javax.annotation.Nullable String type) {
            ArmorItem item = (ArmorItem)stack.getItem();
            String texture = item.getMaterial().getName();
            String domain = "minecraft";
            int idx = texture.indexOf(':');
            if (idx != -1) {
                domain = texture.substring(0, idx);
                texture = texture.substring(idx + 1);
            }
            String s1 = String.format("%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, (1), type == null ? "" : String.format("_%s", type));

            s1 = ForgeHooksClient.getArmorTexture(entity, stack, s1, slot, type);
            ResourceLocation resourcelocation = ARMOR_TEXTURE_RES_MAP.get(s1);

            if (resourcelocation == null) {
                resourcelocation = new ResourceLocation(s1);
                ARMOR_TEXTURE_RES_MAP.put(s1, resourcelocation);
            }

            return resourcelocation;
        }

        protected void setModelSlotVisible(HumanoidModel humanoidModel, EquipmentSlot slotIn) {
            this.setModelVisible(humanoidModel);
            switch (slotIn) {
                case HEAD -> humanoidModel.head.visible = true;
                case CHEST -> {
                    humanoidModel.body.visible = true;
                    humanoidModel.rightArm.visible = true;
                    humanoidModel.leftArm.visible = true;
                }
                case LEGS -> {
                    humanoidModel.body.visible = true;
                    humanoidModel.rightLeg.visible = true;
                    humanoidModel.leftLeg.visible = true;
                }
                case FEET -> {
                    humanoidModel.rightLeg.visible = true;
                    humanoidModel.leftLeg.visible = true;
                }
            }
        }

        protected void setModelVisible(HumanoidModel model) {
            model.setAllVisible(false);

        }

        protected HumanoidModel<?> getArmorModelHook(LivingEntity entity, ItemStack itemStack, EquipmentSlot slot, HumanoidModel model) {
            Model basicModel = ForgeHooksClient.getArmorModel(entity, itemStack, slot, model);
            return basicModel instanceof HumanoidModel ? (HumanoidModel<?>) basicModel : model;
        }

        protected void translateToHand(PoseStack matrixStack) {
            this.getParentModel().owl.translateAndRotate(matrixStack);
            this.getParentModel().owl.getChild("head").translateAndRotate(matrixStack);

        }

    }

}
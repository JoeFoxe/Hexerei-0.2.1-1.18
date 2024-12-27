package net.joefoxe.hexerei.client.renderer.entity.render;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.custom.ModChestBoatEntity;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;

import java.util.Map;
import java.util.stream.Stream;


public class ModChestBoatRenderer extends EntityRenderer<ModChestBoatEntity> {

    private final Map<ModChestBoatEntity.Type, Pair<ResourceLocation, ChestBoatModel>> boatResources;

    public ModChestBoatRenderer(EntityRendererProvider.Context p_234563_) {
        super(p_234563_);
        this.shadowRadius = 0.8F;
        this.boatResources = Stream.of(ModChestBoatEntity.Type.values()).collect(ImmutableMap.toImmutableMap((p_173938_) -> {
            return p_173938_;
        }, (p_234575_) -> {
            return Pair.of(HexereiUtil.getResource(getTextureLocation(p_234575_)), this.createBoatModel(p_234563_, p_234575_));
        }));
    }

    private ChestBoatModel createBoatModel(EntityRendererProvider.Context p_234569_, ModChestBoatEntity.Type p_234570_) {
        ModelLayerLocation modellayerlocation = new ModelLayerLocation(HexereiUtil.getResource("chest_boat/" + p_234570_.getName()), "main");
        return new ChestBoatModel(p_234569_.bakeLayer(modellayerlocation));
    }

    @Override
    public void render(ModChestBoatEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        pMatrixStack.pushPose();
        pMatrixStack.translate(0.0D, 0.375D, 0.0D);
        pMatrixStack.mulPose(Axis.YP.rotationDegrees(180.0F - pEntityYaw));
        float f = (float)pEntity.getHurtTime() - pPartialTicks;
        float f1 = pEntity.getDamage() - pPartialTicks;
        if (f1 < 0.0F) {
            f1 = 0.0F;
        }

        if (f > 0.0F) {
            pMatrixStack.mulPose(Axis.XP.rotationDegrees(Mth.sin(f) * f * f1 / 10.0F * (float)pEntity.getHurtDir()));
        }

        float f2 = pEntity.getBubbleAngle(pPartialTicks);
        if (!Mth.equal(f2, 0.0F)) {
            pMatrixStack.mulPose((new Quaternionf()).setAngleAxis(pEntity.getBubbleAngle(pPartialTicks) * ((float)Math.PI / 180F), 1.0F, 0.0F, 1.0F));
        }

        Pair<ResourceLocation, ChestBoatModel> pair = getModelWithLocation(pEntity);
        ResourceLocation resourcelocation = pair.getFirst();
        ChestBoatModel boatmodel = pair.getSecond();
        pMatrixStack.scale(-1.0F, -1.0F, 1.0F);
        pMatrixStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        boatmodel.setupAnim(pEntity, pPartialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
        VertexConsumer vertexconsumer = pBuffer.getBuffer(boatmodel.renderType(resourcelocation));
        boatmodel.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, HexereiUtil.getColorValueAlpha(1.0F, 1.0F, 1.0F, 1.0F));
        if (!pEntity.isUnderWater()) {
            VertexConsumer vertexconsumer1 = pBuffer.getBuffer(RenderType.waterMask());
            boatmodel.waterPatch().render(pMatrixStack, vertexconsumer1, pPackedLight, OverlayTexture.NO_OVERLAY);
        }

        pMatrixStack.popPose();
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(ModChestBoatEntity entity) {

        return HexereiUtil.getResource(getTextureLocation(entity.getModBoatType()));
    }



    private static String getTextureLocation(ModChestBoatEntity.Type p_234566_) {
        return "textures/entity/chest_boat/" + p_234566_.getName() + ".png";
    }

    public Pair<ResourceLocation, ChestBoatModel> getModelWithLocation(ModChestBoatEntity boat) { return this.boatResources.get(boat.getModBoatType()); }
}
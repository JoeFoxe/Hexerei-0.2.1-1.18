package net.joefoxe.hexerei.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;


public class BroomLargeSatchelModel extends ListModel<BroomEntity> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(HexereiUtil.getResource("broom_large_satchel"), "main");
    private final ModelPart satchel;


    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Satchel = partdefinition.addOrReplaceChild("Satchel", CubeListBuilder.create().texOffs(0, 14).addBox(5.5F, -3.5F, -1.5F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 14).addBox(-1.5F, -3.5F, -1.5F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 7).addBox(3.0F, -1.5F, 1.0F, 7.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(14, 18).addBox(3.0F, -1.75F, 1.25F, 7.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(3.0F, -1.5F, -3.0F, 7.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(14, 25).addBox(3.0F, -1.75F, -3.25F, 7.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(47, 27).addBox(-3.0F, -1.75F, -3.25F, 5.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(33, 2).addBox(-3.0F, -1.5F, -3.0F, 5.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(50, 19).addBox(-3.0F, -1.75F, 1.25F, 5.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(33, 7).addBox(-3.0F, -1.5F, 1.0F, 5.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    public BroomLargeSatchelModel(ModelPart root) {
        this.satchel = root.getChild("Satchel");
    }



    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        satchel.render(poseStack, buffer, packedLight, packedOverlay, color);
    }

    @Override
    public Iterable<ModelPart> parts() {
        return ImmutableList.of(satchel);
    }

    @Override
    public void setupAnim(BroomEntity p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_, float p_102623_) {

    }
}
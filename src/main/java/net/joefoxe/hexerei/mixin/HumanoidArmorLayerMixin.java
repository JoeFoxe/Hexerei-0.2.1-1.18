package net.joefoxe.hexerei.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.joefoxe.hexerei.item.custom.WitchArmorItem;
import net.joefoxe.hexerei.util.HexereiSupporterBenefits;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
@Mixin(value = HumanoidArmorLayer.class, priority = 999)
public abstract class HumanoidArmorLayerMixin<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {

    @Shadow
    private TextureAtlas armorTrimAtlas;

    @OnlyIn(Dist.CLIENT)
    public HumanoidArmorLayerMixin(RenderLayerParent<T, M> p_117346_) {
        super(p_117346_);
    }

    @OnlyIn(Dist.CLIENT)
    @Inject(at = @At("HEAD"), method = "renderArmorPiece", cancellable = true)
    public void renderArmorPiecerenderArmorPiece(PoseStack poseStack, MultiBufferSource bufferSource, T livingEntity, EquipmentSlot slot, int packedLight, A p_model, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
//    public void renderArmorPiece(PoseStack p_117119_, MultiBufferSource p_117120_, T livingEntity, EquipmentSlot slot, int packedLight, A p_model, CallbackInfo ci) {

//        ItemStack itemstack = livingEntity.getItemBySlot(slot);
//        if (itemstack.getItem() instanceof ArmorItem armoritem) {
//            if (armoritem.getType().getSlot() == slot) {
//                this.getParentModel().copyPropertiesTo(p_model);
//                this.setPartVisibility(p_model, slot);
//                net.minecraft.client.model.Model model = getArmorModelHook(livingEntity, itemstack, slot, p_model);
//                boolean flag1 = itemstack.hasFoil();
//                if(armoritem instanceof WitchArmorItem witchArmorItem){
//                    int i = (witchArmorItem).getColor(itemstack);
//                    float f = (float) (i >> 16 & 255) / 255.0F;
//                    float f1 = (float) (i >> 8 & 255) / 255.0F;
//                    float f2 = (float) (i & 255) / 255.0F;
//                    if (livingEntity instanceof Player player && HexereiSupporterBenefits.matchesSupporterUUID(player.getUUID())) // - 80 + ((int)(Math.sin(livingEntity.level.nextSubTickCount() / 100f) * (80)))
//                    {
//                        this.renderModel(p_117119_, p_117120_, 15728880, flag1, model, f, f1, f2, this.getArmorResource(livingEntity, itemstack, slot, null));
//                        this.renderModel(p_117119_, p_117120_, packedLight, flag1, model, 1.0F, 1.0F, 1.0F, this.getArmorResource(livingEntity, itemstack, slot, "overlay"));
//                        ci.cancel();
//                    }
//                    if (livingEntity instanceof Player player && !HexereiSupporterBenefits.matchesSupporterUUID(player.getUUID())) // - 80 + ((int)(Math.sin(livingEntity.level.nextSubTickCount() / 100f) * (80)))
//                    {
//                        this.renderModel(p_117119_, p_117120_, packedLight, flag1, model, f, f1, f2, this.getArmorResource(livingEntity, itemstack, slot, null));
//                        this.renderModel(p_117119_, p_117120_, packedLight, flag1, model, 1.0F, 1.0F, 1.0F, this.getArmorResource(livingEntity, itemstack, slot, "overlay"));
//                        ci.cancel();
//                    }
//                    if (!(livingEntity instanceof Player)) // - 80 + ((int)(Math.sin(livingEntity.level.nextSubTickCount() / 100f) * (80)))
//                    {
//                        this.renderModel(p_117119_, p_117120_, packedLight, flag1, model, f, f1, f2, this.getArmorResource(livingEntity, itemstack, slot, null));
//                        this.renderModel(p_117119_, p_117120_, packedLight, flag1, model, 1.0F, 1.0F, 1.0F, this.getArmorResource(livingEntity, itemstack, slot, "overlay"));
//                        ci.cancel();
//                    }
//                }
//            }
//        }



        ItemStack itemstack = livingEntity.getItemBySlot(slot);
        if (itemstack.getItem() instanceof ArmorItem armoritem) {
            if (armoritem.getEquipmentSlot() == slot) {
                this.getParentModel().copyPropertiesTo(p_model);
                this.setPartVisibility(p_model, slot);
                net.minecraft.client.model.Model model = getArmorModelHook(livingEntity, itemstack, slot, p_model);
                boolean flag = this.usesInnerModel(slot);
                ArmorMaterial armormaterial = armoritem.getMaterial().value();

                IClientItemExtensions extensions = IClientItemExtensions.of(itemstack);
                extensions.setupModelAnimations(livingEntity, itemstack, slot, model, limbSwing, limbSwingAmount, partialTick, ageInTicks, netHeadYaw, headPitch);
                int fallbackColor = extensions.getDefaultDyeColor(itemstack);
                for (int layerIdx = 0; layerIdx < armormaterial.layers().size(); layerIdx++) {
                    ArmorMaterial.Layer armormaterial$layer = armormaterial.layers().get(layerIdx);
                    int j = extensions.getArmorLayerTintColor(itemstack, livingEntity, armormaterial$layer, layerIdx, fallbackColor);
                    float[] colors = HexereiUtil.rgbIntToFloatArray(j);
                    if (j != 0) {
                        var texture = ClientHooks.getArmorTexture(livingEntity, itemstack, armormaterial$layer, flag, slot);
                        this.renderModel(poseStack, bufferSource, packedLight, false, model, colors[0], colors[1], colors[2], texture);
                    }
                }

                ArmorTrim armortrim = itemstack.get(DataComponents.TRIM);
                if (armortrim != null) {
                    this.renderTrim(armoritem.getMaterial(), poseStack, bufferSource, packedLight, armortrim, model, flag);
                }

                if (itemstack.hasFoil()) {
                    this.renderGlint(poseStack, bufferSource, packedLight, model);
                }
            }
        }

    }



    private void renderArmorPiece(PoseStack poseStack, MultiBufferSource bufferSource, T livingEntity, EquipmentSlot slot, int packedLight, A p_model, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    private void renderTrim(
            Holder<ArmorMaterial> p_323506_, PoseStack p_289687_, MultiBufferSource p_289643_, int p_289683_, ArmorTrim p_289692_, net.minecraft.client.model.Model p_289663_, boolean p_289651_
    ) {
        TextureAtlasSprite textureatlassprite = this.armorTrimAtlas
                .getSprite(p_289651_ ? p_289692_.innerTexture(p_323506_) : p_289692_.outerTexture(p_323506_));
        VertexConsumer vertexconsumer = textureatlassprite.wrap(p_289643_.getBuffer(Sheets.armorTrimsSheet(p_289692_.pattern().value().decal())));
        p_289663_.renderToBuffer(p_289687_, vertexconsumer, p_289683_, OverlayTexture.NO_OVERLAY);
    }

    private void renderGlint(PoseStack p_289673_, MultiBufferSource p_289654_, int p_289649_, net.minecraft.client.model.Model p_289659_) {
        p_289659_.renderToBuffer(p_289673_, p_289654_.getBuffer(RenderType.armorEntityGlint()), p_289649_, OverlayTexture.NO_OVERLAY);
    }


    private boolean usesInnerModel(EquipmentSlot p_117129_) {
        return p_117129_ == EquipmentSlot.LEGS;
    }

    protected net.minecraft.client.model.Model getArmorModelHook(T entity, ItemStack itemStack, EquipmentSlot slot, A model) {
        return ClientHooks.getArmorModel(entity, itemStack, slot, model);
    }

    private void renderModel(PoseStack p_117107_, MultiBufferSource p_117108_, int p_117109_, boolean p_117111_, net.minecraft.client.model.Model p_117112_, float p_117114_, float p_117115_, float p_117116_, ResourceLocation armorResource) {
        VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(p_117108_, RenderType.armorCutoutNoCull(armorResource), p_117111_);
        p_117112_.renderToBuffer(p_117107_, vertexconsumer, p_117109_, OverlayTexture.NO_OVERLAY, HexereiUtil.getColorValueAlpha(p_117114_, p_117115_, p_117116_, 1.0F));
    }

//    public ResourceLocation getArmorResource(net.minecraft.world.entity.Entity entity, ItemStack stack, EquipmentSlot slot, @Nullable String type) {
//        ArmorItem item = (ArmorItem)stack.getItem();
//        String texture = item.getMaterial().value().layers().getName();
//        String domain = "minecraft";
//        int idx = texture.indexOf(':');
//        if (idx != -1) {
//            domain = texture.substring(0, idx);
//            texture = texture.substring(idx + 1);
//        }
//        String s1 = String.format(java.util.Locale.ROOT, "%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, (usesInnerModel(slot) ? 2 : 1), type == null ? "" : String.format(java.util.Locale.ROOT, "_%s", type));
//
//        s1 = ClientHooks.getArmorTexture(entity, stack, s1, slot, type);
//        ResourceLocation resourcelocation = HumanoidArmorLayer.ARMOR_LOCATION_CACHE.get(s1);
//
//        if (resourcelocation == null) {
//            resourcelocation = new ResourceLocation(s1);
//            HumanoidArmorLayer.ARMOR_LOCATION_CACHE.put(s1, resourcelocation);
//        }
//
//        return resourcelocation;
//    }

    protected void setPartVisibility(A p_117126_, EquipmentSlot p_117127_) {
        p_117126_.setAllVisible(false);
        switch (p_117127_) {
            case HEAD -> {
                p_117126_.head.visible = true;
                p_117126_.hat.visible = true;
            }
            case CHEST -> {
                p_117126_.body.visible = true;
                p_117126_.rightArm.visible = true;
                p_117126_.leftArm.visible = true;
            }
            case LEGS -> {
                p_117126_.body.visible = true;
                p_117126_.rightLeg.visible = true;
                p_117126_.leftLeg.visible = true;
            }
            case FEET -> {
                p_117126_.rightLeg.visible = true;
                p_117126_.leftLeg.visible = true;
            }
        }

    }

}
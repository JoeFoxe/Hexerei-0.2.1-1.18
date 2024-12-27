package net.joefoxe.hexerei.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.joefoxe.hexerei.tileentity.MixingCauldronTile;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Random;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class CauldronParticle extends TextureSheetParticle {

    private final ResourceLocation TEXTURE = HexereiUtil.getResource(
            "textures/particle/cauldron_boil_particle.png");

    // thanks to understanding simibubi's code from the Create mod for rendering particles I was able to render my own :D
    public static final Vec3[] CUBE = {
            // top render
            new Vec3(0.5, 0.1, -0.5),
            new Vec3(0.5, 0.1, 0.5),
            new Vec3(-0.5, 0.1, 0.5),
            new Vec3(-0.5, 0.1, -0.5),

            // bottom render
            new Vec3(-0.5, -0.1, -0.5),
            new Vec3(-0.5, -0.1, 0.5),
            new Vec3(0.5, -0.1, 0.5),
            new Vec3(0.5, -0.1, -0.5),

            // front render
            new Vec3(-0.5, -0.1, 0.5),
            new Vec3(-0.5, 0.1, 0.5),
            new Vec3(0.5, 0.1, 0.5),
            new Vec3(0.5, -0.1, 0.5),

            // back render
            new Vec3(0.5, -0.1, -0.5),
            new Vec3(0.5, 0.1, -0.5),
            new Vec3(-0.5, 0.1, -0.5),
            new Vec3(-0.5, -0.1, -0.5),

            // left render
            new Vec3(-0.5, -0.1, -0.5),
            new Vec3(-0.5, 0.1, -0.5),
            new Vec3(-0.5, 0.1, 0.5),
            new Vec3(-0.5, -0.1, 0.5),

            // right render
            new Vec3(0.5, -0.1, 0.5),
            new Vec3(0.5, 0.1, 0.5),
            new Vec3(0.5, 0.1, -0.5),
            new Vec3(0.5, -0.1, -0.5)
    };

    public static final Vec3[] CUBE_NORMALS = {
            // modified normals for the sides
            new Vec3(0, 0.1, 0),
            new Vec3(0, -0.5, 0),
            new Vec3(0, 0, 0.5),
            new Vec3(0, 0, 0.5),
            new Vec3(0, 0, 0.5),
            new Vec3(0, 0, 0.5),
    };

    public final static ResourceLocation TEXTURE_BLANK =
            HexereiUtil.getResource("textures/block/blank.png");
    private static final ParticleRenderType renderType = new ParticleRenderType() {
        @Override
        public @Nullable BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
            RenderSystem.setShaderTexture(0, TEXTURE_BLANK);

            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);

            return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

//        @Override
//        public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
//            RenderSystem.setShaderTexture(0, TEXTURE_BLANK);
//
//            RenderSystem.depthMask(false);
//            RenderSystem.enableBlend();
//            RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
//
//            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
//        }
//
//        @Override
//        public void end(Tesselator tesselator) {
//            tesselator.end();
//            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
//                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
//        }
    };

    protected float scale;
    protected float rotationDirection;
    protected float rotation;
    private IClientFluidTypeExtensions clientFluid;

    public CauldronParticle(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z);
        this.xd = motionX;
        this.yd = motionY;
        this.zd = motionZ;
        this.rotation = 0;
        Random random = new Random();
        setScale(0.2F);
        setRotationDirection(random.nextFloat() - 0.5f);
    }

    public void setScale(float scale) {
        this.scale = scale;
        this.setSize(scale * 0.5f, scale * 0.5f);
    }

    public void setRotationDirection(float rotationDirection) {
        this.rotationDirection = rotationDirection;
    }

    @Override
    public void tick() {
        this.rotation = (this.rotationDirection * 0.1f) + this.rotation;
        super.tick();
    }

    @Override
    public void render(VertexConsumer builder, Camera renderInfo, float p_225606_3_) {
        Vec3 projectedView = renderInfo.getPosition();
        float lerpX = (float) (Mth.lerp(p_225606_3_, this.xo, this.x) - projectedView.x());
        float lerpY = (float) (Mth.lerp(p_225606_3_, this.yo, this.y) - projectedView.y());
        float lerpZ = (float) (Mth.lerp(p_225606_3_, this.zo, this.z) - projectedView.z());

        int light = 15728880;
        double ageMultiplier = 1 - Math.pow(Mth.clamp(age + p_225606_3_, 0, lifetime), 3) / Math.pow(lifetime, 3);

        RenderSystem._setShaderTexture(0, TEXTURE);

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 4; j++) {
                Vec3 vec = CUBE[i * 4 + j];
                vec = vec
                        .yRot(this.rotation)
                        .scale(scale * ageMultiplier)
                        .add(lerpX, lerpY, lerpZ);

                Vec3 normal = CUBE_NORMALS[i];

                if(i == 0) {
                    builder.addVertex((float)vec.x, (float)vec.y, (float)vec.z)
                            .setUv(0, 0)
                            .setColor(Mth.clamp(rCol * 1.25f, 0, 1.0f), Mth.clamp(gCol * 1.25f, 0, 1.0f), Mth.clamp(bCol * 1.25f, 0, 1.0f), alpha)
                            .setNormal((float) normal.x, (float) normal.y, (float) normal.z)
                            .setLight(light);
                }else if(i == 1) {
                    builder.addVertex((float)vec.x, (float)vec.y, (float)vec.z)
                            .setUv(0, 0)
                            .setColor(rCol * 0.55f, gCol * 0.55f, bCol * 0.55f, alpha)
                            .setNormal((float) normal.x, (float) normal.y, (float) normal.z)
                            .setLight(light);
                }else if(i == 2) {
                    builder.addVertex((float)vec.x, (float)vec.y, (float)vec.z)
                            .setUv(0, 0)
                            .setColor(rCol * 0.95f, gCol * 0.95f, bCol * 0.95f, alpha)
                            .setNormal((float) normal.x, (float) normal.y, (float) normal.z)
                            .setLight(light);
                }else if(i == 3) {
                    builder.addVertex((float)vec.x, (float)vec.y, (float)vec.z)
                            .setUv(0, 0)
                            .setColor(rCol * 0.75f, gCol * 0.75f, bCol * 0.75f, alpha)
                            .setNormal((float) normal.x, (float) normal.y, (float) normal.z)
                            .setLight(light);
                }else if(i == 4) {
                    builder.addVertex((float)vec.x, (float)vec.y, (float)vec.z)
                            .setUv(0, 0)
                            .setColor(rCol * 0.9f, gCol * 0.9f, bCol * 0.9f, alpha)
                            .setNormal((float) normal.x, (float) normal.y, (float) normal.z)
                            .setLight(light);
                }else {
                    builder.addVertex((float)vec.x, (float)vec.y, (float)vec.z)
                            .setUv(0, 0)
                            .setColor(rCol * 0.85f, gCol * 0.85f, bCol * 0.85f, alpha)
                            .setNormal((float) normal.x, (float) normal.y, (float) normal.z)
                            .setLight(light);
                }
            }
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return renderType;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<CauldronParticleData> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet sprite) {
            this.spriteSet = sprite;
        }

        @Nullable
        @Override
        public Particle createParticle(CauldronParticleData data, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            CauldronParticle cauldronParticle = new CauldronParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            Random random = new Random();

//            this.spriteSet = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(IClientFluidTypeExtensions.of(fluidStack.getFluid()).getStillTexture(fluidStack));
            MixingCauldronTile mixingCauldronTile = null;
            FluidStack fluidStack = data.fluid;

            Color color = new Color(BiomeColors.getAverageWaterColor(worldIn, new BlockPos((int) x, (int) (y), (int) z)));

            BlockState blockStateAtPos = worldIn.getBlockState(new BlockPos((int) x, (int) (y - 0.1), (int) z));

            cauldronParticle.clientFluid = IClientFluidTypeExtensions.of(fluidStack.getFluid());

            Function<ResourceLocation, TextureAtlasSprite> textureAtlasSpriteFunction = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);

            ResourceLocation stillLoc = cauldronParticle.clientFluid.getStillTexture(fluidStack);

            TextureAtlasSprite sprite = textureAtlasSpriteFunction.apply(stillLoc);

            int colorInt = IClientFluidTypeExtensions.of(fluidStack.getFluid()).getTintColor(fluidStack);
            float alpha = (colorInt >> 24 & 255) / 275f;
            float red = (colorInt >> 16 & 255) / 275f;
            float green = (colorInt >> 8 & 255) / 275f;
            float blue = (colorInt & 255) / 275f;
            colorInt = sprite.getPixelRGBA(0, random.nextInt(sprite.contents().width()), random.nextInt(sprite.contents().height()));
            float alpha2 = (colorInt >> 24 & 255) / 275f;
            float blue2 = (colorInt >> 16 & 255) / 275f;
            float green2 = (colorInt >> 8 & 255) / 275f;
            float red2 = (colorInt & 255) / 275f;

            float colorOffset = (random.nextFloat() * 0.15f);
            if (red > 0.75f && blue > 0.75f && green > 0.75f)
                cauldronParticle.setColor(Mth.clamp(red2 + colorOffset, 0, 1), Mth.clamp(green2 + colorOffset, 0, 1), Mth.clamp(blue2 + colorOffset, 0, 1));
            else
                cauldronParticle.setColor(Mth.clamp(red + colorOffset, 0, 1), Mth.clamp(green + colorOffset, 0, 1), Mth.clamp(blue + colorOffset, 0, 1));


            if (fluidStack.is(Fluids.WATER))
                cauldronParticle.setColor(color.getRed() / 450f + colorOffset, color.getGreen() / 450f + colorOffset, color.getBlue() / 450f + colorOffset);

            cauldronParticle.setAlpha(1.0f);

            cauldronParticle.pickSprite(this.spriteSet);
            return cauldronParticle;
        }
    }


}

package net.joefoxe.hexerei.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class FlowingLeavesParticle extends TextureSheetParticle {
    private static final float ACCELERATION_SCALE = 0.0025F;
    private static final int INITIAL_LIFETIME = 300;
    private static final int CURVE_ENDPOINT_TIME = 300;
    private static final float FALL_ACC = 0.25F;
    private static final float WIND_BIG = 2.0F;
    private float rotSpeed;
    private final float particleRandom;
    private final float spinAcceleration;

    protected FlowingLeavesParticle(ClientLevel pLevel, double pX, double pY, double pZ, SpriteSet pSpriteSet) {
        super(pLevel, pX, pY, pZ);
        this.setSprite(pSpriteSet.get(this.random.nextInt(12), 12));
        this.rotSpeed = (float)Math.toRadians(this.random.nextBoolean() ? -30.0D : 30.0D);
        this.particleRandom = this.random.nextFloat();
        this.spinAcceleration = (float)Math.toRadians(this.random.nextBoolean() ? -5.0D : 5.0D);
        this.lifetime = 300;
        this.gravity = 7.5E-4F;
        float f = this.random.nextBoolean() ? 0.05F : 0.075F;
        this.quadSize = f;
        this.setSize(f, f);
        this.friction = 1.0F;
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.lifetime-- <= 0) {
            this.remove();
        }

        if (!this.removed) {
            float f = (float)(300 - this.lifetime);
            float f1 = Math.min(f / 300.0F, 1.0F);
            double d0 = Math.cos(Math.toRadians((double)(this.particleRandom * 60.0F))) * 2.0D * Math.pow((double)f1, 1.25D);
            double d1 = Math.sin(Math.toRadians((double)(this.particleRandom * 60.0F))) * 2.0D * Math.pow((double)f1, 1.25D);
            this.xd += d0 * (double)0.0025F;
            this.zd += d1 * (double)0.0025F;
            this.yd -= (double)this.gravity;
            this.rotSpeed += this.spinAcceleration / 20.0F;
            this.oRoll = this.roll;
            this.roll += this.rotSpeed / 20.0F;
            this.move(this.xd, this.yd, this.zd);
            if (this.onGround || this.lifetime < 299 && (this.xd == 0.0D || this.zd == 0.0D)) {
                this.remove();
            }

            if (!this.removed) {
                this.xd *= (double)this.friction;
                this.yd *= (double)this.friction;
                this.zd *= (double)this.friction;
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet sprite) {
            this.spriteSet = sprite;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            Random rand = new Random();
            float colorOffset = (rand.nextFloat() * 0.4f);
            FlowingLeavesParticle flowingLeaves = new FlowingLeavesParticle(worldIn, x, y, z, this.spriteSet);
            flowingLeaves.pickSprite(this.spriteSet);

            flowingLeaves.setColor(1f - colorOffset,1f - colorOffset,1f - colorOffset);

            return flowingLeaves;
        }
    }
}
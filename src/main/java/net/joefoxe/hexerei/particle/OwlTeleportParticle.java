package net.joefoxe.hexerei.particle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class OwlTeleportParticle extends TextureSheetParticle {
    protected float scale;
    protected float rotationDir;
    protected float fallingSpeed;
    protected double xdStart;
    protected double ydStart;
    protected double zdStart;
    protected double ydExtra;

    public OwlTeleportParticle(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z);
        this.xd = motionX;
        this.yd = motionY;
        this.zd = motionZ;
        this.xdStart = motionX;
        this.ydStart = motionY;
        this.zdStart = motionZ;
        this.roll = new Random().nextFloat() * (float)Math.PI;
        this.oRoll = this.roll;
        this.ydExtra = new Random().nextFloat() * (motionY/10f);
        this.rotationDir = new Random().nextFloat() - 0.5f;
        this.fallingSpeed = new Random().nextFloat();
        this.lifetime = 20 + (int)(new Random().nextFloat() * 20f);
        this.quadSize = 0.125f + 0.125f * new Random().nextFloat();

        setScale(0.2F);
    }

    public void setScale(float scale) {
        this.scale = scale;
        this.setSize(scale * 0.5f, scale * 0.5f);
    }

    @Override
    public void tick() {

//        this.oRoll = this.roll;
//        if(Math.abs(this.yd) > 0 && this.y != this.yo)
//            this.roll += 0.3f * rotationDir;
        this.xd  = Math.min(1, (this.lifetime - this.age) / (float)this.lifetime) * xdStart;
        this.yd  = Math.min(1, (this.age) / (float)this.lifetime) * ydStart + this.ydExtra;
        this.zd  = Math.min(1, (this.lifetime - this.age) / (float)this.lifetime) * zdStart;
        this.alpha = Math.min(1, (this.lifetime - this.age) / (float)this.lifetime);



        this.oRoll = this.roll;
        if(Math.abs(this.yd) > 0 && this.y != this.yo)
            this.roll += 0.3f * rotationDir;
        this.yd -= 0.005f * fallingSpeed;

        super.tick();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
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
            OwlTeleportParticle extinguishParticle = new OwlTeleportParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            extinguishParticle.pickSprite(this.spriteSet);

            extinguishParticle.setColor(1f - colorOffset,1f - colorOffset,1f - colorOffset);

            return extinguishParticle;
        }
    }
}

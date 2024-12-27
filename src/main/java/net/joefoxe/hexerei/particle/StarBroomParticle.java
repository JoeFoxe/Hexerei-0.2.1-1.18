package net.joefoxe.hexerei.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StarBroomParticle extends SimpleAnimatedParticle {
    StarBroomParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ, pSprites, 0.0125F);
        this.xd = pXSpeed;
        this.yd = pYSpeed;
        this.zd = pZSpeed;
        this.quadSize *= 0.75F;
        this.lifetime = 20 + this.random.nextInt(12);
        this.setFadeColor(15916745);
        this.setSpriteFromAge(pSprites);
    }

    public void move(double pX, double pY, double pZ) {
        this.setBoundingBox(this.getBoundingBox().move(pX, pY, pZ));
        this.setLocationFromBoundingbox();
    }

    @Override
    public int getLightColor(float pPartialTick) {
        float time = level.getTimeOfDay(0);
        if(time > 0.25f && time < 0.75f && level.getMoonPhase() == 0 && !level.dimensionType().hasFixedTime())
            return LightTexture.FULL_BRIGHT;
        return super.getLightColor(pPartialTick);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new StarBroomParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprites);
        }
    }
}

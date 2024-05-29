package net.joefoxe.hexerei.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Locale;

/**
 * Created by TGG on 25/03/2020.
 * <p>
 * The particle has two pieces of information which are used to customise it:
 * <p>
 * 1) The colour (tint) which is used to change the hue of the particle
 * 2) The diameter of the particle
 * <p>
 * This class is used to
 * 1) store this information, and
 * 2) transmit it between server and client (write and read methods), and
 * 3) parse it from a command string i.e. the /particle params
 */
public class CauldronParticleData extends ParticleType<CauldronParticleData> implements ParticleOptions {

    ParticleType<CauldronParticleData> type;
    FluidStack fluid;

    @SuppressWarnings("unchecked")
    public CauldronParticleData(ParticleType<?> type, FluidStack fluid){
        super(true, DESERIALIZER);
        this.type = (ParticleType<CauldronParticleData>)type;
        this.fluid = fluid;

    }
    @Nonnull
    @Override
    public ParticleType<CauldronParticleData> getType() {
        return type;
    }

    // write the particle information to a FriendlyByteBuf, ready for transmission to a client
    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeFluidStack(fluid);
    }

    // used for debugging I think; prints the data in human-readable format

    @Override
    public String writeToString() {
        return HexereiUtil.getKeyOrThrow(type) + " " + HexereiUtil.getKeyOrThrow(fluid.getFluid());
    }

    // --------- these remaining methods are used to serialize the Particle Data.
    //  I'm not yet sure what the Codec is used for, given that the DESERIALIZER already deserializes using read.
    //  Perhaps it will be used to replace the manual read methods in the future.

    //  The CODEC is a convenience to make it much easier to serialise and deserialize your objects.
    //  Using the builder below, you construct a serializer and deserializer in one go, using lambda functions.
    //  eg for the FlameParticleData CODEC:
    //  a) In order to serialise it, it reads the 'tint' member variable (type: INT) and the 'diameter' member variable (type: DOUBLE)
    //  b) In order to deserialise it, call the matching constructor FlameParticleData(INT, DOUBLE)


    //public static final Codec<CauldronParticleData> CODEC = RecordCodecBuilder.create(
    //        instance -> instance.group(
    //                Codec.INT.fieldOf("tint").forGetter(d -> d.tint.getRGB()),
    //                Codec.DOUBLE.fieldOf("diameter").forGetter(d -> d.diameter)
    //        ).apply(instance, CauldronParticleData::new)
    //);



//    public static Codec<AltarParticleOptions> codec(ParticleType<AltarParticleOptions> particleType) {
//        return RecordCodecBuilder.create(c -> c.group(
//                Vector3f.CODEC.fieldOf("color").forGetter(data -> data.color)
//        ).apply(c, (color) -> new AltarParticleOptions(particleType, color)));
//    }

    public static Codec<CauldronParticleData> codec(ParticleType<CauldronParticleData> particleType) {
        return RecordCodecBuilder.create(c -> c
            .group(FluidStack.CODEC.fieldOf("fluid")
                .forGetter(p -> p.fluid))
            .apply(c, (color) -> new CauldronParticleData(particleType, color)));
    }

    // The DESERIALIZER is used to construct CauldronParticleData from either command line parameters or from a network packet

    public static final ParticleOptions.Deserializer<CauldronParticleData> DESERIALIZER =
            new ParticleOptions.Deserializer<>() {

                // TODO Fluid particles on command
                public CauldronParticleData fromCommand(ParticleType<CauldronParticleData> particleTypeIn, StringReader reader)
                        throws CommandSyntaxException {
                    return new CauldronParticleData(particleTypeIn, new FluidStack(Fluids.WATER, 1));
                }

                public CauldronParticleData fromNetwork(ParticleType<CauldronParticleData> particleTypeIn, FriendlyByteBuf buffer) {
                    return new CauldronParticleData(particleTypeIn, buffer.readFluidStack());
                }
            };

    @Override
    public Codec<CauldronParticleData> codec() {
        return codec(this);
    }
}

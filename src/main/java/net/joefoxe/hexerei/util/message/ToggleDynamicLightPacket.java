package net.joefoxe.hexerei.util.message;

import net.joefoxe.hexerei.light.LightManager;
import net.joefoxe.hexerei.util.AbstractPacket;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public class ToggleDynamicLightPacket extends AbstractPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, ToggleDynamicLightPacket> CODEC  = StreamCodec.ofMember(ToggleDynamicLightPacket::encode, ToggleDynamicLightPacket::new);
    public static final Type<ToggleDynamicLightPacket> TYPE = new Type<>(HexereiUtil.getResource("toggle_dynamic_light"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    boolean enabled;

    public ToggleDynamicLightPacket(boolean enabled) {
        this.enabled = enabled;
    }
    public ToggleDynamicLightPacket(RegistryFriendlyByteBuf buf) {
        this.enabled = buf.readBoolean();
    }

    public static void encode(ToggleDynamicLightPacket object, RegistryFriendlyByteBuf buffer) {
        buffer.writeBoolean(object.enabled);
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        LightManager.toggleLightsAndConfig(enabled);
    }
}
package com.caffeineaddict.caffeineaddictmode.registry;

import com.caffeineaddict.caffeineaddictmode.CaffeineAddictMode;
import com.caffeineaddict.caffeineaddictmode.blocks.CoffeeMachine.network.BrewRequestPacket;
import com.caffeineaddict.caffeineaddictmode.blocks.IceMaker.network.GiveWaterPacket;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;

public class ModNetwork {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(CaffeineAddictMode.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void registerPackets() {
        // CoffeeMachine packet
        CHANNEL.registerMessage(packetId++,
                BrewRequestPacket.class,
                BrewRequestPacket::encode,
                BrewRequestPacket::decode,
                BrewRequestPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );

        // IceMachine packet
        CHANNEL.registerMessage(packetId++,
                GiveWaterPacket.class,
                GiveWaterPacket::encode,
                GiveWaterPacket::decode,
                GiveWaterPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );
    }

    // helper for sending to server
    public static <MSG> void sendToServer(MSG message) {
        CHANNEL.sendToServer(message);
    }

    // helper for sending to client
    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}


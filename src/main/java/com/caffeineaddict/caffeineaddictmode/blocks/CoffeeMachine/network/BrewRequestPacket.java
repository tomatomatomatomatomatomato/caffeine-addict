package com.caffeineaddict.caffeineaddictmode.blocks.CoffeeMachine.network;

import com.caffeineaddict.caffeineaddictmode.blocks.CoffeeMachine.CoffeeMachineBlockEntity;
import java.nio.charset.StandardCharsets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BrewRequestPacket {
    int idx;

    public BrewRequestPacket(int idx) {this.idx = idx;}

    public static void encode(BrewRequestPacket pkt, FriendlyByteBuf buf) {
        buf.writeInt(pkt.idx);
    }
    public static BrewRequestPacket decode(FriendlyByteBuf buf) {
        return new BrewRequestPacket(buf.readInt());
    }

    public static void handle(BrewRequestPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            String log = "[DEBUGG] Received brew request for slot " + pkt.idx;
            System.out.writeBytes(log.getBytes(StandardCharsets.UTF_8));
            if (player != null && player.containerMenu instanceof com.caffeineaddict.caffeineaddictmode.blocks.CoffeeMachine.CoffeeMachineMenu menu) {
                var be = player.level.getBlockEntity(menu.getPos());
                if (be instanceof CoffeeMachineBlockEntity machine) {
                    System.out.writeBytes("[DEBUGG] brewing...".getBytes(StandardCharsets.UTF_8));
                    machine.brew(pkt.idx);
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }

    public int getSlotIndex() {
        return idx;
    }
}

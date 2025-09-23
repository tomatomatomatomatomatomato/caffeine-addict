package com.caffeineaddict.caffeineaddictmode.blocks.IceMaker.network;

import com.caffeineaddict.caffeineaddictmode.registry.ModItems;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class GiveWaterPacket {
    private final BlockPos pos;
    private final boolean isHot;

    public GiveWaterPacket(BlockPos pos, boolean isHot) {
        this.pos = pos;
        this.isHot = isHot;
    }

    public static void encode(GiveWaterPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeBoolean(msg.isHot);
    }

    public static GiveWaterPacket decode(FriendlyByteBuf buf) {
        return new GiveWaterPacket(buf.readBlockPos(), buf.readBoolean());
    }

    public static void handle(GiveWaterPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                ItemStack water = new ItemStack(msg.isHot ? ModItems.HOT_WATER.get() : ModItems.ICE_WATER.get());
                player.addItem(water);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

package com.caffeineaddict.caffeineaddictmode;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = CaffeineAddictMode.MOD_ID)
public class BreakEffectsHandler {

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent e) {
        Level level = (Level) e.getLevel();
        if (level.isClientSide()) return; // 파티클/사운드 브로드캐스트는 서버에서

        BlockPos pos = e.getPos();
        BlockState state = e.getState();

        // 이 블럭이 "우리 모드 네임스페이스"인지 확인
        ResourceLocation key = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        if (key != null && CaffeineAddictMode.MOD_ID.equals(key.getNamespace())) {
            // 바닐라 기본 파괴 이펙트는 그대로 두고, 철블럭 파괴 이펙트 '추가'로 한 번 더 쏘기
            ((ServerLevel) level).levelEvent(
                    /* 2001 = PARTICLES_DESTROY_BLOCK */ 2001,
                    pos,
                    Block.getId(Blocks.IRON_BLOCK.defaultBlockState())
            );
        }
    }
}
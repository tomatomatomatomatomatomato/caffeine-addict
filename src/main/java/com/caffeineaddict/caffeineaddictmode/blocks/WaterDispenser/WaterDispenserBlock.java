package com.caffeineaddict.caffeineaddictmode.blocks.WaterDispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class WaterDispenserBlock extends Block implements EntityBlock {

    public WaterDispenserBlock(Properties props) {
        super(props);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WaterDispenserBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {

        if (!level.isClientSide) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof WaterDispenserBlockEntity dispenser) {

                // 1. 물 양동이 들고 있으면 무조건 채우기
                if (player.getItemInHand(hand).is(Items.WATER_BUCKET)) {
                    dispenser.fill();
                    player.setItemInHand(hand, new ItemStack(Items.BUCKET));
                    return InteractionResult.SUCCESS;
                }

                // 2. 클릭 위치 판별
                Vec3 hitLoc = hit.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
                System.out.println("hitLoc : " + hitLoc);
                double x = hitLoc.x;
                double y = hitLoc.y;

                // NORTH 고정 기준: 좌 → x < 0.5, 우 → x > 0.5
                Direction facing = Direction.NORTH;

                if (hit.getDirection() == facing) {
                    if (y > 0.6 && y <= 1.0) {
                        if (x >= 0.25 && x <= 0.48) {
                            dispenser.giveCoolWater(player); // 왼쪽 → 찬물
                        } else if (x >= 0.55 && x <= 0.80) {
                            dispenser.giveHotWater(player); // 오른쪽 → 뜨물
                        }
                    }
                }
            }
        }

        return InteractionResult.SUCCESS;
    }
}

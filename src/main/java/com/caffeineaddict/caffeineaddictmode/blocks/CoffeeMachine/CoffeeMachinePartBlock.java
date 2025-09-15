package com.caffeineaddict.caffeineaddictmode.blocks.CoffeeMachine;

import com.caffeineaddict.caffeineaddictmode.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;

public class CoffeeMachinePartBlock extends Block {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public CoffeeMachinePartBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    // 파트 부서지면 본체도 제거
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            if (!level.isClientSide) {
                Direction facing = state.getValue(FACING);
                BlockPos mainPos = pos.relative(facing.getOpposite());
                if (level.getBlockState(mainPos).is(ModBlocks.COFFEE_MACHINE.get())) {
                    level.destroyBlock(mainPos, false);
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    // 파트 우클릭 ⇒ 본체로 전달
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        Direction facing = state.getValue(FACING);
        BlockPos mainPos = pos.relative(facing.getOpposite());
        BlockState main = level.getBlockState(mainPos);
        if (main.is(ModBlocks.COFFEE_MACHINE.get())) {
            return main.getBlock().use(main, level, mainPos, player, hand, hit);
        }
        return InteractionResult.PASS;
    }
}
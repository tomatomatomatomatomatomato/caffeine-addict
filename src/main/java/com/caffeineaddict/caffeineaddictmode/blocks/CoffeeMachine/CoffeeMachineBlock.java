package com.caffeineaddict.caffeineaddictmode.blocks.CoffeeMachine;

import com.caffeineaddict.caffeineaddictmode.CoffeeMachineBlockEntities;
//import javax.annotation.Nullable;

import com.caffeineaddict.caffeineaddictmode.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class CoffeeMachineBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

//    public CoffeeMachineBlock(Properties properties) {
//        super(properties);
//    }
    public CoffeeMachineBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    // 배치 방향
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    
    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (!world.isClientSide) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof CoffeeMachineBlockEntity coffeeMachine) {
                coffeeMachine.setLastUsedBy(player.getName().getString());
                NetworkHooks.openScreen((ServerPlayer) player, coffeeMachine, pos);
            }
        }
        return InteractionResult.SUCCESS;
    }


    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state,
                            @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (level.isClientSide) return;

        Direction facing = state.getValue(FACING);
        BlockPos otherPos = pos.relative(facing);

        if (level.getBlockState(otherPos).is(ModBlocks.COFFEE_MACHINE_PART.get())) {
            return;
        }

        if (!level.getBlockState(otherPos).getMaterial().isReplaceable()) {
            level.destroyBlock(pos, true);
            return;
        }

        level.setBlock(otherPos,
                ModBlocks.COFFEE_MACHINE_PART.get()
                        .defaultBlockState()
                        .setValue(FACING, facing),
                Block.UPDATE_ALL);
    }
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            if (!level.isClientSide) {
                Direction facing = state.getValue(FACING);
                BlockPos otherPos = pos.relative(facing);
                if (level.getBlockState(otherPos).is(ModBlocks.COFFEE_MACHINE_PART.get())) {
                    level.destroyBlock(otherPos, false);
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }


    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CoffeeMachineBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == CoffeeMachineBlockEntities.COFFEE_MACHINE.get() ?
                (lvl, pos, st, be) -> ((CoffeeMachineBlockEntity) be).tick(lvl, pos, st)
                : null;
    }


}

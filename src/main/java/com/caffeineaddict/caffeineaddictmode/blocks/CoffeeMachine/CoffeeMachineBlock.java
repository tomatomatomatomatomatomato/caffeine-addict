package com.caffeineaddict.caffeineaddictmode.blocks.CoffeeMachine;

import com.caffeineaddict.caffeineaddictmode.registry.ModBlockEntities;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

public class CoffeeMachineBlock extends Block implements EntityBlock {
    public CoffeeMachineBlock(Properties properties) {
        super(properties);
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
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CoffeeMachineBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        // 16x16x32 (2블록 높이) 모양
        return Block.box(0.0D, 0.0D, 0.0D, 24.0D, 19.2D, 25.6D);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == ModBlockEntities.COFFEE_MACHINE.get() ?
                (lvl, pos, st, be) -> ((CoffeeMachineBlockEntity) be).tick(lvl, pos, st)
                : null;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) { // 블록이 실제로 바뀔 때만 처리
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof CoffeeMachineBlockEntity te) {
                Containers.dropContents(level, pos, te.getShotContainerForRemoval()); // 내부 샷 인벤토리 아이템 흩뿌리기
                Containers.dropContents(level, pos, te.getSteamContainerForRemoval()); // 내부 스팀 인벤토리 아이템 흩뿌리기
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }
}

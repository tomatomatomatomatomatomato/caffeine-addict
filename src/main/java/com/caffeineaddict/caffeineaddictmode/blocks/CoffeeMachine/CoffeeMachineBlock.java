package com.caffeineaddict.caffeineaddictmode.blocks.CoffeeMachine;

import com.caffeineaddict.caffeineaddictmode.CoffeeMachineBlockEntities;
import com.caffeineaddict.caffeineaddictmode.registry.ModBlocks;
import com.caffeineaddict.caffeineaddictmode.sound.ModSoundEvents;
//import javax.annotation.Nullable;

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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.Nullable;

public class CoffeeMachineBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

//    private static final int H = 12;
//    private static final VoxelShape MAIN_W = Block.box(0, 0, 0,   8, H, 16); // X:0~8
//    private static final VoxelShape MAIN_E = Block.box(8, 0, 0,  16, H, 16); // X:8~16
//    private static final VoxelShape MAIN_N = Block.box(0, 0, 0,  16, H,  8); // Z:0~8
//    private static final VoxelShape MAIN_S = Block.box(0, 0, 8,  16, H, 16); // Z:8~16

    private static final VoxelShape SHAPE_NS = Block.box(0, 0, 0, 16, 16, 16); // 북/남 방향 때
    private static final VoxelShape SHAPE_EW = Block.box(0, 0, 0, 16, 16, 16); // 동/서 방향 때


//    @Override
//    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
//        return switch (state.getValue(FACING)) {
//            case NORTH -> MAIN_W; // 오른쪽=EAST → 본체=WEST 절반
//            case EAST  -> MAIN_N; // 오른쪽=SOUTH → 본체=NORTH 절반
//            case SOUTH -> MAIN_E; // 오른쪽=WEST  → 본체=EAST 절반
//            case WEST  -> MAIN_S; // 오른쪽=NORTH → 본체=SOUTH 절반
//            default    -> MAIN_W;
//        };
//    }
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        Direction f = state.getValue(FACING);
        return (f == Direction.NORTH || f == Direction.SOUTH) ? SHAPE_NS : SHAPE_EW;
    }
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return getShape(state, level, pos, ctx);
    }
    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.empty();
    }


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
        BlockPos otherPos = pos.relative(facing.getClockWise()); // ★ 옆(오른쪽)

        if (!level.getBlockState(otherPos).getMaterial().isReplaceable()) {
            level.destroyBlock(pos, true);
            return;
        }

        level.setBlock(otherPos,
                ModBlocks.COFFEE_MACHINE_PART.get().defaultBlockState().setValue(FACING, facing),
                Block.UPDATE_ALL);
    }
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            if (!level.isClientSide) {
                Direction facing = state.getValue(FACING);
                BlockPos otherPos = pos.relative(facing.getClockWise());
                if (level.getBlockState(otherPos).is(ModBlocks.COFFEE_MACHINE_PART.get())) {
                    level.destroyBlock(otherPos, false);
                }

                if (level instanceof ServerLevel server) {
                    var stopCoffee = new ClientboundStopSoundPacket(
                            ModSoundEvents.COFFEE_MACHINE_SOUND.get().getLocation(), SoundSource.BLOCKS);

                    for (ServerPlayer p : server.players()) {
                        p.connection.send(stopCoffee);
                    }
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

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F; // 방향별 밝기 차이 제거
    }
}

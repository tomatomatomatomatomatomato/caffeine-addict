package com.caffeineaddict.caffeineaddictmode.block;

import com.caffeineaddict.caffeineaddictmode.block.entity.IceMakerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class IceMakerBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    // 모델이 2~14(1/16 단위) 정도를 쓰는 가정: 필요 시 숫자 미세조정
    private static final VoxelShape SHAPE_NORTH = Block.box(2, 0, 2, 14, 16, 14);
    private static final VoxelShape SHAPE_EAST  = rotateY(SHAPE_NORTH);
    private static final VoxelShape SHAPE_SOUTH = rotateY(SHAPE_EAST);
    private static final VoxelShape SHAPE_WEST  = rotateY(SHAPE_SOUTH);

    private static final VoxelShape COLL_N = Block.box(2.0, 0, 2.0, 14.0, 16, 14.0);   // 충돌(딱 맞게)
    private static final VoxelShape PICK_N  = Block.box(1.25, 0, 1.25, 14.75, 16, 14.75); // 선택/타겟팅(살짝 크게)
    private static final VoxelShape VIS_N   = COLL_N;  // 시각 판정(내부 보임 방지: 충돌과 같게)

    private static final VoxelShape COLL_E = rotateY(COLL_N);
    private static final VoxelShape COLL_S = rotateY(COLL_E);
    private static final VoxelShape COLL_W = rotateY(COLL_S);

    private static final VoxelShape PICK_E = rotateY(PICK_N);
    private static final VoxelShape PICK_S = rotateY(PICK_E);
    private static final VoxelShape PICK_W = rotateY(PICK_S);

    private static final VoxelShape VIS_E  = rotateY(VIS_N);
    private static final VoxelShape VIS_S  = rotateY(VIS_E);
    private static final VoxelShape VIS_W  = rotateY(VIS_S);

    public IceMakerBlock() {
        // 부분 블록이므로 noOcclusion 권장(광원/시야/렌더 합성 문제 방지)
        super(BlockBehaviour.Properties.of(Material.STONE).strength(2.0f).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    /* ---------- 배치/상태 ---------- */

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        // 플레이어가 바라보는 반대 방향으로 정면이 향하게
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
        b.add(FACING);
    }

    /* ---------- 렌더/히트박스/충돌 ---------- */

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override // 아웃라인(하이라이트 선) = 크게 → 클릭 잘 됨
//    public VoxelShape getShape(BlockState s, BlockGetter l, BlockPos p, CollisionContext c) {
//        return switch (s.getValue(FACING)) {
//            case EAST  -> PICK_E;
//            case SOUTH -> PICK_S;
//            case WEST  -> PICK_W;
//            default    -> PICK_N;
//        };
//    }
    public VoxelShape getShape(BlockState s, BlockGetter l, BlockPos p, CollisionContext c) {
        return Shapes.block(); // 선택/하이라이트도 풀큐브
    }

    @Override // 레이 트레이스(부수기/상호작용) = 크게 → 밑/뒤 타격 방지
    public VoxelShape getInteractionShape(BlockState s, BlockGetter l, BlockPos p) {
        return switch (s.getValue(FACING)) {
            case EAST  -> PICK_E;
            case SOUTH -> PICK_S;
            case WEST  -> PICK_W;
            default    -> PICK_N;
        };
    }

    @Override // 충돌 = 딱 맞게 → 캐릭터가 ‘안으로’ 끼어들어 내부가 보이는 문제 방지
//    public VoxelShape getCollisionShape(BlockState s, BlockGetter l, BlockPos p, CollisionContext c) {
//        return switch (s.getValue(FACING)) {
//            case EAST  -> COLL_E;
//            case SOUTH -> COLL_S;
//            case WEST  -> COLL_W;
//            default    -> COLL_N;
//        };
//    }
    public VoxelShape getCollisionShape(BlockState s, BlockGetter l, BlockPos p, CollisionContext c) {
        return Shapes.block(); // 0~16 풀큐브
    }

    @Override // 시각 판정 = 충돌과 동일 → 카메라가 블록 내부를 관통해 보이는 현상 감소
    public VoxelShape getVisualShape(BlockState s, BlockGetter l, BlockPos p, CollisionContext c) {
        return switch (s.getValue(FACING)) {
            case EAST  -> VIS_E;
            case SOUTH -> VIS_S;
            case WEST  -> VIS_W;
            default    -> VIS_N;
        };
    }

    private static VoxelShape rotateY(VoxelShape shape) {
        List<AABB> boxes = shape.toAabbs();
        VoxelShape out = Shapes.empty();
        for (AABB bb : boxes) {
            // x,z를 0~16 좌표계에서 시계방향 90도 회전
            AABB r = new AABB(
                    16 - bb.maxZ, bb.minY, bb.minX,
                    16 - bb.minZ, bb.maxY, bb.maxX
            );
            out = Shapes.or(out, Shapes.create(r));
        }
        return out;
    }

    /* ---------- 블록 엔티티/GUI ---------- */

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new IceMakerBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof IceMakerBlockEntity) {
                NetworkHooks.openScreen((ServerPlayer) player, (MenuProvider) be, pos);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : (lvl, p, st, be) -> {
            if (be instanceof IceMakerBlockEntity ice) {
                IceMakerBlockEntity.tick(lvl, p, st, ice);
            }
        };
    }
}
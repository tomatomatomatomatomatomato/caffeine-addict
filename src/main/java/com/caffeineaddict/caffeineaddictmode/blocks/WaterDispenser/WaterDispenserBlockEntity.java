package com.caffeineaddict.caffeineaddictmode.blocks.WaterDispenser;

import com.caffeineaddict.caffeineaddictmode.registry.ModItems;
import com.caffeineaddict.caffeineaddictmode.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class WaterDispenserBlockEntity extends BlockEntity {
    private int waterLevel = 0;
    private static final int MAX_WATER = 10;

    public WaterDispenserBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WATER_DISPENSER.get(), pos, state);
    }

    public void fill() {
        if (waterLevel < MAX_WATER) {
            waterLevel = Math.min(MAX_WATER, waterLevel + 10);
            setChanged();
            System.out.println("[정수기] HOT WATER 요청, 현재 수량: " + waterLevel);
        }
    }

    public void giveHotWater(Player player) {
        if (waterLevel > 0 && level instanceof ServerLevel serverLevel) {
            Direction facing = Direction.NORTH; // 나중엔 BlockState에서 가져오면 됨
            Vec3 posVec = Vec3.atCenterOf(worldPosition).add(Vec3.atLowerCornerOf(facing.getNormal()).scale(0.5));
            ItemStack hotWater = new ItemStack(ModItems.HOT_WATER.get());

            ItemEntity item = new ItemEntity(serverLevel, posVec.x, posVec.y, posVec.z, hotWater);
            item.setDeltaMovement(facing.step().x() * 0.2, 0.1, facing.step().z() * 0.2);
            item.setPickUpDelay(10); // 바로 못 줍게 약간 지연

            serverLevel.addFreshEntity(item);
            waterLevel--;
            setChanged();
        }
    }

    public void giveCoolWater(Player player) {
        if (waterLevel > 0 && level instanceof ServerLevel serverLevel) {
            Direction facing = Direction.NORTH; // 향후 BlockState에서 받아오면 동적으로 가능
            Vec3 posVec = Vec3.atCenterOf(worldPosition)
                    .add(Vec3.atLowerCornerOf(facing.getNormal()).scale(0.5));
            ItemStack coolWater = new ItemStack(ModItems.COOL_WATER.get());

            ItemEntity item = new ItemEntity(serverLevel, posVec.x, posVec.y, posVec.z, coolWater);
            item.setDeltaMovement(
                    facing.step().x() * 0.2,
                    0.1,
                    facing.step().z() * 0.2
            );
            item.setPickUpDelay(10);  // 줍기 지연

            serverLevel.addFreshEntity(item);
            waterLevel--;
            setChanged();
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        waterLevel = tag.getInt("WaterLevel");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("WaterLevel", waterLevel);
    }
}


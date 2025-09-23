package com.caffeineaddict.caffeineaddictmode.blocks.IceMaker;

import com.caffeineaddict.caffeineaddictmode.registry.ModBlockEntities;
import com.caffeineaddict.caffeineaddictmode.registry.ModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class IceMakerBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(2); // 0 = input, 1 = output
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);

    private int progress = 0;
    private int maxProgress = 100;

    public IceMakerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ICE_MAKER.get(), pos, state);
    }

    // 슬롯 제공
    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    // 메뉴 이름
    @Override
    public Component getDisplayName() {
        return Component.literal("제빙기");
    }

    // GUI 열기
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new IceMakerMenu(id, playerInventory, this);
    }

    // capability
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return handler.cast();
        }
        return super.getCapability(cap, side);
    }

    // 진행도 데이터 전송
    private final ContainerData data = new ContainerData() {
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> maxProgress;
                default -> 0;
            };
        }

        public void set(int index, int value) {
            if (index == 0) progress = value;
            if (index == 1) maxProgress = value;
        }

        public int getCount() {
            return 2;
        }
    };

    public ContainerData getContainerData() {
        return data;
    }

    // 틱마다 실행
    public static void tick(Level level, BlockPos pos, BlockState state, IceMakerBlockEntity entity) {
        if (level.isClientSide) return;

        ItemStack input = entity.itemHandler.getStackInSlot(0);
        ItemStack output = entity.itemHandler.getStackInSlot(1);

        boolean validInput = input.getItem() == ModItems.HOT_WATER.get()
                || input.getItem() == ModItems.COOL_WATER.get();

        boolean validOutput = output.isEmpty() ||
                (output.getItem() == ModItems.ICE.get() && output.getCount() < output.getMaxStackSize());

        if (validInput && validOutput) {
            entity.progress++;
            if (entity.progress >= entity.maxProgress) {
                // 입력 아이템 소비
                entity.itemHandler.extractItem(0, 1, false);

                // 출력 슬롯에 ICE 생성
                if (output.isEmpty()) {
                    entity.itemHandler.setStackInSlot(1, new ItemStack(ModItems.ICE.get(), 1));
                } else {
                    output.grow(1);
                }

                // 사운드
                level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 0.7f, 1.0f);

                entity.progress = 0;
            }
        } else {
            entity.progress = 0;
        }

        entity.setChanged();
    }
}
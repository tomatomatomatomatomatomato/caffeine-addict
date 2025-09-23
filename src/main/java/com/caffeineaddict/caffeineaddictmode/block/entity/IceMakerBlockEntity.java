package com.caffeineaddict.caffeineaddictmode.block.entity;

import com.caffeineaddict.caffeineaddictmode.menu.IceMakerMenu;
import com.caffeineaddict.caffeineaddictmode.registry.ModBlockEntities;
import com.caffeineaddict.caffeineaddictmode.ModItems;

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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import com.caffeineaddict.caffeineaddictmode.sound.ModSoundEvents;
import net.minecraft.sounds.SoundSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class IceMakerBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(2); // 0 = input, 1 = output
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);

    private int progress = 0;
    private int maxProgress = 100;

    private boolean wasWorking = false;
    private long lastSoundGameTime = -200;
    private static final int SOUND_COOLDOWN_TICKS = 240;

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
        return Component.literal("Ice Maker");
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
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
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

        ItemStack input  = entity.itemHandler.getStackInSlot(0);
        ItemStack output = entity.itemHandler.getStackInSlot(1);

        boolean validInput  = input.getItem() == ModItems.HOT_WATER.get() || input.getItem() == ModItems.COOL_WATER.get();
        boolean validOutput = output.isEmpty()
                || (output.getItem() == ModItems.ICE.get() && output.getCount() < output.getMaxStackSize());

        boolean canProcess = validInput && validOutput;

        if (canProcess) {
            // ----- 진행/생산: 여기서만 처리 -----
            entity.progress++;
            if (entity.progress >= entity.maxProgress) {
                // 입력 1개 소비
                entity.itemHandler.extractItem(0, 1, false);
                // 출력 1개 추가 (빈/기존 상관 없이 안전)
                entity.itemHandler.insertItem(1, new ItemStack(ModItems.ICE.get(), 1), false);
                entity.progress = 0;
            }

            // ----- 사운드: 작업 중엔 주기 재생 -----
            long now = level.getGameTime();
            if (!entity.wasWorking || now - entity.lastSoundGameTime >= SOUND_COOLDOWN_TICKS) {
                level.playSound(null, pos, ModSoundEvents.ICE_MAKER_SOUND.get(), SoundSource.BLOCKS, 0.66f, 1.0f);
                entity.lastSoundGameTime = now;
            }
        } else {
            // 가공 불가: 진행도 리셋 + 소리 정지(막 멈춘 프레임이면)
            if (entity.wasWorking) {
                entity.stopIceMakerSoundServer();
                entity.lastSoundGameTime = level.getGameTime() - SOUND_COOLDOWN_TICKS; // 재시작 즉시 허용
            }
            entity.progress = 0;
        }

        entity.wasWorking = canProcess;
        entity.setChanged();
    }

    // 서버에서 정지 패킷 보내는 헬퍼
    private void stopIceMakerSoundServer() {
        if (this.level instanceof ServerLevel server) {
            var pkt = new ClientboundStopSoundPacket(
                    ModSoundEvents.ICE_MAKER_SOUND.get().getLocation(),
                    SoundSource.BLOCKS
            );
            for (ServerPlayer p : server.players()) {
                p.connection.send(pkt);
            }
        }
    }

    // 블록엔티티 제거/언로드 시에도 확실히 끊기
    @Override
    public void setRemoved() {
        stopIceMakerSoundServer();
        super.setRemoved();
    }
    @Override
    public void onChunkUnloaded() {
        stopIceMakerSoundServer();
        super.onChunkUnloaded();
    }
}
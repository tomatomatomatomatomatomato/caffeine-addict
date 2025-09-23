package com.caffeineaddict.caffeineaddictmode.blocks.Grinder;

import com.caffeineaddict.caffeineaddictmode.registry.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraft.world.inventory.ContainerData;

public class GrinderMenu extends AbstractContainerMenu {
    private final GrinderBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public GrinderMenu(int id, Inventory playerInv, FriendlyByteBuf extraData) {
        this(id, playerInv, (GrinderBlockEntity) playerInv.player.level.getBlockEntity(extraData.readBlockPos()));
    }

    public GrinderMenu(int id, Inventory playerInv, GrinderBlockEntity blockEntity) {
        super(ModMenus.GRINDER_MENU.get(), id);
        this.blockEntity = blockEntity;
        this.level = playerInv.player.level;
        this.data = blockEntity.getContainerData();

        addDataSlots(data);

        // BlockEntity 슬롯 (0: input, 1: output)
        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 0, 56, 35));
        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 1, 116, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        // 플레이어 인벤토리
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // 핫바
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, 142));
        }
    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

//    public int getScaledProgress() {
//        int progress = blockEntity.getProgress();
//        int maxProgress = blockEntity.getMaxProgress();
//        int barWidth = 24; // 진행바 최대 길이 (픽셀 단위)
//        return maxProgress != 0 && progress != 0 ? progress * barWidth / maxProgress : 0;
//    }
    public int getScaledProgress() {
        int progress = data.get(0); // 진행 상태 값 (0~N)
        int maxProgress = data.get(1); // 최대값
        int progressBarWidth = 24; // arrow.png 가로 길이

        return maxProgress != 0 && progress != 0 ? (progress * progressBarWidth) / maxProgress : 0;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY; // 나중에 자동 이동 기능을 구현하고 싶다면 이 로직을 바꿔도 돼
    }
}
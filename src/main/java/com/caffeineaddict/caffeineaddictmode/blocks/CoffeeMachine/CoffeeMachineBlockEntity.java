package com.caffeineaddict.caffeineaddictmode.blocks.CoffeeMachine;

import com.caffeineaddict.caffeineaddictmode.items.drink.Coffee.Espresso;
import com.caffeineaddict.caffeineaddictmode.registry.ModBlockEntities;
import com.caffeineaddict.caffeineaddictmode.registry.ModItems;
import com.caffeineaddict.caffeineaddictmode.registry.ModSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CoffeeMachineBlockEntity extends BlockEntity implements MenuProvider {
    private final int SHOT_INV_SIZE = 6;
    private final int STEAM_INV_SIZE = 4;
    private final Container shotInventory = new SimpleContainer(SHOT_INV_SIZE);
    private final Container steamInventory = new SimpleContainer(STEAM_INV_SIZE);
    private final ContainerData gauges = new SimpleContainerData((SHOT_INV_SIZE + STEAM_INV_SIZE) / 2);
    private String lastUsedBy = "";
    private final String LAST_USEDBY_KEY = "LastUsedBy";

    // 사운드 관련
    private boolean wasWorking = false;
    private long lastSoundGameTime = -200;
    private static final int SOUND_COOLDOWN_TICKS = 220;

    public CoffeeMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COFFEE_MACHINE.get(), pos, state);
    }

    public void setLastUsedBy(String playerName) {
        this.lastUsedBy = playerName;
    }

    public String getLastUsedBy() {
        return lastUsedBy;
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Coffee Machine");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, Player player) {
        return new CoffeeMachineMenu(id, playerInventory, this.worldPosition, this.shotInventory, this.steamInventory, this.gauges);
    }

    // Save data
    // Save data
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString(LAST_USEDBY_KEY, lastUsedBy);
        NonNullList<ItemStack> stacks = NonNullList.withSize(SHOT_INV_SIZE+STEAM_INV_SIZE, ItemStack.EMPTY);
        for (int i = 0; i < SHOT_INV_SIZE; i++) {
            stacks.set(i, shotInventory.getItem(i));
        }
        for (int j = 0; j < STEAM_INV_SIZE; j++) {
            stacks.set(j+SHOT_INV_SIZE, steamInventory.getItem(j));
        }
        ContainerHelper.saveAllItems(tag, stacks);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(LAST_USEDBY_KEY)) {
            lastUsedBy = tag.getString(LAST_USEDBY_KEY);
        }
        NonNullList<ItemStack> stacks = NonNullList.withSize(SHOT_INV_SIZE+STEAM_INV_SIZE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, stacks);

        for (int i = 0; i < SHOT_INV_SIZE; i++) {
            shotInventory.setItem(i, stacks.get(i));
        }
        for (int j = 0; j<STEAM_INV_SIZE; j++){
            steamInventory.setItem(j, stacks.get(j+SHOT_INV_SIZE));
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    public void brew(int currentIndex){
        if(currentIndex>=SHOT_INV_SIZE){
            throw new RuntimeException("커피머신 슬롯 개수를 넘어갈 수 없습니다");
        }
        ItemStack input = shotInventory.getItem(currentIndex);
        ItemStack output = shotInventory.getItem(currentIndex + (SHOT_INV_SIZE / 2));

        if (input.is(ModItems.COFFEE_POWDER.get()) && output.is(ModItems.SHOT_CUP.get())) {
            // 커피콩 소비
            shotInventory.removeItem(currentIndex, 1);

            int progress = gauges.get(currentIndex+1);
            int distance = Math.abs(progress - 12);
            // 에스프레소 추출
            int quality = 0;

            if (distance <= 1) {
                quality = 3; // 최고 등급
            } else if (distance <= 4) {
                quality = 2;
            } else {
                quality = 1; // 완전 멀어지면 최저 등급
            }

            ItemStack espresso = new ItemStack(ModItems.ESPRESSO.get(), 1);
            Espresso.withMeta(espresso, lastUsedBy, quality);
            shotInventory.setItem(currentIndex + 3, espresso);
        }
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        boolean anyWorking = false;
        anyWorking |= processShotInventory(level);
        anyWorking |= processSteamInventory(level);

        // 사운드 제어
        long now = level.getGameTime();
        if (anyWorking) {
            if (now - lastSoundGameTime >= SOUND_COOLDOWN_TICKS) {
                level.playSound(null, worldPosition, ModSoundEvents.COFFEE_MACHINE_SOUND.get(),
                        SoundSource.BLOCKS, 2.0f, 1.0f);
                lastSoundGameTime = now;
            }
        } else {
            if (wasWorking) {
                stopCoffeeMachineSoundServer();
                lastSoundGameTime = now - SOUND_COOLDOWN_TICKS;
            }
        }
        wasWorking = anyWorking;

        setChanged();
    }

    private boolean processShotInventory(Level level) {
        boolean working = false;
        for (int i = 0; i < SHOT_INV_SIZE / 2; i++) {
            ItemStack input = shotInventory.getItem(i);
            ItemStack output = shotInventory.getItem(i + (SHOT_INV_SIZE / 2));

            if (input.is(ModItems.COFFEE_POWDER.get()) && output.is(ModItems.SHOT_CUP.get())) {
                working = true;
                handleProgress(level, i + 1, gauges.get(i + 1), () -> {
                    level.playSound(null, worldPosition, SoundEvents.BREWING_STAND_BREW,
                            SoundSource.BLOCKS, 1.0f, 1.0f);
                });
            } else {
                gauges.set(i + 1, 0);
            }
        }
        return working;
    }

    private boolean processSteamInventory(Level level) {
        boolean working = false;
        for (int j = 0; j < STEAM_INV_SIZE / 2; j++) {
            ItemStack input = steamInventory.getItem(j);
            ItemStack output = steamInventory.getItem(j + (STEAM_INV_SIZE / 2));
            int gaugeIndex = j == 0 ? 0 : ((SHOT_INV_SIZE + STEAM_INV_SIZE) / 2) - 1;

            if (input.is(Items.MILK_BUCKET) &&
                    (output.is(ItemStack.EMPTY.getItem()) || output.is(ModItems.STEAMED_MILK.get()))) {

                working = true;
                handleProgress(level, gaugeIndex, gauges.get(gaugeIndex), () -> {});
                if (gauges.get(gaugeIndex) >= 24) {
                    steamInventory.removeItem(j, 1);
                    steamInventory.setItem(j + (STEAM_INV_SIZE / 2), new ItemStack(ModItems.STEAMED_MILK.get(), 1));
                }
            }
        }
        return working;
    }

    private void handleProgress(Level level, int index, int currentProgress, Runnable onTickSound) {
        int newProgress = (currentProgress + 1) % 25;
        gauges.set(index, newProgress);
        if (newProgress % 20 == 0 && newProgress != 0) {
            onTickSound.run();
        }
    }

    private void stopCoffeeMachineSoundServer() {
        if (this.level instanceof ServerLevel server) {
            var pkt = new ClientboundStopSoundPacket(
                    ModSoundEvents.COFFEE_MACHINE_SOUND.get().getLocation(),
                    SoundSource.BLOCKS
            );
            for (ServerPlayer p : server.players()) {
                p.connection.send(pkt);
            }
        }
    }

    @Override
    public void setRemoved() {
        stopCoffeeMachineSoundServer();
        super.setRemoved();
    }

    @Override
    public void onChunkUnloaded() {
        stopCoffeeMachineSoundServer();
        super.onChunkUnloaded();
    }

    protected Container getShotContainerForRemoval(){
        return shotInventory;
    }

    protected Container getSteamContainerForRemoval(){
        return steamInventory;
    }

    public Container getShotInventory() {
        return shotInventory;
    }

    public Container getSteamInventory() {
        return steamInventory;
    }
}

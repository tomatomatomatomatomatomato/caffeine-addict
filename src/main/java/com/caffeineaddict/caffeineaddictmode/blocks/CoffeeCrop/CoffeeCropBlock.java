package com.caffeineaddict.caffeineaddictmode.blocks.CoffeeCrop;

import com.caffeineaddict.caffeineaddictmode.registry.ModItems;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;

public class CoffeeCropBlock extends CropBlock {
    public CoffeeCropBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return ModItems.COFFEE_BEAN.get();
    }

    @Override
    public int getMaxAge() {
        return 7;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        List<ItemStack> drops = new ArrayList<>();

        int age = state.getValue(this.getAgeProperty());
        RandomSource random = builder.getLevel().getRandom();

        drops.add(new ItemStack(ModItems.COFFEE_BEAN.get()));

        if (age >= getMaxAge()) {
            drops.add(new ItemStack(ModItems.COFFEE_BEAN.get(), 1 + random.nextInt(2)));
            if (random.nextFloat() < 0.5f) {
                drops.add(new ItemStack(ModItems.COFFEE_BEAN.get()));
            }
        }

        return drops;
    }
}

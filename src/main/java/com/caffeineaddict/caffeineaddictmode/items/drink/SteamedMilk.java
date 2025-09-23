package com.caffeineaddict.caffeineaddictmode.items.drink;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SteamedMilk extends Item {
    public SteamedMilk(Properties props) {
        super(props);
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        return new ItemStack(Items.BUCKET);
    }

}

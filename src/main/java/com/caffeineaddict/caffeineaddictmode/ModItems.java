package com.caffeineaddict.caffeineaddictmode;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import com.caffeineaddict.caffeineaddictmode.drink.Coffee;
import com.caffeineaddict.caffeineaddictmode.drink.Drink;
import java.util.List;
import com.caffeineaddict.caffeineaddictmode.registry.ModBlocks;
import com.caffeineaddict.caffeineaddictmode.drink.Tea;

import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final String MOD_ID = CaffeineAddictMode.MOD_ID;

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static final RegistryObject<Item> COFFEE_MACHINE_ITEM =
            ModItems.ITEMS.register("coffee_machine", () ->
                    new BlockItem(ModBlocks.COFFEE_MACHINE.get(), new Item.Properties().tab(ModCreativeTab.CAFFEINE_TAB)));

    public static final RegistryObject<Item> GROUND_COFFEE =
            ITEMS.register("ground_coffee", () ->
                    new Item(new Item.Properties().tab(ModCreativeTab.CAFFEINE_TAB)));

    public static final RegistryObject<Item> SHOT_CUP =
            ITEMS.register("shot_cup", () ->
                    new Item(new Item.Properties().stacksTo(16).tab(ModCreativeTab.CAFFEINE_TAB)));

    public static RegistryObject<Item> getCoffeeMachine(){
        return COFFEE_MACHINE_ITEM;
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
  
    public static final RegistryObject<Item> GRINDER_ITEM =
            ModItems.ITEMS.register("grinder", () ->
                    new BlockItem(ModBlocks.GRINDER_BLOCK.get(), new Item.Properties().tab(ModCreativeTab.CAFFEINE_TAB))
            );

    public static final RegistryObject<Item> SHOT_MACHINE =
            ITEMS.register("shot_machine", () ->
                    new Item(new Item.Properties().stacksTo(16).tab(ModCreativeTab.CAFFEINE_TAB)));

    public static final RegistryObject<Item> STEAMER =
            ITEMS.register("steamer", () ->
                    new Item(new Item.Properties().stacksTo(16).tab(ModCreativeTab.CAFFEINE_TAB)));

    /**
     * Ingredients
     */
    public static final RegistryObject<Item> COFFEE_BEAN = ITEMS.register(
            "coffee_bean",
            () -> new Item(new Item.Properties().tab(ModCreativeTab.CAFFEINE_TAB))
    );

    public static final RegistryObject<Item> ROASTED_COFFEE_BEAN = ITEMS.register(
            "roasted_coffee_bean",
            () -> new Item(new Item.Properties().tab(ModCreativeTab.CAFFEINE_TAB))
    );

    public static final RegistryObject<Item> COFFEE_POWDER = ITEMS.register(
            "coffee_powder",
            () -> new Item(new Item.Properties().tab(ModCreativeTab.CAFFEINE_TAB))
    );

    public static final RegistryObject<Item> MILK = ITEMS.register(
            "milk",
            () -> new Item(new Item.Properties().tab(ModCreativeTab.CAFFEINE_TAB))
    );

    public static final RegistryObject<Item> ICE = ITEMS.register(
            "ice",
            () -> new Item(new Item.Properties().tab(ModCreativeTab.CAFFEINE_TAB))
    );

    public static final RegistryObject<Item> HOT_WATER = ITEMS.register(
            "hot_water",
            () -> new Item(new Item.Properties().tab(ModCreativeTab.CAFFEINE_TAB))
    );

    public static final RegistryObject<Item> COOL_WATER = ITEMS.register(
            "cool_water",
            () -> new Item(new Item.Properties().tab(ModCreativeTab.CAFFEINE_TAB))
    );

    public static final RegistryObject<Item> CUP = ITEMS.register(
            "cup",
            () -> new Item(new Item.Properties().tab(ModCreativeTab.CAFFEINE_TAB))
    );

    /**
     * Drink
     */
    public static final RegistryObject<Item> ESPRESSO = ITEMS.register(
            "espresso",
            () -> new Coffee(1, 1, List.of(MobEffects.MOVEMENT_SPEED), 15, 0)
    );

    public static final RegistryObject<Item> ICE_WATER = ITEMS.register(
            "ice_water",
            () -> new Drink(1, 1, List.of(MobEffects.MOVEMENT_SPEED), 15, 0)
    );

    public static final RegistryObject<Item> AMERICANO = ITEMS.register(
            "americano",
            () -> new Coffee(1, 1, List.of(MobEffects.MOVEMENT_SPEED), 15, 0)
    );

    public static final RegistryObject<Item> ICE_AMERICANO = ITEMS.register(
            "ice_americano",
            () -> new Coffee(1, 1, List.of(MobEffects.MOVEMENT_SPEED), 15, 0)
    );

    public static final RegistryObject<Item> LATTE = ITEMS.register(
            "latte",
            () -> new Coffee(1, 1, List.of(MobEffects.MOVEMENT_SPEED), 15, 0)
    );

    public static final RegistryObject<Item> ICE_LATTE = ITEMS.register(
            "ice_latte",
            () -> new Coffee(1, 1, List.of(MobEffects.MOVEMENT_SPEED), 15, 0)
    );

    /**
     * Block
     */
    public static final RegistryObject<Item> WATER_DISPENSER_ITEM =
            ITEMS.register("water_dispenser", () ->
                    new BlockItem(ModBlocks.WATER_DISPENSER.get(),
                            new Item.Properties().tab(ModCreativeTab.CAFFEINE_TAB))
            );

    public static final RegistryObject<Item> ICE_MAKER =
            ModItems.ITEMS.register("ice_maker", () ->
                    new BlockItem(ModBlocks.ICE_MAKER.get(), new Item.Properties().tab(ModCreativeTab.CAFFEINE_TAB))
            );

    /**
     * dried tea leaf
     */
    public static final RegistryObject<Item> DRIED_DANDELION_LEAF =
            ITEMS.register("dried_dandelion_leaf", () ->
                    new Item(new Item.Properties().tab(ModCreativeTab.TEA_TAB)));

    public static final RegistryObject<Item> DRIED_ALLIUM_LEAF =
            ITEMS.register("dried_allium_leaf", () ->
                    new Item(new Item.Properties().tab(ModCreativeTab.TEA_TAB)));

    public static final RegistryObject<Item> DRIED_AZURE_BLUET_LEAF =
            ITEMS.register("dried_azure_bluet_leaf", () ->
                    new Item(new Item.Properties().tab(ModCreativeTab.TEA_TAB)));

    public static final RegistryObject<Item> DRIED_CORNFLOWER_LEAF =
            ITEMS.register("dried_cornflower_leaf", () ->
                    new Item(new Item.Properties().tab(ModCreativeTab.TEA_TAB)));

    public static final RegistryObject<Item> DRIED_POPPY_LEAF =
            ITEMS.register("dried_poppy_leaf", () ->
                    new Item(new Item.Properties().tab(ModCreativeTab.TEA_TAB)));

    public static final RegistryObject<Item> DRIED_WITHER_ROSE_LEAF =
            ITEMS.register("dried_wither_rose_leaf", () ->
                    new Item(new Item.Properties().tab(ModCreativeTab.TEA_TAB)));

    public static final RegistryObject<Item> DRIED_FERN_LEAF =
            ITEMS.register("dried_fern_leaf", () ->
                    new Item(new Item.Properties().tab(ModCreativeTab.TEA_TAB)));

    public static final RegistryObject<Item> DRIED_WARPED_ROOTS_LEAF =
            ITEMS.register("dried_warped_roots_leaf", () ->
                    new Item(new Item.Properties().tab(ModCreativeTab.TEA_TAB)));

    /**
     * tea
     */
    public static final RegistryObject<Item> DANDELION_TEA = ITEMS.register(
            "dandelion_tea",
            () -> new Tea(List.of(MobEffects.DIG_SLOWDOWN), 20, 0)
    );

    public static final RegistryObject<Item> POPPY_TEA = ITEMS.register(
            "poppy_tea",
            () -> new Tea(List.of(MobEffects.CONFUSION), 10, 0)
    );

    public static final RegistryObject<Item> ALLIUM_TEA = ITEMS.register(
            "allium_tea",
            () -> new Tea(List.of(MobEffects.WEAKNESS), 20, 0)
    );

    public static final RegistryObject<Item> AZURE_BLUET_TEA = ITEMS.register(
            "azure_bluet_tea",
            () -> new Tea(List.of(MobEffects.BLINDNESS), 10, 0)
    );

    public static final RegistryObject<Item> CORNFLOWER_TEA = ITEMS.register(
            "cornflower_tea", // 효과 부여 음수로 안된대서 일단 구속 걸어놨어요
            () -> new Tea(List.of(MobEffects.MOVEMENT_SLOWDOWN), 10, 0)
    );

    public static final RegistryObject<Item> WITHER_ROSE_TEA = ITEMS.register(
            "wither_rose_tea",
            () -> new Tea(List.of(MobEffects.WITHER), 10, 0)
    );

    public static final RegistryObject<Item> FERN_TEA = ITEMS.register(
            "fern_tea",
            () -> new Tea(List.of(MobEffects.POISON), 10, 0)
    );

    public static final RegistryObject<Item> WARPED_ROOTS_TEA = ITEMS.register(
            "warped_roots_tea",
            () -> new Tea(List.of(MobEffects.HARM), 1, 0)
    );

    public static void register() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
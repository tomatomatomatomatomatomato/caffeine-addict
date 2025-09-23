package com.caffeineaddict.caffeineaddictmode.registry;

import com.caffeineaddict.caffeineaddictmode.blocks.CoffeeCrop.CoffeeCropBlock;
import com.caffeineaddict.caffeineaddictmode.blocks.CoffeeCrop.WildCoffeeBushBlock;
import com.caffeineaddict.caffeineaddictmode.blocks.WaterDispenser.WaterDispenserBlock;
import com.caffeineaddict.caffeineaddictmode.blocks.Grinder.GrinderBlock;
import com.caffeineaddict.caffeineaddictmode.blocks.IceMaker.IceMakerBlock;

import com.caffeineaddict.caffeineaddictmode.CaffeineAddictMode;
import com.caffeineaddict.caffeineaddictmode.blocks.CoffeeMachine.CoffeeMachineBlock;
import com.caffeineaddict.caffeineaddictmode.blocks.CoffeeMachine.CoffeeMachinePartBlock;
import com.caffeineaddict.caffeineaddictmode.blocks.CoffeeMachine.CoffeeMachineBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, CaffeineAddictMode.MOD_ID);

    public static final RegistryObject<Block> WATER_DISPENSER =
            BLOCKS.register("water_dispenser",
                    () -> new WaterDispenserBlock(BlockBehaviour.Properties.of(Material.METAL).strength(3.0f)));

    public static final RegistryObject<Block> GRINDER_BLOCK =
            BLOCKS.register("grinder", () ->
                    new GrinderBlock(Properties.of(Material.STONE).strength(2.0f).noOcclusion()));

    public static final RegistryObject<Block> ICE_MAKER =
            BLOCKS.register("ice_maker", () -> new IceMakerBlock());

    public static final RegistryObject<Block> COFFEE_MACHINE =
            BLOCKS.register("coffee_machine",
                    () -> new CoffeeMachineBlock(Properties.of(Material.STONE).strength(2.0f).noOcclusion()));

    public static final RegistryObject<Block> COFFEE_MACHINE_PART =
            BLOCKS.register("coffee_machine_part",
                    () -> new CoffeeMachinePartBlock(Properties.of(Material.STONE).strength(2.0f).noOcclusion()));


    public static final RegistryObject<Block> COFFEE_CROP =
            BLOCKS.register("coffee_crop", () ->
                    new CoffeeCropBlock(BlockBehaviour.Properties.copy(Blocks.CARROTS)));

    public static final RegistryObject<Block> WILD_COFFEE_BUSH =
            BLOCKS.register("wild_coffee_bush",
                    () -> new WildCoffeeBushBlock(BlockBehaviour.Properties
                            .of(Material.PLANT)
                            .noCollission()
                            .instabreak()
                            .sound(SoundType.GRASS)));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
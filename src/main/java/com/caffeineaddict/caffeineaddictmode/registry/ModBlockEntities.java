package com.caffeineaddict.caffeineaddictmode.registry;

import com.caffeineaddict.caffeineaddictmode.CaffeineAddictMode;
import com.caffeineaddict.caffeineaddictmode.blocks.Grinder.GrinderBlockEntity;
import com.caffeineaddict.caffeineaddictmode.blocks.WaterDispenser.WaterDispenserBlockEntity;
import com.caffeineaddict.caffeineaddictmode.blocks.IceMaker.IceMakerBlockEntity;
import com.caffeineaddict.caffeineaddictmode.blocks.CoffeeMachine.CoffeeMachineBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CaffeineAddictMode.MOD_ID);

    public static final RegistryObject<BlockEntityType<WaterDispenserBlockEntity>> WATER_DISPENSER =
            BLOCK_ENTITIES.register("water_dispenser", () ->
                    BlockEntityType.Builder.of(WaterDispenserBlockEntity::new,
                            ModBlocks.WATER_DISPENSER.get()).build(null));

    public static final RegistryObject<BlockEntityType<IceMakerBlockEntity>> ICE_MAKER =
            BLOCK_ENTITIES.register("ice_maker", () ->
                    BlockEntityType.Builder.of(IceMakerBlockEntity::new,
                            ModBlocks.ICE_MAKER.get()).build(null));

    // Coffee machine block entity
    public static final RegistryObject<BlockEntityType<CoffeeMachineBlockEntity>> COFFEE_MACHINE =
            BLOCK_ENTITIES.register("coffee_machine",
                    () -> BlockEntityType.Builder.of(CoffeeMachineBlockEntity::new, ModBlocks.COFFEE_MACHINE.get()).build(null));

    public static final RegistryObject<BlockEntityType<GrinderBlockEntity>> GRINDER =
            BLOCK_ENTITIES.register("grinder",
                    () -> BlockEntityType.Builder.of(GrinderBlockEntity::new, ModBlocks.GRINDER_BLOCK.get()).build(null)
            );



    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITIES.register(modEventBus);
    }
}
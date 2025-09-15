package com.caffeineaddict.caffeineaddictmode;

import com.caffeineaddict.caffeineaddictmode.blocks.CoffeeMachine.CoffeeMachineBlockEntity;
import com.caffeineaddict.caffeineaddictmode.registry.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CoffeeMachineBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CaffeineAddictMode.MOD_ID);

    // Coffee machine block entity
    public static final RegistryObject<BlockEntityType<CoffeeMachineBlockEntity>> COFFEE_MACHINE =
            BLOCK_ENTITIES.register("coffee_machine",
                    () -> BlockEntityType.Builder.of(CoffeeMachineBlockEntity::new, ModBlocks.COFFEE_MACHINE.get()).build(null));

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITIES.register(modEventBus);
    }
}

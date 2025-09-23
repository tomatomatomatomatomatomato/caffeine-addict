package com.caffeineaddict.caffeineaddictmode.menu;

import com.caffeineaddict.caffeineaddictmode.CaffeineAddictMode;

import com.caffeineaddict.caffeineaddictmode.blocks.CoffeeMachine.CoffeeMachineMenu;
import com.caffeineaddict.caffeineaddictmode.blocks.Grinder.GrinderMenu;
import com.caffeineaddict.caffeineaddictmode.blocks.IceMaker.IceMakerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.common.extensions.IForgeMenuType;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, CaffeineAddictMode.MOD_ID);

    public static final RegistryObject<MenuType<GrinderMenu>> GRINDER_MENU =
            MENUS.register("grinder_menu",
                    () -> IForgeMenuType.create(GrinderMenu::new));

    public static final RegistryObject<MenuType<IceMakerMenu>> ICE_MAKER_MENU =
            MENUS.register("ice_maker_menu",
                    () -> IForgeMenuType.create(IceMakerMenu::new));

    public static final RegistryObject<MenuType<CoffeeMachineMenu>> COFFEE_MACHINE_MENU =
            MENUS.register("coffee_machine_menu",
                    () -> IForgeMenuType.create(CoffeeMachineMenu::new));
}
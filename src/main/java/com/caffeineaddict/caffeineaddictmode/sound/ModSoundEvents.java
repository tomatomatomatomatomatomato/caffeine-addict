package com.caffeineaddict.caffeineaddictmode.sound;

import com.caffeineaddict.caffeineaddictmode.CaffeineAddictMode;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSoundEvents {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CaffeineAddictMode.MOD_ID);

    // 공통 등록 유틸
    private static RegistryObject<SoundEvent> register(String id) {
        return SOUNDS.register(id,
                () -> new SoundEvent(new ResourceLocation(CaffeineAddictMode.MOD_ID, id)));
    }

    public static final RegistryObject<SoundEvent> GRINDER_SOUND         = register("block.grinder");
    public static final RegistryObject<SoundEvent> ICE_MAKER_SOUND       = register("block.ice_maker");
    public static final RegistryObject<SoundEvent> COFFEE_MACHINE_SOUND = register("block.coffee_machine");
}
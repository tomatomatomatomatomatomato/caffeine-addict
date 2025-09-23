package com.caffeineaddict.caffeineaddictmode.items.drink.Tea;

import com.caffeineaddict.caffeineaddictmode.items.drink.Drink;
import com.caffeineaddict.caffeineaddictmode.items.drink.DrinkState;
import com.caffeineaddict.caffeineaddictmode.registry.ModItems;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Tea 아이템 클래스.
 * 기본적으로 nutrition=1, saturation=0.3, FIRE_RESISTANCE 60초 버프를 주며,
 * de-buff는 필수로, buff는 선택적으로 지정 가능.
 */
public class Tea extends Drink {
    private final List<MobEffect> badEffects;
    private final int badDuration;
    private final int badAmplifier;

    /**
     * 기본 buff(FIRE_RESISTANCE 60초)를 적용하고,
     * de-buff만 지정하는 Tea 생성자.
     *
     * @param badEffects   적용할 de-buff
     * @param badDuration  de-buff 지속 시간 (초)
     * @param badAmplifier de-buff 증폭치
     */
    public Tea(List<MobEffect> badEffects, int badDuration, int badAmplifier) {
        this(List.of(MobEffects.FIRE_RESISTANCE), 60, 0, badEffects, badDuration, badAmplifier);
    }

    /**
     * goodEffect와 badEffect를 모두 지정하는 Tea 생성자.
     *
     * @param goodEffects    적용할 buff
     * @param goodDuration   buff 지속 시간 (초)
     * @param goodAmplifier  buff 증폭치
     * @param badEffects     적용할 de-buff
     * @param badDuration    de-buff 지속 시간 (초)
     * @param badAmplifier   de-buff 증폭치
     */
    public Tea(List<MobEffect> goodEffects, int goodDuration, int goodAmplifier,
               List<MobEffect> badEffects, int badDuration, int badAmplifier) {
        // nutrition=1, saturation=0.3 고정
        super(1, 0.3F, goodEffects, goodDuration, goodAmplifier, DrinkState.HOT);
        this.badEffects = List.copyOf(badEffects);
        this.badDuration = badDuration;
        this.badAmplifier = badAmplifier;
    }

    @Override
    protected List<MobEffectInstance> createEffectInstances(ItemStack stack) {
        List<MobEffectInstance> effectsList = new ArrayList<>();
        // goodEffects (Drink가 관리)
        effectsList.addAll(super.createEffectInstances(stack));
        // badEffects
        effectsList.addAll(
                badEffects.stream()
                        .map(effect -> new MobEffectInstance(effect, badDuration * 20, badAmplifier))
                        .toList()
        );
        return effectsList;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        if (!level.isClientSide) {
            createEffectInstances(stack).forEach(entity::addEffect);

            if (entity instanceof Player player && !player.getAbilities().instabuild) {
                ItemStack drop = new ItemStack(ModItems.CUP.get());
                if (!player.getInventory().add(drop)) {
                    player.drop(drop, false);
                }
            }
        }
        return result;
    }
}
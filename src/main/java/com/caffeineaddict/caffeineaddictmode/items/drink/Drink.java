package com.caffeineaddict.caffeineaddictmode.items.drink;

import com.caffeineaddict.caffeineaddictmode.registry.ModCreativeTab;
import com.caffeineaddict.caffeineaddictmode.registry.ModItems;
import java.util.List;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/**
 * 음료 아이템을 정의.
 * 포션 효과 적용, 스택 불가능, 사용 후 빈 컵 드롭
 * <p>
 * {@code Coffee}에서 확장하여 사용
 *
 * @author @moongua404
 */
public class Drink extends Item {
    protected final List<MobEffect> effects;
    protected int duration;     // 초 단위
    protected int amplifier;
    protected DrinkState drinkState;
    /**
     * Drink 생성자
     *
     * @param nutrition   음식의 포만감 수치
     * @param saturation  음식의 포화도 수치
     * @param effects      적용할 효과 (예: 이동 속도 향상 등)
     * @param duration    효과 지속 시간 (초 단위)
     * @param amplifier   효과 증폭 수치
     */
    public Drink(int nutrition, float saturation, List<MobEffect> effects, int duration, int amplifier, DrinkState drinkState) {
        super(new Item.Properties()
                .tab(ModCreativeTab.CAFFEINE_TAB)
                .stacksTo(1)
                .food(new FoodProperties.Builder()
                        .nutrition(nutrition)
                        .saturationMod(saturation)
                        .alwaysEat()
                        .build()));
        this.effects = List.copyOf(effects);
        this.duration = duration;
        this.amplifier = amplifier;
        this.drinkState = drinkState;
    }

    protected List<MobEffectInstance> createEffectInstances(ItemStack stack) {
        return effects.stream()
                .map(effect -> new MobEffectInstance(effect, duration * 20, amplifier))
                .toList();
    }

    /**
     * 아이템 사용 후 호출
     * 빈 컵을 지급 (크리에이티브 모드일 경우 지급하지 않음)
     */
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        if (!level.isClientSide) {
            createEffectInstances(stack).forEach(entity::addEffect);

            if (entity instanceof Player player && !player.getAbilities().instabuild) {
                ItemStack drop = new ItemStack(ModItems.SHOT_CUP.get());
                if (!player.getInventory().add(drop)) {
                    player.drop(drop, false);
                }
            }
        }
        return result;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setAmplifier(int amplifier) {
        this.amplifier = amplifier;
    }

    protected ItemStack getEmptyCupItem() {
        return new ItemStack(ModItems.CUP.get());
    }
}


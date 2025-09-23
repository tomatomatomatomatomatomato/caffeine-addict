package com.caffeineaddict.caffeineaddictmode.items.drink.Coffee;

import com.caffeineaddict.caffeineaddictmode.items.drink.DrinkState;
import com.caffeineaddict.caffeineaddictmode.registry.ModItems;
import java.util.List;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * {@code Espresso}의 확장이나 Coffee의 예외적인 부분 구현
 * <p>
 * 여타는 Espresso와 동일, 섭취 시 cup 드랍
 * <p>
 * 메서드:
 * <ul>
 *     <li>{@code finishUsingItem}(Overriding)</li>
 * </ul>
 *<p>
 *
 * @author @daeGULLL
 */
public class Coffee extends Espresso {

    /**
     * 새로운 Coffee 아이템을 생성합니다.
     * <p>
     *  에스프레소의 nbt 태그를 상속 및 효과 자동 적용
     * <p>
     * @param nutrition   음식의 포만감 수치
     * @param saturation  음식의 포화도 수치
     * @param effects     적용할 효과
     * @param duration    효과 지속 시간 (초)
     * @param amplifier   기본 증폭 수치
     * @param drinkState  음료 온도 (찬/뜨거운)
     */
    public Coffee(int nutrition, float saturation, List<MobEffect> effects, int duration, int amplifier, DrinkState drinkState) {
        super(nutrition, saturation, effects, duration, amplifier, drinkState);
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
                //ItemStack drop = drinkState==DrinkState.HOT? new ItemStack(ModItems.MUG_CUP.get()) : new ItemStack(ModItems.GLASS_CUP.get());
                ItemStack drop = new ItemStack(ModItems.CUP.get());
                if (!player.getInventory().add(drop)) {
                    player.drop(drop, false);
                }
            }
        }
        return result;
    }
}

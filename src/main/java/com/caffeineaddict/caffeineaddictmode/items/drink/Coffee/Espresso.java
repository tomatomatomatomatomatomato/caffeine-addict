package com.caffeineaddict.caffeineaddictmode.items.drink.Coffee;

import com.caffeineaddict.caffeineaddictmode.items.drink.Drink;
import com.caffeineaddict.caffeineaddictmode.items.drink.DrinkState;
import com.caffeineaddict.caffeineaddictmode.registry.ModItems;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * {@code Drink}의 확장
 * <p>
 * 에스프레소 정보를 nbt 태그로 보유 및 효과 자동 적용
 * <p>
 * 메서드:
 * <ul>
 *     <li>{@code appendHoverText}(Overriding) - 아이템 호버시 정보 </li>
 *     <li>{@code withMeta} - nbt 태크 삽입</li>
 * </ul>
 *<p>
 * NBT 태그 키:
 * <ul>
 *     <li>{@code "creator"} : 제작자 이름 default: unknown</li>
 *     <li>{@code "star"} : 등급 (0 + 1~3 등) default: 0</li>
 * </ul>
 *
 * @author @moongua404
 * @editor @daeGULLL
 */
public class Espresso extends Drink {
    private static final String TAG_CREATOR = "creator";
    private static final String TAG_STAR = "star";

    /**
     * 새로운 Coffee 아이템을 생성합니다.
     *
     * @param nutrition   음식의 포만감 수치
     * @param saturation  음식의 포화도 수치
     * @param effects     적용할 효과
     * @param duration    효과 지속 시간 (초)
     * @param amplifier   기본 증폭 수치
     */
    public Espresso(int nutrition, float saturation, List<MobEffect> effects, int duration, int amplifier, DrinkState drinkState) {
        super(nutrition, saturation, effects, duration, amplifier, drinkState);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);

        CompoundTag tag = stack.getOrCreateTag();
        String creator = tag.contains(TAG_CREATOR) ? tag.getString(TAG_CREATOR) : "unknown";
        int star = tag.contains(TAG_STAR) ? tag.getInt(TAG_STAR) : 0;

        if(creator.equals("unknown") && star==0){
            tooltip.add(Component.literal("???"));
        }else{
            tooltip.remove(Component.literal("???"));
            tooltip.add(Component.literal("제작자: " + creator));
            tooltip.add(Component.literal("등급: " + "★".repeat(Math.max(0, star))));
        }
    }

    /**
     * Coffee 아이템에 제작자와 등급 정보를 추가합니다.
     * <p>
     * 만약 {@code withMeta}가 아닌 다른 방법으로 nbt 태그 부여 시 반영 안됨
     *
     * @param stack     커피 아이템 스택
     * @param creator   제작자 이름
     * @param star      등급 (예: 1 ~ 3)
     * @return NBT가 추가된 아이템 스택
     */
    public static ItemStack withMeta(ItemStack stack, String creator, int star) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString(TAG_CREATOR, creator);
        tag.putInt(TAG_STAR, star);
        return stack;
    }

    public static ItemStack inheritMeta(ItemStack stack, CompoundTag parent_tag){
        return withMeta(stack, parent_tag.getString(TAG_CREATOR), parent_tag.getInt(TAG_STAR));
    }

    /**
     * 아이템 사용 후 호출
     * 빈 컵을 지급 (크리에이티브 모드일 경우 지급하지 않음)
     */

    protected ItemStack getEmptyCupItem() {
        return new ItemStack(ModItems.SHOT_CUP.get());
    }

    @Override
    protected List<MobEffectInstance> createEffectInstances(ItemStack stack) {
        int star = getStar(stack);

        return effects.stream()
                .map(effect -> new MobEffectInstance(effect, duration * 20, star))
                .toList();
    }

    private int getStar(ItemStack stack) {
        int star = 0;

        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(TAG_STAR)) {
            star = tag.getInt(TAG_STAR);
        }
        if (star <= 1) {
            star = 1;
        }

        return star;
    }
}

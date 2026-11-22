package dev.protoskey.entity;

import lombok.Getter;

@Getter
public enum StandardOfLiving {
    VERY_HIGH("Очень высокий"),
    HIGH("Высокий"),
    ABOVE_AVERAGE("Выше среднего"),
    MEDIUM("Средний"),
    BELOW_AVERAGE("Ниже среднего"),
    LOW("Низкий"),
    VERY_LOW("Очень низкий"),
    POVERTY("За чертой бедности"),
    EXTREME_POVERTY("Крайняя нищета"),
    CRITICAL("Критический"),
    NIGHTMARE("Кошмарный");

    private final String label;

    StandardOfLiving(String label) {
        this.label = label;
    }
}

package dev.protoskey.entity;

import lombok.Getter;

@Getter
public enum Climate {
    EQUATORIAL("Экваториальный"),
    SUBEQUATORIAL("Субэкваториальный"),
    TROPICAL("Тропический"),
    TROPICAL_DESERT("Тропический пустынный"),
    SUBTROPICAL("Субтропический"),
    TEMPERATE("Умеренный"),
    SUBARCTIC("Субарктический"),
    ARCTIC("Арктический"),
    ANTARCTIC("Антарктический");

    /*
    RAIN_FOREST("Влажный тропический лес"),
    TUNDRA("Тундра"),
    POLAR_ICECAP("Полярная ледяная шапка"),
     */

    private final String label;

    Climate(String label) {
        this.label = label;
    }
}

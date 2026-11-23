package dev.protoskey.entity;

import lombok.Getter;

@Getter
public enum UserLevel {
    ADMIN("Администратор"),
    USER("Пользователь");
    private final String label;

    UserLevel(String label) {
        this.label = label;
    }
}

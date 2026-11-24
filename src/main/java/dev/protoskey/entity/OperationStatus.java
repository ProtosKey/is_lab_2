package dev.protoskey.entity;

import lombok.Getter;

@Getter
public enum OperationStatus {
    SUCCESS("Успешно"),
    FAILED("Ошибка");

    private final String label;

    OperationStatus(String label) {
        this.label = label;
    }
}

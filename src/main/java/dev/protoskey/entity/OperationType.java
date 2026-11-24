package dev.protoskey.entity;

import lombok.Getter;

@Getter
public enum OperationType {
    IMPORT("Импорт");

    private final String label;

    OperationType(String label) {
        this.label = label;
    }
}

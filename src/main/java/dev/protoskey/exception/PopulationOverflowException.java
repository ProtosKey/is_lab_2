package dev.protoskey.exception;

public class PopulationOverflowException extends Exception {
    public PopulationOverflowException() {
        super("Население города-получателя переполнило бы диапазон int");
    }
}

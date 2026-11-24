package dev.protoskey.exception;

public class ReplacementRequiredException extends Exception {
    public ReplacementRequiredException(String what) {
        super("Существуют связанные объекты — укажите замену для " + what);
    }
}

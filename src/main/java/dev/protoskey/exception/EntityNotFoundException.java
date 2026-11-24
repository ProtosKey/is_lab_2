package dev.protoskey.exception;

public class EntityNotFoundException extends Exception {
    public EntityNotFoundException(String entity, Integer id) {
        super(entity + " с id " + id + " не найден");
    }

    public EntityNotFoundException(String entity, String key) {
        super(entity + " \"" + key + "\" не найден");
    }
}

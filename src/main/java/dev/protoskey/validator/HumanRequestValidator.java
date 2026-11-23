package dev.protoskey.validator;

import dev.protoskey.exception.ValidatorException;
import dev.protoskey.model.HumanRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class HumanRequestValidator implements Validator<HumanRequest> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    public void validate(HumanRequest request) throws ValidatorException {
        if (request == null) {
            throw new ValidatorException("Объект не может быть null");
        }
        validateName(request.getName());
        validateAge(request.getAge());
        validateBirthday(request.getBirthday());
    }

    public void validateName(String name) throws ValidatorException {
        if (name == null || name.isBlank()) {
            throw new ValidatorException("Имя не может быть пустым");
        }
    }

    public void validateAge(Long age) throws ValidatorException {
        if (age == null) {
            throw new ValidatorException("Возраст не может быть пустым");
        }
        if (age <= 0) {
            throw new ValidatorException("Возраст должен быть положительным");
        }
    }

    public void validateBirthday(String birthday) throws ValidatorException {
        if (birthday == null || birthday.isBlank()) return;
        try {
            LocalDateTime dateTime = LocalDateTime.parse(birthday, FORMATTER);
            if (dateTime.isAfter(LocalDateTime.now())) {
                throw new ValidatorException("Дата рождения не может быть в будущем");
            }
        } catch (DateTimeParseException e) {
            throw new ValidatorException("Неверный формат даты рождения. Ожидается формат yyyy-MM-dd'T'HH:mm:ss");
        }
    }
}

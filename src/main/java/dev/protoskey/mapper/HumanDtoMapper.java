package dev.protoskey.mapper;

import dev.protoskey.entity.Human;
import dev.protoskey.exception.MapperException;
import dev.protoskey.model.HumanDto;
import jakarta.enterprise.context.ApplicationScoped;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

@ApplicationScoped
public class HumanDtoMapper implements Mapper<Human, HumanDto> {
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    @Override
    public HumanDto parseFrom(Human human) throws MapperException {
        if (human == null) {
            return null;
        }
        HumanDto dto = new HumanDto();
        dto.setId(human.getId());
        dto.setName(human.getName());
        dto.setAge(human.getAge());
        if (human.getBirthday() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            dto.setBirthday(sdf.format(human.getBirthday()));
        }
        return dto;
    }

    @Override
    public Human parseTo(HumanDto dto) throws MapperException {
        if (dto == null) {
            return null;
        }
        Human human = new Human();
        human.setId(dto.getId());
        human.setName(dto.getName());
        human.setAge(dto.getAge());
        if (dto.getBirthday() != null && !dto.getBirthday().isEmpty()) {
            human.setBirthday(parseBirthday(dto.getBirthday()));
        }
        return human;
    }

    private java.util.Date parseBirthday(String birthday) throws MapperException {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            return sdf.parse(birthday);
        } catch (ParseException e) {
            throw new MapperException("Недопустимый формат даты рождения: " + birthday);
        }
    }
}

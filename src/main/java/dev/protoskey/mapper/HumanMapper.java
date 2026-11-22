package dev.protoskey.mapper;

import dev.protoskey.entity.Human;
import dev.protoskey.exception.MapperException;
import dev.protoskey.model.HumanDto;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class HumanMapper implements Mapper<Human, HumanDto> {
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

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
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            try {
                human.setBirthday(sdf.parse(dto.getBirthday()));
            } catch (ParseException e) {
                throw new MapperException("Недопустимый формат даты рождения: " + dto.getBirthday());
            }
        }

        return human;
    }
}

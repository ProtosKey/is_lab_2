package dev.protoskey.mapper;

import dev.protoskey.entity.Human;
import dev.protoskey.exception.MapperException;
import dev.protoskey.model.HumanRequest;
import jakarta.enterprise.context.ApplicationScoped;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

@ApplicationScoped
public class HumanRequestMapper implements Mapper<Human, HumanRequest> {
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    @Override
    public HumanRequest parseFrom(Human human) throws MapperException {
        if (human == null) {
            return null;
        }
        HumanRequest request = new HumanRequest();
        request.setName(human.getName());
        request.setAge(human.getAge());
        if (human.getBirthday() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            request.setBirthday(sdf.format(human.getBirthday()));
        }
        return request;
    }

    @Override
    public Human parseTo(HumanRequest request) throws MapperException {
        if (request == null) {
            return null;
        }
        Human human = new Human();
        human.setName(request.getName());
        human.setAge(request.getAge());
        if (request.getBirthday() != null && !request.getBirthday().isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
                human.setBirthday(sdf.parse(request.getBirthday()));
            } catch (ParseException e) {
                throw new MapperException("Недопустимый формат даты рождения: " + request.getBirthday());
            }
        }
        return human;
    }
}

package dev.protoskey.mapper;

import dev.protoskey.entity.Human;
import dev.protoskey.exception.MapperException;
import dev.protoskey.model.HumanRef;
import jakarta.enterprise.context.ApplicationScoped;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

@ApplicationScoped
public class HumanRefMapper implements Mapper<Human, HumanRef> {
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    @Override
    public HumanRef parseFrom(Human human) throws MapperException {
        if (human == null) {
            return null;
        }
        HumanRef ref = new HumanRef();
        ref.setId(human.getId());
        ref.setName(human.getName());
        ref.setAge(human.getAge());
        if (human.getBirthday() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            ref.setBirthday(sdf.format(human.getBirthday()));
        }
        return ref;
    }

    @Override
    public Human parseTo(HumanRef ref) throws MapperException {
        if (ref == null) {
            return null;
        }
        Human human = new Human();
        human.setId(ref.getId());
        human.setName(ref.getName());
        if (ref.getAge() != null) {
            human.setAge(ref.getAge());
        }
        if (ref.getBirthday() != null && !ref.getBirthday().isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
                human.setBirthday(sdf.parse(ref.getBirthday()));
            } catch (ParseException e) {
                throw new MapperException("Недопустимый формат даты рождения: " + ref.getBirthday());
            }
        }
        return human;
    }
}

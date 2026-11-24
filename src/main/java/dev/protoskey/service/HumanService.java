package dev.protoskey.service;

import dev.protoskey.entity.City;
import dev.protoskey.entity.Human;
import dev.protoskey.entity.User;
import dev.protoskey.entity.UserLevel;
import dev.protoskey.exception.AccessDeniedException;
import dev.protoskey.exception.EntityNotFoundException;
import dev.protoskey.exception.ReplacementRequiredException;
import dev.protoskey.exception.SameObjectException;
import dev.protoskey.exception.UniqueViolationException;
import dev.protoskey.exception.ValidatorException;
import dev.protoskey.exception.MapperException;
import dev.protoskey.mapper.HumanRequestMapper;
import dev.protoskey.model.HumanRef;
import dev.protoskey.model.HumanRequest;
import dev.protoskey.repository.CityRepository;
import dev.protoskey.repository.HumanRepository;
import dev.protoskey.validator.HumanRequestValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class HumanService {
    @Inject
    private HumanRepository humanRepository;

    @Inject
    private CityRepository cityRepository;

    @Inject
    private HumanRequestValidator validator;

    @Inject
    private HumanRequestMapper mapper;

    @Inject
    private UpdateNotifier updateNotifier;

    public List<Human> findAll() {
        return humanRepository.findAll();
    }

    public List<Human> findPage(Integer cursor, int limit) {
        return humanRepository.findPage(cursor, limit);
    }

    public long count() {
        return humanRepository.count();
    }

    public int countLinkedCities(Integer id) throws EntityNotFoundException {
        find(id);
        return cityRepository.findByGovernor(id).size();
    }

    public Human find(Integer id) throws EntityNotFoundException {
        return humanRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Губернатор", id));
    }

    @Transactional(rollbackOn = Exception.class)
    public Human create(HumanRequest request, User user)
            throws ValidatorException, UniqueViolationException, MapperException {
        validator.validate(request);
        checkUnique(request.getName(), null);

        Human human = mapper.parseTo(request);
        human.setCreatedBy(user);
        Human saved = humanRepository.save(human);
        updateNotifier.notify("governors");
        return saved;
    }

    @Transactional(rollbackOn = Exception.class)
    public Human update(Integer id, HumanRequest request, User user)
            throws EntityNotFoundException, AccessDeniedException, ValidatorException, UniqueViolationException, MapperException {
        Human existing = findOwned(id, user);
        validator.validate(request);
        checkUnique(request.getName(), id);

        Human changes = mapper.parseTo(request);
        existing.setName(changes.getName());
        existing.setAge(changes.getAge());
        existing.setBirthday(changes.getBirthday());
        Human updated = humanRepository.update(existing);
        updateNotifier.notify("governors");
        return updated;
    }

    @Transactional(rollbackOn = Exception.class)
    public void delete(Integer id, User user, Integer replacementId)
            throws EntityNotFoundException, AccessDeniedException, ReplacementRequiredException, SameObjectException, UniqueViolationException {
        Human target = findOwned(id, user);
        List<City> linked = cityRepository.findByGovernor(id);

        if (!linked.isEmpty()) {
            if (replacementId == null) {
                throw new ReplacementRequiredException("губернатора");
            }
            if (replacementId.equals(id)) {
                throw new SameObjectException("Нельзя заменить объект им же самим");
            }
            Human replacement = find(replacementId);
            if (!cityRepository.findByGovernor(replacementId).isEmpty()) {
                throw new UniqueViolationException("Выбранный губернатор уже назначен другому городу");
            }
            linked.forEach(city -> city.setGovernor(replacement));
        }

        humanRepository.delete(target);
        updateNotifier.notify("governors");
        if (!linked.isEmpty()) {
            updateNotifier.notify("cities");
        }
    }

    /**
     * Разрешение ссылки из запроса города: id задан — привязка к существующему,
     * id == null — поиск по уникальному имени, иначе создание нового (владелец — user).
     * Валидация ссылки уже выполнена CityRequestValidator.
     */
    public Human resolve(HumanRef ref, User user) throws EntityNotFoundException, MapperException {
        if (ref.getId() != null) {
            return find(ref.getId());
        }
        Optional<Human> existing = humanRepository.findByName(ref.getName());
        if (existing.isPresent()) {
            return existing.get();
        }
        Human human = mapper.parseTo(toRequest(ref));
        human.setCreatedBy(user);
        return humanRepository.save(human);
    }

    private void checkUnique(String name, Integer excludedId) throws UniqueViolationException {
        Optional<Human> existing = humanRepository.findByName(name);
        if (existing.isPresent() && !existing.get().getId().equals(excludedId)) {
            throw new UniqueViolationException("Губернатор \"" + name + "\" уже существует");
        }
    }

    private Human findOwned(Integer id, User user)
            throws EntityNotFoundException, AccessDeniedException {
        Human human = find(id);
        checkModifyAccess(human.getCreatedBy(), user);
        return human;
    }

    private void checkModifyAccess(User owner, User current) throws AccessDeniedException {
        if (current.getUserLevel() != UserLevel.ADMIN && !owner.getId().equals(current.getId())) {
            throw new AccessDeniedException("Изменять можно только свои объекты");
        }
    }

    private static HumanRequest toRequest(HumanRef ref) {
        HumanRequest request = new HumanRequest();
        request.setName(ref.getName());
        request.setAge(ref.getAge());
        request.setBirthday(ref.getBirthday());
        return request;
    }
}

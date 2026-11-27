package dev.protoskey.service;

import dev.protoskey.entity.City;
import dev.protoskey.entity.Coordinates;
import dev.protoskey.entity.User;
import dev.protoskey.entity.UserLevel;
import dev.protoskey.exception.AccessDeniedException;
import dev.protoskey.exception.EntityNotFoundException;
import dev.protoskey.exception.ReplacementRequiredException;
import dev.protoskey.exception.SameObjectException;
import dev.protoskey.exception.UniqueViolationException;
import dev.protoskey.exception.ValidatorException;
import dev.protoskey.exception.MapperException;
import dev.protoskey.mapper.CoordinatesRequestMapper;
import dev.protoskey.model.CoordinatesRef;
import dev.protoskey.model.CoordinatesRequest;
import dev.protoskey.repository.CityRepository;
import dev.protoskey.repository.CoordinatesRepository;
import dev.protoskey.validator.CoordinatesRequestValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CoordinatesService {
    @Inject
    private CoordinatesRepository coordinatesRepository;

    @Inject
    private CityRepository cityRepository;

    @Inject
    private CoordinatesRequestValidator validator;

    @Inject
    private CoordinatesRequestMapper mapper;

    @Inject
    private TransactionIsolation transactionIsolation;

    @Inject
    private UpdateNotifier updateNotifier;

    public List<Coordinates> findAll() {
        return coordinatesRepository.findAll();
    }

    public List<Coordinates> findPage(Integer cursor, int limit) {
        return coordinatesRepository.findPage(cursor, limit);
    }

    public long count() {
        return coordinatesRepository.count();
    }

    public int countLinkedCities(Integer id) throws EntityNotFoundException {
        find(id);
        return cityRepository.findByCoordinates(id).size();
    }

    public Coordinates find(Integer id) throws EntityNotFoundException {
        return coordinatesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Координаты", id));
    }

    @Transactional(rollbackOn = Exception.class)
    public Coordinates create(CoordinatesRequest request, User user)
            throws ValidatorException, UniqueViolationException, MapperException {
        transactionIsolation.serializable();
        validator.validate(request);
        checkUnique(request.getX(), request.getY(), null);

        Coordinates coordinates = mapper.parseTo(request);
        coordinates.setCreatedBy(user);
        Coordinates saved = coordinatesRepository.save(coordinates);
        updateNotifier.notify("coordinates");
        return saved;
    }

    @Transactional(rollbackOn = Exception.class)
    public Coordinates update(Integer id, CoordinatesRequest request, User user)
            throws EntityNotFoundException, AccessDeniedException, ValidatorException, UniqueViolationException, MapperException {
        transactionIsolation.serializable();
        Coordinates existing = findOwned(id, user);
        validator.validate(request);
        checkUnique(request.getX(), request.getY(), id);

        existing.setX(request.getX());
        existing.setY(request.getY());
        Coordinates updated = coordinatesRepository.update(existing);
        updateNotifier.notify("coordinates");
        return updated;
    }

    @Transactional(rollbackOn = Exception.class)
    public void delete(Integer id, User user, Integer replacementId)
            throws EntityNotFoundException, AccessDeniedException, ReplacementRequiredException, SameObjectException, UniqueViolationException {
        transactionIsolation.serializable();
        Coordinates target = findOwned(id, user);
        List<City> linked = cityRepository.findByCoordinates(id);

        if (!linked.isEmpty()) {
            if (replacementId == null) {
                throw new ReplacementRequiredException("координаты");
            }
            if (replacementId.equals(id)) {
                throw new SameObjectException("Нельзя заменить объект им же самим");
            }
            Coordinates replacement = find(replacementId);
            if (!cityRepository.findByCoordinates(replacementId).isEmpty()) {
                throw new UniqueViolationException("Выбранные координаты уже привязаны к другому городу");
            }
            linked.forEach(city -> city.setCoordinates(replacement));
        }

        coordinatesRepository.delete(target);
        updateNotifier.notify("coordinates");
        if (!linked.isEmpty()) {
            updateNotifier.notify("cities");
        }
    }

    public Coordinates resolve(CoordinatesRef ref, User user) throws EntityNotFoundException, MapperException {
        if (ref.getId() != null) {
            return find(ref.getId());
        }
        Optional<Coordinates> existing = coordinatesRepository.findByCoordinates(ref.getX(), ref.getY());
        if (existing.isPresent()) {
            return existing.get();
        }
        Coordinates coordinates = mapper.parseTo(toRequest(ref));
        coordinates.setCreatedBy(user);
        return coordinatesRepository.save(coordinates);
    }

    private void checkUnique(Integer x, Integer y, Integer excludedId) throws UniqueViolationException {
        Optional<Coordinates> existing = coordinatesRepository.findByCoordinates(x, y);
        if (existing.isPresent() && !existing.get().getId().equals(excludedId)) {
            throw new UniqueViolationException("Координаты (" + x + ", " + y + ") уже существуют");
        }
    }

    private Coordinates findOwned(Integer id, User user)
            throws EntityNotFoundException, AccessDeniedException {
        Coordinates coordinates = find(id);
        checkModifyAccess(coordinates.getCreatedBy(), user);
        return coordinates;
    }

    private void checkModifyAccess(User owner, User current) throws AccessDeniedException {
        if (current.getUserLevel() != UserLevel.ADMIN && !owner.getId().equals(current.getId())) {
            throw new AccessDeniedException("Изменять можно только свои объекты");
        }
    }

    private static CoordinatesRequest toRequest(CoordinatesRef ref) {
        CoordinatesRequest request = new CoordinatesRequest();
        request.setX(ref.getX());
        request.setY(ref.getY());
        return request;
    }
}

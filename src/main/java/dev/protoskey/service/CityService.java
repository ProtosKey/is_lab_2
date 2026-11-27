package dev.protoskey.service;

import dev.protoskey.entity.City;
import dev.protoskey.entity.User;
import dev.protoskey.entity.UserLevel;
import dev.protoskey.exception.AccessDeniedException;
import dev.protoskey.exception.EntityNotFoundException;
import dev.protoskey.exception.UniqueViolationException;
import dev.protoskey.exception.ValidatorException;
import dev.protoskey.exception.MapperException;
import dev.protoskey.mapper.CityRequestMapper;
import dev.protoskey.model.CityRequest;
import dev.protoskey.repository.CityQuery;
import dev.protoskey.repository.CityRepository;
import dev.protoskey.validator.CityRequestValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class CityService {
    @Inject
    private CityRepository cityRepository;

    @Inject
    private CoordinatesService coordinatesService;

    @Inject
    private HumanService humanService;

    @Inject
    private UpdateNotifier updateNotifier;

    @Inject
    private TransactionIsolation transactionIsolation;

    @Inject
    private CityRequestValidator validator;

    @Inject
    private CityRequestMapper mapper;

    public List<City> findPage(CityQuery query) {
        return cityRepository.findPage(query);
    }

    public long count(CityQuery query) {
        return cityRepository.countAll(query);
    }

    public City find(Integer id) throws EntityNotFoundException {
        return cityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Город", id));
    }

    @Transactional(rollbackOn = Exception.class)
    public City create(CityRequest request, User user)
            throws ValidatorException, EntityNotFoundException, UniqueViolationException, MapperException {
        transactionIsolation.serializable();
        validator.validate(request);

        City city = mapper.parseTo(request);
        var coordinates = coordinatesService.resolve(request.getCoordinates(), user);
        checkCoordinatesAvailable(coordinates.getId(), null);
        var governor = humanService.resolve(request.getGovernor(), user);
        checkGovernorAvailable(governor.getId(), null);
        city.setCoordinates(coordinates);
        city.setGovernor(governor);
        city.setCreatedBy(user);
        City saved = cityRepository.save(city);
        updateNotifier.notify("cities");
        updateNotifier.notify("coordinates");
        updateNotifier.notify("governors");
        return saved;
    }

    @Transactional(rollbackOn = Exception.class)
    public City update(Integer id, CityRequest request, User user)
            throws ValidatorException, EntityNotFoundException, AccessDeniedException, UniqueViolationException, MapperException {
        transactionIsolation.serializable();
        validator.validate(request);

        City existing = find(id);
        checkModifyAccess(existing.getCreatedBy(), user);

        City changes = mapper.parseTo(request);
        existing.setName(changes.getName());
        existing.setArea(changes.getArea());
        existing.setPopulation(changes.getPopulation());
        existing.setEstablishmentDate(changes.getEstablishmentDate());
        existing.setCapital(changes.isCapital());
        existing.setMetersAboveSeaLevel(changes.getMetersAboveSeaLevel());
        existing.setPopulationDensity(changes.getPopulationDensity());
        existing.setClimate(changes.getClimate());
        existing.setStandardOfLiving(changes.getStandardOfLiving());
        var coordinates = coordinatesService.resolve(request.getCoordinates(), user);
        checkCoordinatesAvailable(coordinates.getId(), id);
        var governor = humanService.resolve(request.getGovernor(), user);
        checkGovernorAvailable(governor.getId(), id);
        existing.setCoordinates(coordinates);
        existing.setGovernor(governor);
        City updated = cityRepository.update(existing);
        updateNotifier.notify("cities");
        updateNotifier.notify("coordinates");
        updateNotifier.notify("governors");
        return updated;
    }

    @Transactional(rollbackOn = Exception.class)
    public void delete(Integer id, User user)
            throws EntityNotFoundException, AccessDeniedException {
        transactionIsolation.serializable();
        City city = find(id);
        checkModifyAccess(city.getCreatedBy(), user);
        cityRepository.delete(city);
        updateNotifier.notify("cities");
    }

    private void checkCoordinatesAvailable(Integer coordinatesId, Integer excludedCityId)
            throws UniqueViolationException {
        boolean busy = cityRepository.findByCoordinates(coordinatesId).stream()
                .anyMatch(c -> !c.getId().equals(excludedCityId));
        if (busy) {
            throw new UniqueViolationException("Координаты уже привязаны к другому городу");
        }
    }

    private void checkGovernorAvailable(Integer governorId, Integer excludedCityId)
            throws UniqueViolationException {
        boolean busy = cityRepository.findByGovernor(governorId).stream()
                .anyMatch(c -> !c.getId().equals(excludedCityId));
        if (busy) {
            throw new UniqueViolationException("Губернатор уже назначен другому городу");
        }
    }

    private void checkModifyAccess(User owner, User current) throws AccessDeniedException {
        if (current.getUserLevel() != UserLevel.ADMIN && !owner.getId().equals(current.getId())) {
            throw new AccessDeniedException("Изменять можно только свои города");
        }
    }
}

package dev.protoskey.service;

import dev.protoskey.entity.City;
import dev.protoskey.entity.Climate;
import dev.protoskey.entity.User;
import dev.protoskey.entity.UserLevel;
import dev.protoskey.exception.AccessDeniedException;
import dev.protoskey.exception.EntityNotFoundException;
import dev.protoskey.exception.NoCityToMoveException;
import dev.protoskey.exception.NoCityWithClimateException;
import dev.protoskey.exception.PopulationOverflowException;
import dev.protoskey.exception.SameObjectException;
import dev.protoskey.repository.CityRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class SpecialOperationsService {
    private final CityRepository cityRepository;

    @Inject
    public SpecialOperationsService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Transactional(rollbackOn = Exception.class)
    public City deleteOneByClimate(Climate climate, User user)
            throws AccessDeniedException, NoCityWithClimateException {
        checkAdmin(user);

        City city = cityRepository.findAnyByClimate(climate)
                .orElseThrow(() -> new NoCityWithClimateException(climate));
        cityRepository.delete(city);
        return city;
    }

    public long countByMetersAboveSeaLevel(Float metersAboveSeaLevel) {
        return cityRepository.countByMetersAboveSeaLevel(metersAboveSeaLevel);
    }

    public List<City> findByNameContains(String part) {
        return cityRepository.findByNameContains(part);
    }

    @Transactional(rollbackOn = Exception.class)
    public City movePopulation(Integer sourceId, Integer targetId, User user)
            throws AccessDeniedException, SameObjectException, EntityNotFoundException, PopulationOverflowException {
        checkAdmin(user);
        if (sourceId.equals(targetId)) {
            throw new SameObjectException("Город-источник и город-получатель должны различаться");
        }
        City source = findCity(sourceId);
        City target = findCity(targetId);
        return move(source, target);
    }

    @Transactional(rollbackOn = Exception.class)
    public City movePopulationToSmallest(Integer sourceId, User user)
            throws AccessDeniedException, EntityNotFoundException, NoCityToMoveException, PopulationOverflowException {
        checkAdmin(user);

        City source = findCity(sourceId);
        City target = cityRepository.findWithSmallestPopulation()
                .orElseThrow(() -> new NoCityToMoveException("В системе нет городов"));
        if (target.getId().equals(sourceId)) {
            throw new NoCityToMoveException("Город-источник сам является наименее населённым");
        }
        return move(source, target);
    }

    private City move(City source, City target) throws PopulationOverflowException {
        int moved;
        try {
            moved = Math.addExact(target.getPopulation(), source.getPopulation());
        } catch (ArithmeticException e) {
            throw new PopulationOverflowException();
        }

        target.setPopulation(moved);
        cityRepository.update(target);
        cityRepository.delete(source);
        return target;
    }

    private City findCity(Integer id) throws EntityNotFoundException {
        return cityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Город", id));
    }

    private void checkAdmin(User user) throws AccessDeniedException {
        if (user.getUserLevel() != UserLevel.ADMIN) {
            throw new AccessDeniedException("Операция доступна только администратору");
        }
    }
}

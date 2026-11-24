package dev.protoskey.service;

import dev.protoskey.entity.City;
import dev.protoskey.entity.Coordinates;
import dev.protoskey.entity.Human;
import dev.protoskey.entity.OperationType;
import dev.protoskey.entity.User;
import dev.protoskey.exception.EntityNotFoundException;
import dev.protoskey.exception.UniqueViolationException;
import dev.protoskey.exception.ValidatorException;
import dev.protoskey.exception.MapperException;
import dev.protoskey.mapper.CoordinatesRequestMapper;
import dev.protoskey.mapper.HumanRequestMapper;
import dev.protoskey.mapper.ImportCityDtoMapper;
import dev.protoskey.model.ImportBatchDto;
import dev.protoskey.model.ImportCityDto;
import dev.protoskey.model.CoordinatesRef;
import dev.protoskey.model.CoordinatesRequest;
import dev.protoskey.model.HumanRef;
import dev.protoskey.model.HumanRequest;
import dev.protoskey.repository.CityRepository;
import dev.protoskey.repository.CoordinatesRepository;
import dev.protoskey.repository.HumanRepository;
import dev.protoskey.validator.ImportBatchDtoValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ImportService {
    @Inject
    private ImportBatchDtoValidator validator;

    @Inject
    private CoordinatesRequestMapper coordinatesMapper;

    @Inject
    private HumanRequestMapper humanMapper;

    @Inject
    private ImportCityDtoMapper cityMapper;

    @Inject
    private CoordinatesRepository coordinatesRepository;

    @Inject
    private HumanRepository humanRepository;

    @Inject
    private CityRepository cityRepository;

    @Inject
    private OperationService operationService;

    @Transactional(rollbackOn = Exception.class)
    public int importBatch(ImportBatchDto batch, User user, String fileName)
            throws ValidatorException, EntityNotFoundException, UniqueViolationException, MapperException {
        validator.validate(batch);

        int created = 0;
        created += importCoordinates(batch.getCoordinates(), user);
        created += importGovernors(batch.getGovernors(), user);
        created += importCities(batch.getCities(), user);

        operationService.recordSuccess(user, OperationType.IMPORT, created, fileName);
        return created;
    }

    private int importCoordinates(List<CoordinatesRequest> coordinates, User user)
            throws UniqueViolationException, MapperException {
        if (coordinates == null) {
            return 0;
        }
        int created = 0;
        for (CoordinatesRequest request : coordinates) {
            Optional<Coordinates> existing =
                    coordinatesRepository.findByCoordinates(request.getX(), request.getY());
            if (existing.isPresent()) {
                throw new UniqueViolationException(
                        "Координаты (" + request.getX() + ", " + request.getY() + ") уже существуют");
            }
            Coordinates entity = coordinatesMapper.parseTo(request);
            entity.setCreatedBy(user);
            coordinatesRepository.save(entity);
            created++;
        }
        return created;
    }

    private int importGovernors(List<HumanRequest> governors, User user)
            throws UniqueViolationException, MapperException {
        if (governors == null) {
            return 0;
        }
        int created = 0;
        for (HumanRequest request : governors) {
            Optional<Human> existing = humanRepository.findByName(request.getName());
            if (existing.isPresent()) {
                throw new UniqueViolationException("Губернатор \"" + request.getName() + "\" уже существует");
            }
            Human entity = humanMapper.parseTo(request);
            entity.setCreatedBy(user);
            humanRepository.save(entity);
            created++;
        }
        return created;
    }

    private int importCities(List<ImportCityDto> cities, User user)
            throws EntityNotFoundException, UniqueViolationException, MapperException {
        if (cities == null) {
            return 0;
        }
        int created = 0;
        for (ImportCityDto dto : cities) {
            City city = cityMapper.parseTo(dto);
            city.setCoordinates(linkOrImportCoordinates(dto.getCoordinates(), user));
            city.setGovernor(linkOrImportGovernor(dto.getGovernor(), user));
            city.setCreatedBy(user);
            cityRepository.save(city);
            created++;
        }
        return created;
    }

    private Coordinates linkOrImportCoordinates(CoordinatesRef ref, User user)
            throws EntityNotFoundException, UniqueViolationException, MapperException {
        if (ref.getId() != null) {
            return coordinatesRepository.findById(ref.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Координаты", ref.getId()));
        }
        Optional<Coordinates> existing =
                coordinatesRepository.findByCoordinates(ref.getX(), ref.getY());
        if (existing.isPresent()) {
            throw new UniqueViolationException(
                    "Координаты (" + ref.getX() + ", " + ref.getY() + ") уже существуют");
        }
        Coordinates coordinates = coordinatesMapper.parseTo(toRequest(ref));
        coordinates.setCreatedBy(user);
        coordinatesRepository.save(coordinates);
        return coordinates;
    }

    private Human linkOrImportGovernor(HumanRef ref, User user)
            throws EntityNotFoundException, UniqueViolationException, MapperException {
        if (ref.getId() != null) {
            return humanRepository.findById(ref.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Губернатор", ref.getId()));
        }
        Optional<Human> existing = humanRepository.findByName(ref.getName());
        if (existing.isPresent()) {
            throw new UniqueViolationException("Губернатор \"" + ref.getName() + "\" уже существует");
        }
        Human human = humanMapper.parseTo(toRequest(ref));
        human.setCreatedBy(user);
        humanRepository.save(human);
        return human;
    }

    private static CoordinatesRequest toRequest(CoordinatesRef ref) {
        CoordinatesRequest request = new CoordinatesRequest();
        request.setX(ref.getX());
        request.setY(ref.getY());
        return request;
    }

    private static HumanRequest toRequest(HumanRef ref) {
        HumanRequest request = new HumanRequest();
        request.setName(ref.getName());
        request.setAge(ref.getAge());
        request.setBirthday(ref.getBirthday());
        return request;
    }
}

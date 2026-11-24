package dev.protoskey.repository;

import dev.protoskey.entity.City;
import dev.protoskey.entity.Climate;
import dev.protoskey.entity.StandardOfLiving;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CityRepository implements Repository<City> {
    @PersistenceContext(unitName = "is_lab_2PU")
    private EntityManager entityManager;

    public Optional<City> findById(Integer id) {
        return Optional.ofNullable(entityManager.find(City.class, id));
    }

    public List<City> findAll() {
        return entityManager.createQuery("select c from City c order by c.id", City.class).getResultList();
    }

    @SuppressWarnings("SqlSourceToSinkFlow")
    public List<City> findPage(CityQuery query) {
        String jpql = "select c from City c" + whereClause(query) + orderClause(query);
        TypedQuery<City> typedQuery = entityManager.createQuery(jpql, City.class);
        applyFilter(typedQuery, query);
        return typedQuery
                .setFirstResult(query.offset())
                .setMaxResults(query.limit())
                .getResultList();
    }

    @SuppressWarnings("SqlSourceToSinkFlow")
    public long countAll(CityQuery query) {
        String jpql = "select count(c) from City c" + whereClause(query);
        TypedQuery<Long> typedQuery = entityManager.createQuery(jpql, Long.class);
        applyFilter(typedQuery, query);
        return typedQuery.getSingleResult();
    }

    public List<City> findByNameContains(String part) {
        return entityManager.createQuery(
                        "select c from City c where lower(c.name) like :part order by c.id", City.class)
                .setParameter("part", "%" + part.toLowerCase() + "%")
                .getResultList();
    }

    public long countByMetersAboveSeaLevel(Float value) {
        return entityManager.createQuery(
                        "select count(c) from City c where c.metersAboveSeaLevel = :value", Long.class)
                .setParameter("value", value)
                .getSingleResult();
    }

    public Optional<City> findAnyByClimate(Climate climate) {
        return entityManager.createQuery("select c from City c where c.climate = :climate", City.class)
                .setParameter("climate", climate)
                .setMaxResults(1)
                .getResultList()
                .stream()
                .findFirst();
    }

    public Optional<City> findWithSmallestPopulation() {
        return entityManager.createQuery(
                        "select c from City c order by c.population asc, c.id asc", City.class)
                .setMaxResults(1)
                .getResultList()
                .stream()
                .findFirst();
    }

    public List<City> findByCoordinates(Integer coordinatesId) {
        return entityManager.createQuery("select c from City c where c.coordinates.id = :id", City.class)
                .setParameter("id", coordinatesId)
                .getResultList();
    }

    public List<City> findByGovernor(Integer governorId) {
        return entityManager.createQuery("select c from City c where c.governor.id = :id", City.class)
                .setParameter("id", governorId)
                .getResultList();
    }

    public City save(City city) {
        entityManager.persist(city);
        return city;
    }

    public City update(City city) {
        return entityManager.merge(city);
    }

    public void delete(City city) {
        entityManager.remove(entityManager.contains(city) ? city : entityManager.merge(city));
    }

    private String whereClause(CityQuery query) {
        if (hasNotFilter(query)) {
            return "";
        }
        return switch (query.filterField()) {
            case "name" -> " where lower(c.name) like :filter";
            case "governor" -> " where lower(c.governor.name) like :filter";
            case "climate", "standardOfLiving" -> matchingEnums(query).isEmpty()
                    ? " where 1 = 0"
                    : " where c." + query.filterField() + " in :filter";
            default -> "";
        };
    }

    private String orderClause(CityQuery query) {
        String path = switch (query.sortField() == null ? "" : query.sortField()) {
            case "name" -> "c.name";
            case "coordinatesX" -> "c.coordinates.x";
            case "coordinatesY" -> "c.coordinates.y";
            case "creationDate" -> "c.creationDate";
            case "area" -> "c.area";
            case "population" -> "c.population";
            case "establishmentDate" -> "c.establishmentDate";
            case "capital" -> "c.capital";
            case "metersAboveSeaLevel" -> "c.metersAboveSeaLevel";
            case "populationDensity" -> "c.populationDensity";
            case "climate" -> "c.climate";
            case "standardOfLiving" -> "c.standardOfLiving";
            case "governor" -> "c.governor.name";
            default -> "c.id";
        };
        return " order by " + path + (query.ascending() ? " asc" : " desc");
    }

    private void applyFilter(TypedQuery<?> typedQuery, CityQuery query) {
        if (hasNotFilter(query)) {
            return;
        }
        switch (query.filterField()) {
            case "name", "governor" ->
                    typedQuery.setParameter("filter", "%" + query.filterValue().toLowerCase() + "%");
            case "climate", "standardOfLiving" -> {
                List<? extends Enum<?>> values = matchingEnums(query);
                if (!values.isEmpty()) {
                    typedQuery.setParameter("filter", values);
                }
            }
            default -> { }
        }
    }

    private boolean hasNotFilter(CityQuery query) {
        return query.filterField() == null
                || query.filterValue() == null
                || query.filterValue().isBlank();
    }

    private List<? extends Enum<?>> matchingEnums(CityQuery query) {
        Enum<?>[] values = "climate".equals(query.filterField())
                ? Climate.values()
                : StandardOfLiving.values();
        String part = query.filterValue().toLowerCase();
        return Arrays.stream(values)
                .filter(value -> value.name().toLowerCase().contains(part))
                .toList();
    }
}

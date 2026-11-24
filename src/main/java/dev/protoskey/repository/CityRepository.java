package dev.protoskey.repository;

import dev.protoskey.entity.City;
import dev.protoskey.entity.Climate;
import dev.protoskey.entity.StandardOfLiving;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.ArrayList;
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
        City anchor = query.cursor() != null ? entityManager.find(City.class, query.cursor()) : null;
        String jpql = "select c from City c" + buildWhere(query, anchor) + orderClause(query);
        TypedQuery<City> typedQuery = entityManager.createQuery(jpql, City.class);
        applyFilter(typedQuery, query);
        applyCursor(typedQuery, query, anchor);
        return typedQuery
                .setMaxResults(Math.max(1, query.limit()) + 1)
                .getResultList();
    }

    @SuppressWarnings("SqlSourceToSinkFlow")
    public long countAll(CityQuery query) {
        String jpql = "select count(c) from City c" + filterWhere(query);
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

    private String buildWhere(CityQuery query, City anchor) {
        List<String> parts = new ArrayList<>();
        String filter = filterPredicate(query);
        if (!filter.isEmpty()) {
            parts.add(filter);
        }
        String cursor = cursorPredicate(query, anchor);
        if (!cursor.isEmpty()) {
            parts.add(cursor);
        }
        return parts.isEmpty() ? "" : " where " + String.join(" and ", parts);
    }

    private String filterWhere(CityQuery query) {
        String filter = filterPredicate(query);
        return filter.isEmpty() ? "" : " where " + filter;
    }

    private String filterPredicate(CityQuery query) {
        if (hasNotFilter(query)) {
            return "";
        }
        return switch (query.filterField()) {
            case "name" -> "lower(c.name) like :filter";
            case "governor" -> "lower(c.governor.name) like :filter";
            case "climate", "standardOfLiving" -> matchingEnums(query).isEmpty()
                    ? "1 = 0"
                    : "c." + query.filterField() + " in :filter";
            default -> "";
        };
    }

    private String sortPath(CityQuery query) {
        return switch (query.sortField() == null ? "" : query.sortField()) {
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
    }

    private Object sortValue(City city, CityQuery query) {
        return switch (query.sortField() == null ? "" : query.sortField()) {
            case "name" -> city.getName();
            case "coordinatesX" -> city.getCoordinates().getX();
            case "coordinatesY" -> city.getCoordinates().getY();
            case "creationDate" -> city.getCreationDate();
            case "area" -> city.getArea();
            case "population" -> city.getPopulation();
            case "establishmentDate" -> city.getEstablishmentDate();
            case "capital" -> city.isCapital();
            case "metersAboveSeaLevel" -> city.getMetersAboveSeaLevel();
            case "populationDensity" -> city.getPopulationDensity();
            case "climate" -> city.getClimate();
            case "standardOfLiving" -> city.getStandardOfLiving();
            case "governor" -> city.getGovernor().getName();
            default -> city.getId();
        };
    }

    private String cursorPredicate(CityQuery query, City anchor) {
        if (anchor == null) {
            return "";
        }
        String op = query.ascending() ? ">" : "<";
        String path = sortPath(query);
        if ("c.id".equals(path)) {
            return "c.id " + op + " :cursorId";
        }
        Object val = sortValue(anchor, query);
        if (val == null) {
            return "(" + path + " is null and c.id " + op + " :cursorId)";
        }
        if (query.ascending()) {
            return "(" + path + " > :cursorVal or (" + path + " = :cursorVal and c.id > :cursorId) or " + path + " is null)";
        }
        return "(" + path + " < :cursorVal or (" + path + " = :cursorVal and c.id < :cursorId))";
    }

    private void applyCursor(TypedQuery<?> typedQuery, CityQuery query, City anchor) {
        if (anchor == null) {
            return;
        }
        typedQuery.setParameter("cursorId", anchor.getId());
        String path = sortPath(query);
        if (!"c.id".equals(path)) {
            Object val = sortValue(anchor, query);
            if (val != null) {
                typedQuery.setParameter("cursorVal", val);
            }
        }
    }

    private String orderClause(CityQuery query) {
        String path = sortPath(query);
        String dir = query.ascending() ? " asc" : " desc";
        if ("c.id".equals(path)) {
            return " order by c.id" + dir;
        }
        return " order by " + path + dir + ", c.id" + dir;
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

package dev.protoskey.repository;

public record CityQuery(String filterField,
                        String filterValue,
                        String sortField,
                        boolean ascending,
                        Integer cursor,
                        int limit) {
}

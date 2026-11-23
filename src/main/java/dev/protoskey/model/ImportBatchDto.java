package dev.protoskey.model;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class ImportBatchDto {
    private List<CoordinatesRequest> coordinates;
    private List<HumanRequest> governors;
    private List<ImportCityDto> cities;
}

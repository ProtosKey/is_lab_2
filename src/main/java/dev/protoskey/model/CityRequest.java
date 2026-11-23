package dev.protoskey.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CityRequest {
    private String name;
    private CoordinatesRef coordinates;
    private String creationDate;
    private Integer area;
    private Integer population;
    private String establishmentDate;
    private Boolean capital;
    private Float metersAboveSeaLevel;
    private Float populationDensity;
    private String climate;
    private String standardOfLiving;
    private HumanRef governor;
}

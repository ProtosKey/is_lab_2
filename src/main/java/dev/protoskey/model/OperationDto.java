package dev.protoskey.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OperationDto {
    private Integer id;
    private String type;
    private String status;
    private Integer objectCount;
    private String message;
    private String fileName;
    private String creationDate;
    private String userLogin;
}

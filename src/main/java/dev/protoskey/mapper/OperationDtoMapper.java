package dev.protoskey.mapper;

import dev.protoskey.entity.Operation;
import dev.protoskey.exception.MapperException;
import dev.protoskey.model.OperationDto;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class OperationDtoMapper implements Mapper<Operation, OperationDto> {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public OperationDto parseFrom(Operation operation) throws MapperException {
        if (operation == null) {
            return null;
        }
        OperationDto dto = new OperationDto();
        dto.setId(operation.getId());
        dto.setType(operation.getType().name());
        dto.setStatus(operation.getStatus().name());
        dto.setObjectCount(operation.getObjectCount());
        dto.setMessage(operation.getMessage());
        dto.setFileName(operation.getFileName());
        if (operation.getCreatedAt() != null) {
            dto.setCreationDate(operation.getCreatedAt().format(DATE_FORMATTER));
        }
        dto.setUserLogin(operation.getUser().getLogin());
        return dto;
    }

    @Override
    public Operation parseTo(OperationDto dto) throws MapperException {
        throw new MapperException("Операции создаются только сервисом импорта");
    }
}

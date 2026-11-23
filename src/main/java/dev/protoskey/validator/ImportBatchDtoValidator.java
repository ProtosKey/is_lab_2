package dev.protoskey.validator;

import dev.protoskey.exception.ValidatorException;
import dev.protoskey.model.ImportBatchDto;
import java.util.List;

public class ImportBatchDtoValidator implements Validator<ImportBatchDto> {
    private final CoordinatesRequestValidator coordinatesValidator = new CoordinatesRequestValidator();
    private final HumanRequestValidator humanValidator = new HumanRequestValidator();
    private final ImportCityDtoValidator cityValidator = new ImportCityDtoValidator();

    @Override
    public void validate(ImportBatchDto batch) throws ValidatorException {
        if (batch == null) {
            throw new ValidatorException("Файл импорта не может быть пустым");
        }
        validateSection("coordinates", batch.getCoordinates(), coordinatesValidator);
        validateSection("governors", batch.getGovernors(), humanValidator);
        validateSection("cities", batch.getCities(), cityValidator);
    }

    private <T> void validateSection(String sectionName, List<T> records, Validator<T> validator) throws ValidatorException {
        if (records == null) {
            return;
        }
        for (int i = 0; i < records.size(); i++) {
            try {
                validator.validate(records.get(i));
            } catch (ValidatorException e) {
                throw new ValidatorException("Секция \"" + sectionName + "\", запись №" + (i + 1) + ": " + e.getMessage());
            }
        }
    }
}

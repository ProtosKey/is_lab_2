package dev.protoskey.validator;

import dev.protoskey.exception.ValidatorException;
import dev.protoskey.model.ImportBatchDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class ImportBatchDtoValidator implements Validator<ImportBatchDto> {
    @Inject
    private CoordinatesRequestValidator coordinatesValidator;
    @Inject
    private HumanRequestValidator humanValidator;
    @Inject
    private ImportCityDtoValidator cityValidator;

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

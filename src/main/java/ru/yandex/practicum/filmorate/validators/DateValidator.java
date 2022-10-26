package ru.yandex.practicum.filmorate.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateValidator implements ConstraintValidator<IsAfter, LocalDate> {    // класс для проверки даты
    private final DateTimeFormatter formatOfDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private LocalDate minDate;

    @Override
    public void initialize(IsAfter constraintAnnotation) {
        String str = constraintAnnotation.current();
        minDate = LocalDate.parse(str, formatOfDate);
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        return localDate.isAfter(minDate);
    }
}

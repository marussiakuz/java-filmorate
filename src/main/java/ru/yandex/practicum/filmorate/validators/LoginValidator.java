package ru.yandex.practicum.filmorate.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LoginValidator implements ConstraintValidator<DoesNotContain, String> {    // проверяет нет ли в логине лишнего символа
    private String charSequence;

    @Override
    public void initialize(DoesNotContain constraintAnnotation) {
        charSequence = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(String login, ConstraintValidatorContext constraintValidatorContext) {
        return !login.contains(charSequence);
    }
}

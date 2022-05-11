package ru.yandex.practicum.filmorate.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(ElementType.FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = LoginValidator.class)
@Documented
public @interface DoesNotContain {    // класс для проверки на отсутствие содержания в строке подстроки
    String message() default "Login may not contain a space";
    String value();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.format.annotation.DateTimeFormat;
import javax.validation.constraints.*;

import ru.yandex.practicum.filmorate.validators.IsAfter;

import java.time.Duration;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Film {

    private int id;

    @NotNull(message = "Name may not be null")
    @NotBlank(message = "Name may not be blank")
    private String name;

    @NotNull(message = "Description may not be null")
    @NotBlank(message = "Description may not be blank")
    @Size(min = 1, max = 200, message = "Description must be between 1 and 200 characters long")
    private String description;

    @IsAfter(current = "1895-12-28", message = "Release date may not be earlier than 28.12.1895")
    private LocalDate releaseDate;

    @DurationMin(minutes = 1, message = "Duration must be higher or equal to 1 minute")
    private Duration duration;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    @JsonFormat(pattern = "MINUTES")
    public Duration getDuration() {
        return duration;
    }
}

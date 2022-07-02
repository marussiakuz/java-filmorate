package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.format.annotation.DateTimeFormat;
import javax.validation.constraints.*;

import ru.yandex.practicum.filmorate.model.serializers.DurationSerializer;
import ru.yandex.practicum.filmorate.validators.IsAfter;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.*;

@ToString
@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Film {

    private Integer id;

    @JsonIgnore
    private final Set<Integer> likes = new TreeSet<>();

    @NotNull(message = "Title may not be null")
    @NotBlank(message = "Title may not be blank")
    private String name;

    @NotNull(message = "Description may not be null")
    @NotBlank(message = "Description may not be blank")
    @Size(min = 1, max = 200, message = "Description must be between 1 and 200 characters long")
    private String description;

    @IsAfter(current = "1895-12-28", message = "Release date may not be earlier than 28.12.1895")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate releaseDate;

    @DurationMin(minutes = 1, message = "Duration must be higher or equal to 1 minute")
    @JsonFormat(shape = NUMBER_INT, pattern = "MINUTES")
    @JsonSerialize(using = DurationSerializer.class)
    private Duration duration;

    private Rating mpa;
    private List<Genre> genres;
    private List<Director> directors;


    public void addLike(Integer userId){
        likes.add(userId);
    }

    public void deleteLike(Integer userId){
        likes.remove(userId);
    }

    public int getCountOfLikes() {
        return likes.size();
    }
}

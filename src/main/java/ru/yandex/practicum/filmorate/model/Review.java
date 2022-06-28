package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
@Setter
@Builder
public class Review {

    private Integer id;

    @NotNull(message = "Content may not be null")
    private String content;
    private boolean isPositive;
    @Positive
    private Integer userId;
    @Positive
    private Integer filmId;
    //private int useful;

    @JsonProperty("reviewId")
    public Integer getId() {
        return id;
    }
}

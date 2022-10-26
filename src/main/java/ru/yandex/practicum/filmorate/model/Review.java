package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
@Setter
@Builder
public class Review {
    private int id;

    @NotNull(message = "Content may not be null")
    private String content;

    @NotNull
    private Boolean isPositive;

    private int userId;
    private int filmId;
    private int useful;

    @JsonProperty("reviewId")
    public Integer getId() {
        return id;
    }

    @JsonProperty("isPositive")
    public boolean isPositive() {
        return isPositive;
    }

    public void setPositive(boolean isPositive) {
        this.isPositive = isPositive;
    }
}

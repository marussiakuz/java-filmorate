package ru.yandex.practicum.filmorate.model;

import lombok.*;

@ToString
@EqualsAndHashCode
@Getter
@Setter
@Builder
public class Genre {
    private int id;
    private String name;
}

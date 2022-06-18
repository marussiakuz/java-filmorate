package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.validators.DoesNotContain;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ToString
@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    private Integer id;
    private String name;
    private final Map<Integer, Boolean> friends = new HashMap<>();

    @NotNull(message = "Email may not be null")
    @NotBlank(message = "Email may not be blank")
    @Email(message = "The email is incorrect")
    private String email;

    @NotNull(message = "Login may not be null")
    @NotBlank(message = "Login may not be blank")
    @DoesNotContain(value = " ", message = "Login must not contain a space")
    private String login;

    @Past(message = "Birthday may not be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthday;

    public void addFriend(Integer friendId) {
        friends.put(friendId, false);
    }

    public void deleteFriend(Integer friendId) {
        friends.remove(friendId);
    }

    public Set<Integer> getFriends() {
        return friends.keySet();
    }
}

package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@Slf4j
public class UserController {

    private final HashMap<Integer, User> users = new HashMap<>();

    @GetMapping("/users")
    public List<User> getUsers() {    // возвращает список всех пользователей на GET запрос
        return new ArrayList<>(users.values());
    }

    @PostMapping(value = "/users")
    public User addUser(@RequestBody User user) {    // добавляет нового пользователя в ответ на POST запрос
        try {
            checkTheUserDataForCorrectness(user);
        } catch (IncorrectUserDataException e) {
            log.debug("adding a user failed with the following error: {}", e.getMessage());
            throw new IncorrectUserDataException(e.getMessage());
        }
        if (user.getName() == null || user.getName().isBlank()) user.setName(user.getLogin());
        log.debug("new user added successfully");
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping(value = "/users")
    public void updateUser(@RequestBody User user) {    // обновляет данные пользователя в ответ на PUT запрос
        try {
            checkTheUserDataForCorrectness(user);
        } catch (IncorrectUserDataException e) {
            log.debug("updating a user failed with the following error: {}", e.getMessage());
            throw new IncorrectUserDataException(e.getMessage());
        }
        if (user.getName() == null || user.getName().isBlank()) user.setName(user.getLogin());
        log.debug("user data has been successfully updated");
        users.put(user.getId(), user);
    }

    private void checkTheUserDataForCorrectness (User user) {    // проверяет соответствие данных пользователя правилам
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new InvalidEmailException("email was entered incorrectly");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new InvalidLoginException("the login was entered incorrectly");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new BirthdayInTheFutureException("birthday can't be in the future");
        }
    }

    @ExceptionHandler(IncorrectUserDataException.class)    // ловит исключения и отправляет код 400
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handle(IncorrectUserDataException e) {
        return new ResponseEntity<>("some user data is incorrect: " + e.getMessage(),
                HttpStatus.BAD_REQUEST);
    }

    public User getUser(int id) {    // возвращает пользователя из списка по id
        return users.get(id);
    }
}

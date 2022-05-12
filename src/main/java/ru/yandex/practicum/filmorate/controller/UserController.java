package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserManagerService;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class UserController extends AbstractController<User>{
    private final UserManagerService userManagerService;

    @Autowired
    public UserController(UserManagerService userManagerService) {
        this.userManagerService = userManagerService;
    }

    @GetMapping("/users")
    public List<User> get() {    // возвращает список всех пользователей на GET запрос
        return userManagerService.get();
    }

    @PostMapping(value = "/users")
    public User add(@Valid @RequestBody User user) {    // добавляет нового пользователя в ответ на POST запрос
        userManagerService.add(checkName(user));
        log.debug("new user added successfully");
        return user;
    }

    @PutMapping(value = "/users")
    public void update(@Valid @RequestBody User user) {    // обновляет данные пользователя в ответ на PUT запрос
        userManagerService.add(checkName(user));
        log.debug("user data has been successfully updated");
    }

    private User checkName(User user) {    // проверяет -> name == null и пустое ли, и если да присваивает логин
        if (user.getName() == null || user.getName().isBlank()) user.setName(user.getLogin());
        return user;
    }

    @Override
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handle(MethodArgumentNotValidException e) {
        Optional<FieldError> fieldError = Optional.ofNullable(e.getFieldError());
        String message = fieldError.isPresent()? fieldError.get().getDefaultMessage() : "unknown error";
        log.debug("User validation failed: " + message);
        return new ResponseEntity<>("Some user data is incorrect: " + message,
                HttpStatus.BAD_REQUEST);
    }
}

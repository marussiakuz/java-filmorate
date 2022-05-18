package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@RestController
@Slf4j
public class UserController extends AbstractController<User>{
    private final UserStorage userStorage;
    private final UserService userService;

    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> getAll() {    // возвращает список всех пользователей на GET запрос
        return userStorage.getAllUsers();
    }

    @PostMapping(value = "/users")
    public User add(@Valid @RequestBody User user) {    // добавляет нового пользователя в ответ на POST запрос
        userStorage.add(user);
        log.debug("new user added successfully");
        return user;
    }

    @PutMapping(value = "/users")
    public User update(@Valid @RequestBody User user) {    // обновляет данные пользователя в ответ на PUT запрос
        userStorage.add(user);
        log.debug("user data has been successfully updated");
        return user;
    }

    @GetMapping(value = "/users/{id}")
    public User getById(@PathVariable Integer id) {
        return userStorage.getUserById(id);
    }

    @PutMapping(value = "/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable(value = "id") Integer userId, @PathVariable Integer friendId) {
        userService.addFriend(userId, friendId);
        log.debug("friend has been successfully added");
    }

    @DeleteMapping(value = "/users/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable(value = "id") Integer userId, @PathVariable Integer friendId) {
        userService.deleteFriend(userId, friendId);
        log.debug("friend has been successfully deleted");
    }

    @GetMapping(value = "/users/{id}/friends")
    public List<User> getAllFriends(@PathVariable(value = "id") Integer userId) {
        return userService.getAllFriends(userId);
    }

    @GetMapping(value = "/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable(value = "id") Integer userId, @PathVariable Integer otherId) {
        return userService.getCommonFriends(userId, otherId);
    }
}

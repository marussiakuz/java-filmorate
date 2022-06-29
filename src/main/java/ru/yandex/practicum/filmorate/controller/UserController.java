package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController extends AbstractController<User>{
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAll() {    // возвращает список всех пользователей на GET запрос
        return userService.getAllUsers();
    }

    @PostMapping
    public User add(@Valid @RequestBody User user) {    // добавляет нового пользователя в ответ на POST запрос
        return userService.add(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {    // обновляет данные пользователя в ответ на PUT запрос
        return userService.update(user);
    }

    @GetMapping(value = "/{id}")
    public User getById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @PutMapping(value = "/{id}/friends/{friendId}")
    public void addFriend(@PathVariable(value = "id") Integer userId, @PathVariable Integer friendId) {
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable(value = "id") Integer userId, @PathVariable Integer friendId) {
        userService.deleteFriend(userId, friendId);
    }

    @GetMapping(value = "/{id}/friends")
    public List<User> getAllFriends(@PathVariable(value = "id") Integer userId) {
        return userService.getAllFriends(userId);
    }

    @GetMapping(value = "/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable(value = "id") Integer userId, @PathVariable Integer otherId) {
        return userService.getCommonFriends(userId, otherId);
    }
    @GetMapping(value = "/{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable(value = "id") Integer userId) {
        return userService.getRecommendations(userId);
    }
    @GetMapping(value = "/{id}/feed")
    public List<Event> getEvents(@PathVariable(value = "id") Integer userId) {
        return userService.getEvents(userId);
    }

}

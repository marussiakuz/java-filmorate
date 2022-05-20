package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User add(User user) {
        if (userStorage.doesUserExist(user.getId()))
            throw new UserAlreadyExistException(String.format("User with id=%s already exists", user.getId()));
        checkName(user);
        userStorage.add(user);
        log.debug("new user added successfully");
        return user;
    }

    public User update(User user) {
        validate(user.getId());
        checkName(user);
        userStorage.update(user);
        log.debug(String.format("user data with id=%s has been successfully updated", user.getId()));
        return user;
    }

    public User getUserById(int userId) {
        validate(userId);
        return userStorage.getUserById(userId);
    }

    public void addFriend(Integer userId, Integer friendId) {
        validate(userId);
        validate(friendId);
        userStorage.getUserById(userId).addFriend(friendId);
        userStorage.getUserById(friendId).addFriend(userId);
        log.debug(String.format("The user with id=%s has added the user with id=%s to friends", userId, friendId));
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        validate(userId);
        validate(friendId);
        userStorage.getUserById(userId).deleteFriend(friendId);
        userStorage.getUserById(friendId).deleteFriend(userId);
        log.debug(String.format("The user with id=%s has removed the user with id=%s from friends", userId, friendId));
    }

    public List<User> getAllFriends(Integer userId) {
        validate(userId);
        User user = userStorage.getUserById(userId);
        return user.getFriends().stream().map(userStorage::getUserById).collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Integer userId, Integer otherUserId) {
        validate(userId);
        validate(otherUserId);
        User user = userStorage.getUserById(userId);
        User otherUser = userStorage.getUserById(otherUserId);
        List<Integer> common = new ArrayList<>(user.getFriends());
        common.retainAll(otherUser.getFriends());
        return common.stream().map(userStorage::getUserById).collect(Collectors.toList());
    }

    private void checkName(User user) {    // проверяет -> name == null и пустое ли, и если да присваивает логин
        if (user.getName() == null || user.getName().isBlank()) user.setName(user.getLogin());
    }

    private void validate(int userId) {
        if (!userStorage.doesUserExist(userId))
            throw new UserNotFoundException(String.format("User with id=%s not found", userId));
    }
}

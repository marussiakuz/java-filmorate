package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserInMemoryService implements UserService {
    private final UserStorage userStorage;

    public UserInMemoryService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User add(User user) {
        userStorage.add(user);
        log.debug("new user added successfully");
        return user;
    }

    public User update(User user) {
        userStorage.update(user);
        log.debug(String.format("user data with id=%s has been successfully updated", user.getId()));
        return user;
    }
    public Optional<User> getUserById(Integer userId) {
        return userStorage.getUserById(userId);
    }

    public void addFriend(Integer userId, Integer friendId) {
        userStorage.addFriend(userId, friendId);
        log.debug(String.format("The user with id=%s has added the user with id=%s to friends", userId, friendId));
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        userStorage.deleteFriend(userId, friendId);
        log.debug(String.format("The user with id=%s has removed the user with id=%s from friends", userId, friendId));
    }

    public List<User> getAllFriends(Integer userId) {
        return userStorage.getAllFriends(userId);
    }

    public List<User> getCommonFriends(Integer userId, Integer otherUserId) {
        return userStorage.getCommonFriends(userId, otherUserId);
    }
}

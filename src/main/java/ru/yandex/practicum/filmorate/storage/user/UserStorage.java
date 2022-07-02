package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {    // управление списком пользователей

    List<User> getAllUsers();

    void add(User user);

    void update(User user);

    Optional<User> getUserById(int id);

    void addFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);

    List<User> getAllFriends(int userId);

    List<User> getCommonFriends(int userId, int otherUserId);

    boolean doesUserExist(int userId);

    boolean doesFriendExist(int userId, int friendId);

    void deleteUserByIdStorage(int userId);
}

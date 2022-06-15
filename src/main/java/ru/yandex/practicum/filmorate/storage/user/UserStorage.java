package ru.yandex.practicum.filmorate.storage.user;


import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {    // управление списком пользователей

    public List<User> getAllUsers();
    public void add(User user);
    public void update(User user);
    public User getUserById(int id);
    public void addFriend(int userId, int friendId);
    public void deleteFriend(int userId, int friendId);
    public List<User> getAllFriends(int userId);
    public List<User> getCommonFriends(int userId, int otherUserId);
    public boolean doesUserExist(int userId);
    public default void checkName(User user) {    // проверяет -> name == null и пустое ли, и если да присваивает логин
        if (user.getName() == null || user.getName().isBlank()) user.setName(user.getLogin());
    }
    public default void validate(int userId) {
        if (!doesUserExist(userId)) throw new UserNotFoundException(String.format("User with id=%s not found", userId));
    }
}

package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {    // управление списком пользователей

    public List<User> getAllUsers();
    public void add(User user);
    public void update(User user);
    public Optional<User> getUserById(int id);
    public void addFriend(int userId, int friendId);
    public void deleteFriend(int userId, int friendId);
    public List<User> getAllFriends(int userId);
    public List<User> getCommonFriends(int userId, int otherUserId);
    public boolean doesUserExist(int userId);
    public boolean doesFriendExist(int userId, int friendId);
}

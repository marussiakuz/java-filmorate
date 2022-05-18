package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Integer userId, Integer friendId) {
        if (!userStorage.doesUserExist(userId) || !userStorage.doesUserExist(friendId))
            throw new UserNotFoundException();
        userStorage.getUserById(userId).addFriend(friendId);
        userStorage.getUserById(friendId).addFriend(userId);
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        if (!userStorage.doesUserExist(userId) || !userStorage.doesUserExist(friendId))
            throw new UserNotFoundException();
        userStorage.getUserById(userId).deleteFriend(friendId);
        userStorage.getUserById(friendId).deleteFriend(userId);
    }

    public List<User> getAllFriends(Integer userId) {
        if (!userStorage.doesUserExist(userId)) throw new UserNotFoundException();
        User user = userStorage.getUserById(userId);
        return user.getFriends().stream().map(userStorage::getUserById).collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Integer userId, Integer otherUserId) {
        if (!userStorage.doesUserExist(userId) || !userStorage.doesUserExist(otherUserId))
            throw new UserNotFoundException();
        User user = userStorage.getUserById(userId);
        User otherUser = userStorage.getUserById(otherUserId);
        List<Integer> common = new ArrayList<>(user.getFriends());
        common.retainAll(otherUser.getFriends());
        return common.stream().map(userStorage::getUserById).collect(Collectors.toList());
    }
}

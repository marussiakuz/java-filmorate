package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Integer, User> users = new HashMap<>();

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void add(User user) {
        if (doesUserExist(user.getId()))
            throw new UserAlreadyExistException(String.format("User with id=%s already exists", user.getId()));
        checkName(user);
        checkId(user);
        users.put(user.getId(), user);
    }

    @Override
    public void update(User user) {
        validate(user.getId());
        checkName(user);
        users.put(user.getId(), user);
    }

    @Override
    public User getUserById(int userId) {
        validate(userId);
        return users.get(userId);
    }

    @Override
    public void addFriend(int userId, int friendId) {
        validate(userId);
        validate(friendId);
        getUserById(userId).addFriend(friendId);
        getUserById(friendId).addFriend(userId);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        validate(userId);
        validate(friendId);
        getUserById(userId).deleteFriend(friendId);
        getUserById(friendId).deleteFriend(userId);
    }

    @Override
    public List<User> getAllFriends(int userId) {
        validate(userId);
        return getUserById(userId).getFriends().stream().map(this::getUserById).collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherUserId) {
        validate(userId);
        validate(otherUserId);
        User user = getUserById(userId);
        User otherUser = getUserById(otherUserId);
        List<Integer> common = new ArrayList<>(user.getFriends());
        common.retainAll(otherUser.getFriends());
        return common.stream().map(this::getUserById).collect(Collectors.toList());
    }

    @Override
    public boolean doesUserExist(int userId) {
        return users.containsKey(userId);
    }

    private void checkId(User user) {
        if (user.getId() == 0) {
            if (users.isEmpty()) user.setId(1);
            else {
                int maxId = users.keySet().stream().max(Comparator.naturalOrder()).get();
                user.setId(++maxId);
            }

        }
    }
}
